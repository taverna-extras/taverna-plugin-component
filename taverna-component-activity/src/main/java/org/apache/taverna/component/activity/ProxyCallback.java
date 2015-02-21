/**
 * 
 */
package org.apache.taverna.component.activity;

import static net.sf.taverna.t2.reference.T2ReferenceType.ErrorDocument;
import static net.sf.taverna.t2.reference.T2ReferenceType.IdentifiedList;
import static net.sf.taverna.t2.reference.T2ReferenceType.ReferenceSet;
import static org.apache.log4j.Logger.getLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.ws.Holder;

import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.reference.ErrorDocument;
import net.sf.taverna.t2.reference.ErrorDocumentService;
import net.sf.taverna.t2.reference.IdentifiedList;
import net.sf.taverna.t2.reference.ListService;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.processor.activity.AsynchronousActivityCallback;
import net.sf.taverna.t2.workflowmodel.processor.dispatch.events.DispatchErrorType;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.profile.ExceptionHandling;
import org.apache.taverna.component.api.profile.ExceptionReplacement;
import org.apache.taverna.component.api.profile.HandleException;

/**
 * @author alanrw
 * 
 */
public class ProxyCallback implements AsynchronousActivityCallback {
	private static final Logger logger = getLogger(ProxyCallback.class);

	private final ComponentExceptionFactory cef;
	private final AsynchronousActivityCallback proxiedCallback;
	private final ReferenceService references;
	private final InvocationContext context;
	private final ExceptionHandling exceptionHandling;
	private final ListService lists;
	private final ErrorDocumentService errors;

	/**
	 * @param proxiedCallback
	 * @param invocationContext
	 * @param exceptionHandling
	 * @param exnFactory
	 */
	ProxyCallback(AsynchronousActivityCallback proxiedCallback,
			InvocationContext invocationContext,
			ExceptionHandling exceptionHandling,
			ComponentExceptionFactory exnFactory) {
		this.proxiedCallback = proxiedCallback;
		this.exceptionHandling = exceptionHandling;
		context = invocationContext;
		references = context.getReferenceService();
		lists = references.getListService();
		errors = references.getErrorDocumentService();
		cef = exnFactory;
	}

	@Override
	public InvocationContext getContext() {
		return context;
	}

	@Override
	public void requestRun(Runnable runMe) {
		proxiedCallback.requestRun(runMe);
	}

	@Override
	public void receiveResult(Map<String, T2Reference> data, int[] index) {
		if (exceptionHandling == null) {
			proxiedCallback.receiveResult(data, index);
			return;
		}

		List<T2Reference> exceptions = new ArrayList<>();
		Map<String, T2Reference> replacement = new HashMap<>();
		for (Entry<String, T2Reference> entry : data.entrySet())
			replacement.put(entry.getKey(),
					considerReference(entry.getValue(), exceptions));
		replacement.put("error_channel",
				references.register(exceptions, 1, true, context));
		proxiedCallback.receiveResult(replacement, index);
	}

	private T2Reference considerReference(T2Reference value,
			List<T2Reference> exceptions) {
		if (!value.containsErrors())
			return value;
		else if (!value.getReferenceType().equals(IdentifiedList))
			return replaceErrors(value, value.getDepth(), exceptions);
		else if (exceptionHandling.failLists())
			return replaceErrors(findFirstFailure(value), value.getDepth(),
					exceptions);

		List<T2Reference> replacementList = new ArrayList<>();
		for (T2Reference subValue : lists.getList(value))
			replacementList.add(considerReference(subValue, exceptions));
		return references.register(replacementList, value.getDepth(), true,
				context);
	}

	private T2Reference findFirstFailure(T2Reference value) {
		IdentifiedList<T2Reference> originalList = lists.getList(value);
		for (T2Reference subValue : originalList) {
			if (subValue.getReferenceType().equals(ErrorDocument))
				return subValue;
			if (subValue.getReferenceType().equals(IdentifiedList))
				if (subValue.containsErrors())
					return findFirstFailure(subValue);
			// No need to consider value
		}
		return null;
	}

