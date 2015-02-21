package org.apache.taverna.component.activity;

import static org.apache.log4j.Logger.getLogger;
import static org.apache.taverna.component.api.config.ComponentPropertyNames.COMPONENT_NAME;
import static org.apache.taverna.component.api.config.ComponentPropertyNames.COMPONENT_VERSION;
import static org.apache.taverna.component.api.config.ComponentPropertyNames.FAMILY_NAME;
import static org.apache.taverna.component.api.config.ComponentPropertyNames.REGISTRY_BASE;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityPortsDefinitionBean;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.Version;
import org.apache.taverna.component.api.profile.ExceptionHandling;
import org.apache.taverna.component.registry.ComponentImplementationCache;
import org.apache.taverna.component.registry.ComponentUtil;
import org.apache.taverna.component.registry.ComponentVersionIdentification;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Component activity configuration bean.
 */
public class ComponentActivityConfigurationBean extends
		ComponentVersionIdentification implements Serializable {
	public static final String ERROR_CHANNEL = "error_channel";
	public static final List<String> ignorableNames = Arrays
			.asList(ERROR_CHANNEL);
	private static final long serialVersionUID = 5774901665863468058L;
	private static final Logger logger = getLogger(ComponentActivity.class);

	private transient ActivityPortsDefinitionBean ports = null;
	private transient ExceptionHandling eh;
	private transient ComponentUtil util;
	private transient ComponentImplementationCache cache;

	public ComponentActivityConfigurationBean(Version.ID toBeCopied,
			ComponentUtil util, ComponentImplementationCache cache) {
		super(toBeCopied);
		this.util = util;
		this.cache = cache;
		try {
			getPorts();
		} catch (org.apache.taverna.component.api.ComponentException e) {
			logger.error("failed to get component realization", e);
		}
	}

	public ComponentActivityConfigurationBean(JsonNode json,
			ComponentUtil util, ComponentImplementationCache cache) throws MalformedURLException {
		super(getUrl(json), getFamily(json), getComponent(json),
				getVersion(json));
		this.util = util;
		this.cache = cache;
	}

	private static URL getUrl(JsonNode json) throws MalformedURLException {
		return new URL(json.get(REGISTRY_BASE).textValue());
	}

	private static String getFamily(JsonNode json) {
		return json.get(FAMILY_NAME).textValue();
	}

	private static String getComponent(JsonNode json) {
		return json.get(COMPONENT_NAME).textValue();
	}

	private static Integer getVersion(JsonNode json) {
		JsonNode node = json.get(COMPONENT_VERSION);
		if (node == null || !node.isInt())
			return null;
		return node.intValue();
	}

	private ActivityPortsDefinitionBean getPortsDefinition(WorkflowBundle w) {
		ActivityPortsDefinitionBean result = new ActivityPortsDefinitionBean();
		List<ActivityInputPortDefinitionBean> inputs = result
				.getInputPortDefinitions();
		List<ActivityOutputPortDefinitionBean> outputs = result
				.getOutputPortDefinitions();

		for (InputWorkflowPort iwp : w.getMainWorkflow().getInputPorts())
			inputs.add(makeInputDefinition(iwp));
		for (OutputWorkflowPort owp : w.getMainWorkflow().getOutputPorts())
			outputs.add(makeOutputDefinition(0, owp.getName()));//FIXME

		try {
			eh = util.getFamily(getRegistryBase(), getFamilyName())
					.getComponentProfile().getExceptionHandling();
			if (eh != null)
				outputs.add(makeOutputDefinition(1, ERROR_CHANNEL));
		} catch (org.apache.taverna.component.api.ComponentException e) {
			logger.error("failed to get exception handling for family", e);
		}
		return result;
	}

	private ActivityInputPortDefinitionBean makeInputDefinition(
			InputWorkflowPort dip) {
		ActivityInputPortDefinitionBean activityInputPortDefinitionBean = new ActivityInputPortDefinitionBean();
		activityInputPortDefinitionBean.setHandledReferenceSchemes(null);
		activityInputPortDefinitionBean.setMimeTypes((List<String>) null);
		activityInputPortDefinitionBean.setTranslatedElementType(String.class);
		activityInputPortDefinitionBean.setAllowsLiteralValues(true);
		activityInputPortDefinitionBean.setDepth(dip.getDepth());
		activityInputPortDefinitionBean.setName(dip.getName());
		return activityInputPortDefinitionBean;
	}

	private ActivityOutputPortDefinitionBean makeOutputDefinition(int depth,
			String name) {
		ActivityOutputPortDefinitionBean activityOutputPortDefinitionBean = new ActivityOutputPortDefinitionBean();
		activityOutputPortDefinitionBean.setMimeTypes(new ArrayList<String>());
		activityOutputPortDefinitionBean.setDepth(depth);
		activityOutputPortDefinitionBean.setGranularDepth(depth);
		activityOutputPortDefinitionBean.setName(name);
		return activityOutputPortDefinitionBean;
	}

	/**
	 * @return the ports
	 */
	public ActivityPortsDefinitionBean getPorts() throws org.apache.taverna.component.api.ComponentException{
		if (ports == null)
			ports = getPortsDefinition(cache.getImplementation(this));
		return ports;
	}

	public ExceptionHandling getExceptionHandling() {
		return eh;
	}
}
