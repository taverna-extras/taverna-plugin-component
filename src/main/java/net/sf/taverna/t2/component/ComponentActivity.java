package net.sf.taverna.t2.component;

import static net.sf.taverna.t2.workflowmodel.utils.AnnotationTools.getAnnotationString;
import static net.sf.taverna.t2.workflowmodel.utils.AnnotationTools.setAnnotationString;
import static org.apache.log4j.Logger.getLogger;

import java.net.MalformedURLException;
import java.util.Map;

import net.sf.taverna.t2.activities.dataflow.DataflowActivity;
import net.sf.taverna.t2.annotation.annotationbeans.SemanticAnnotation;
import net.sf.taverna.t2.component.api.RegistryException;
import net.sf.taverna.t2.component.profile.ExceptionHandling;
import net.sf.taverna.t2.component.registry.ComponentDataflowCache;
import net.sf.taverna.t2.component.registry.ComponentUtil;
import net.sf.taverna.t2.invocation.InvocationContext;
import net.sf.taverna.t2.invocation.impl.InvocationContextImpl;
import net.sf.taverna.t2.reference.ReferenceService;
import net.sf.taverna.t2.reference.T2Reference;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.processor.activity.AbstractAsynchronousActivity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException;
import net.sf.taverna.t2.workflowmodel.processor.activity.AsynchronousActivityCallback;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

public class ComponentActivity extends
		AbstractAsynchronousActivity<JsonNode> {
	public static final String URI = "http://ns.taverna.org.uk/2010/activity/component";
	private Logger logger = getLogger(ComponentActivity.class);

	private ComponentUtil util;
	private ComponentDataflowCache cache;
	private volatile DataflowActivity componentRealization;
	private JsonNode json;
	private ComponentActivityConfigurationBean bean;
	
	private Dataflow realizingDataflow = null;

	ComponentActivity(ComponentUtil util, ComponentDataflowCache cache, Edits edits) {
		this.util = util;
		this.cache = cache;
		setEdits(edits);
		this.componentRealization = new DataflowActivity();
	}

	@Override
	public void configure(JsonNode json) throws ActivityConfigurationException {
		this.json = json;
		try {
			bean = new ComponentActivityConfigurationBean(json, util, cache);
		} catch (MalformedURLException e) {
			throw new ActivityConfigurationException(
					"failed to understand configuration", e);
		}
		try {
			configurePorts(bean.getPorts());
		} catch (RegistryException e) {
			throw new ActivityConfigurationException(
					"failed to get component realization", e);
		}
	}

	@Override
	public void executeAsynch(Map<String, T2Reference> inputs,
			AsynchronousActivityCallback callback) {
		try {
			ExceptionHandling exceptionHandling = bean.getExceptionHandling();
			// InvocationContextImpl newContext = copyInvocationContext(callback);

			getComponentRealization().executeAsynch(inputs, new ProxyCallback(
					callback, callback.getContext(), exceptionHandling));
		} catch (ActivityConfigurationException e) {
			callback.fail("Unable to execute component", e);
		}
	}

	@SuppressWarnings("unused")
	private InvocationContextImpl copyInvocationContext(
			AsynchronousActivityCallback callback) {
		InvocationContext originalContext = callback.getContext();
		ReferenceService rs = originalContext.getReferenceService();
		InvocationContextImpl newContext = new InvocationContextImpl(rs, null);
		// for (Object o : originalContext.getEntities(Object.class)) {
		// newContext.addEntity(o);
		// }
		return newContext;
	}

	@Override
	public JsonNode getConfiguration() {
		return json;
	}

	ComponentActivityConfigurationBean getConfigBean() {
		return bean;
	}

	public DataflowActivity getComponentRealization()
			throws ActivityConfigurationException {
		synchronized (componentRealization) {
			if (componentRealization.getConfiguration() == null) {
				try {
					componentRealization.setNestedDataflow(getImplementationDataflow());
				} catch (RegistryException e1) {
					logger.error("Unable to read dataflow", e1);
					throw new ActivityConfigurationException("Unable to read dataflow", e1);
				}

				copyAnnotations();
			}
			return componentRealization;
		}
	}

	private void copyAnnotations() {
		// FIXME Only copies a SemanticAnnotation itself
		try {
			String annotationValue = getAnnotationString(realizingDataflow,
					SemanticAnnotation.class, null);
			if (annotationValue != null)
				setAnnotationString(this, SemanticAnnotation.class,
						annotationValue, edits).doEdit();
		} catch (EditException e) {
			logger.error("failed to set annotation string", e);
		}
	}

	//@Override
	public Dataflow getNestedDataflow() {
		try {
			return getImplementationDataflow();
		} catch (RegistryException e) {
			logger.error("failed to get component realization", e);
			return null;
		}
	}

	private Dataflow getImplementationDataflow() throws RegistryException {
		if (realizingDataflow == null)
			realizingDataflow = util.getVersion(bean).getDataflow();
		return realizingDataflow;
	}
}