	private T2Reference replaceErrors(T2Reference value, int depth,
			List<T2Reference> exceptions) {
		ErrorDocument doc = errors.getError(value);

		Holder<HandleException> handleException = new Holder<>();
		Set<ErrorDocument> toConsider = new HashSet<>();
		Set<ErrorDocument> considered = new HashSet<>();
		toConsider.add(doc);

		while (!toConsider.isEmpty())
			try {
				ErrorDocument nudoc = remapException(toConsider, considered,
						handleException);
				if (nudoc != null) {
					doc = nudoc;
					break;
				}
			} catch (Exception e) {
				logger.error("failed to locate exception mapping", e);
			}

		String exceptionMessage = doc.getExceptionMessage();
		// An exception that is not mentioned
		if (handleException.value == null) {
			ComponentImplementationException newException = cef
					.createUnexpectedComponentException(exceptionMessage);
			T2Reference replacement = errors.registerError(exceptionMessage,
					newException, depth, context).getId();
			exceptions.add(errors.registerError(exceptionMessage, newException,
					0, context).getId());
			return replacement;
		}

		if (handleException.value.pruneStack())
			doc.getStackTraceStrings().clear();

		ExceptionReplacement exnReplacement = handleException.value
				.getReplacement();
		if (exnReplacement == null) {
			T2Reference replacement = references.register(doc, depth, true,
					context);
			exceptions.add(references.register(doc, 0, true, context));
			return replacement;
		}

		ComponentImplementationException newException = cef
				.createComponentException(exnReplacement.getReplacementId(),
						exnReplacement.getReplacementMessage());
		T2Reference replacement = errors.registerError(
				exnReplacement.getReplacementMessage(), newException, depth,
				context).getId();
		exceptions.add(errors.registerError(
				exnReplacement.getReplacementMessage(), newException, 0,
				context).getId());
		return replacement;
	}

	private ErrorDocument remapException(Set<ErrorDocument> toConsider,
			Set<ErrorDocument> considered,
			Holder<HandleException> handleException) {
		ErrorDocument found = null;
		ErrorDocument errorDoc = toConsider.iterator().next();

		considered.add(errorDoc);
		toConsider.remove(errorDoc);
		String exceptionMessage = errorDoc.getExceptionMessage();
		for (HandleException he : exceptionHandling.getHandleExceptions()) {
			if (!he.matches(exceptionMessage))
				continue;
			handleException.value = he;
			found = errorDoc;
		}
		if (!errorDoc.getErrorReferences().isEmpty())
			for (T2Reference subRef : errorDoc.getErrorReferences())
				for (T2Reference newErrorRef : getErrors(subRef)) {
					ErrorDocument subDoc = errors.getError(newErrorRef);
					if (subDoc == null)
						logger.error("Error document contains references to non-existent sub-errors");
					else if (!considered.contains(subDoc))
						toConsider.add(subDoc);
				}
		return found;
	}

	private Set<T2Reference> getErrors(T2Reference ref) {
		Set<T2Reference> result = new HashSet<>();
		if (ref.getReferenceType().equals(ReferenceSet)) {
			// nothing
		} else if (ref.getReferenceType().equals(IdentifiedList)) {
			IdentifiedList<T2Reference> originalList = lists.getList(ref);
			for (T2Reference subValue : originalList)
				if (subValue.containsErrors())
					result.addAll(getErrors(subValue));
		} else
			result.add(ref);
		return result;
	}

	@Override
	public void receiveCompletion(int[] completionIndex) {
		proxiedCallback.receiveCompletion(completionIndex);
	}

	@Override
	public void fail(String message, Throwable t, DispatchErrorType errorType) {
		proxiedCallback.fail(message, t, errorType);
	}

	@Override
	public void fail(String message, Throwable t) {
		proxiedCallback.fail(message, t);
	}

	@Override
	public void fail(String message) {
		proxiedCallback.fail(message);
	}

	@Override
	public String getParentProcessIdentifier() {
		// return "";
		return proxiedCallback.getParentProcessIdentifier();
	}
}
