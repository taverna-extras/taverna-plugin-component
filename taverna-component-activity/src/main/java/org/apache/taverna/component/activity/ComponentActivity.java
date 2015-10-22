package org.apache.taverna.component.activity;

import static org.apache.log4j.Logger.getLogger;

import java.net.MalformedURLException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.profile.ExceptionHandling;
import org.apache.taverna.component.registry.ComponentImplementationCache;
import org.apache.taverna.component.registry.ComponentUtil;
import org.apache.taverna.component.utils.AnnotationUtils;
import org.apache.taverna.component.utils.SystemUtils;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.taverna.activities.dataflow.DataflowActivity;
import org.apache.taverna.annotation.annotationbeans.SemanticAnnotation;
import org.apache.taverna.invocation.InvocationContext;
import org.apache.taverna.invocation.impl.InvocationContextImpl;
import org.apache.taverna.platform.execution.api.InvalidWorkflowException;
import org.apache.taverna.reference.ReferenceService;
import org.apache.taverna.reference.T2Reference;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.EditException;
import org.apache.taverna.workflowmodel.Edits;
import org.apache.taverna.workflowmodel.processor.activity.AbstractAsynchronousActivity;
import org.apache.taverna.workflowmodel.processor.activity.ActivityConfigurationException;
import org.apache.taverna.workflowmodel.processor.activity.AsynchronousActivityCallback;
import org.apache.taverna.workflowmodel.utils.AnnotationTools;

public class ComponentActivity extends
		AbstractAsynchronousActivity<JsonNode> {
	public static final String URI = "http://ns.taverna.org.uk/2010/activity/component";
	private Logger logger = getLogger(ComponentActivity.class);

	private ComponentUtil util;
	private ComponentImplementationCache cache;
	private volatile DataflowActivity componentRealization;
	private JsonNode json;
	private ComponentActivityConfigurationBean bean;
	private SystemUtils system;
	private AnnotationUtils annUtils;
	private ComponentExceptionFactory cef;
	
	private Dataflow realizingDataflow = null;

	ComponentActivity(ComponentUtil util, ComponentImplementationCache cache,
			Edits edits, SystemUtils system, AnnotationUtils annUtils, ComponentExceptionFactory exnFactory) {
		this.util = util;
		this.cache = cache;
		this.system = system;
		this.annUtils = annUtils;
		setEdits(edits);
		this.componentRealization = new DataflowActivity();
		this.cef = exnFactory;
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
		} catch (ComponentException e) {
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
					callback, callback.getContext(), exceptionHandling, cef));
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

	private DataflowActivity getComponentRealization()
			throws ActivityConfigurationException {
		synchronized (componentRealization) {
			try {
				if (componentRealization.getNestedDataflow() == null) {
					if (realizingDataflow == null)
						realizingDataflow = system.compile(util
								.getVersion(bean).getImplementation());
					componentRealization.setNestedDataflow(realizingDataflow);
					copyAnnotations();
				}
			} catch (ComponentException e) {
				logger.error("unable to read workflow", e);
				throw new ActivityConfigurationException(
						"unable to read workflow", e);
			} catch (InvalidWorkflowException e) {
				logger.error("unable to compile workflow", e);
				throw new ActivityConfigurationException(
						"unable to compile workflow", e);
			}
		}
		
		return componentRealization;
	}

	private void copyAnnotations() {
		// FIXME Completely wrong way of doing this!
		try {
			//annUtils.getAnnotation(subject, uriForAnnotation)
			String annotationValue = AnnotationTools.getAnnotationString(realizingDataflow,
					SemanticAnnotation.class, null);
			if (annotationValue != null)
				AnnotationTools.setAnnotationString(this, SemanticAnnotation.class,
						annotationValue, getEdits()).doEdit();
		} catch (EditException e) {
			logger.error("failed to set annotation string", e);
		}
	}
}
