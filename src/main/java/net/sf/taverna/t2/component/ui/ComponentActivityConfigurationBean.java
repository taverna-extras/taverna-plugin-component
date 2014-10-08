package net.sf.taverna.t2.component.ui;

import static net.sf.taverna.t2.component.api.config.ComponentPropertyNames.COMPONENT_NAME;
import static net.sf.taverna.t2.component.api.config.ComponentPropertyNames.COMPONENT_VERSION;
import static net.sf.taverna.t2.component.api.config.ComponentPropertyNames.FAMILY_NAME;
import static net.sf.taverna.t2.component.api.config.ComponentPropertyNames.REGISTRY_BASE;
import static org.apache.log4j.Logger.getLogger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.taverna.t2.component.api.ComponentException;
import net.sf.taverna.t2.component.api.ComponentFactory;
import net.sf.taverna.t2.component.api.Version;
import net.sf.taverna.t2.component.api.profile.ExceptionHandling;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityPortsDefinitionBean;

import org.apache.log4j.Logger;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Component activity configuration bean.
 */
public class ComponentActivityConfigurationBean extends Version.Identifier {
	public static final String ERROR_CHANNEL = "error_channel";
	public static final List<String> ignorableNames = Arrays
			.asList(ERROR_CHANNEL);
	private static final long serialVersionUID = 5774901665863468058L;
	private static final Logger logger = getLogger(ComponentActivityConfigurationBean.class);

	private ActivityPortsDefinitionBean ports = null;
	private ComponentFactory factory;
	private ExceptionHandling eh;

	public ComponentActivityConfigurationBean(Version.ID toBeCopied,
			ComponentFactory factory) {
		super(toBeCopied.getRegistryBase(), toBeCopied.getFamilyName(),
				toBeCopied.getComponentName(), toBeCopied.getComponentVersion());
		this.factory = factory;
		try {
			getPorts();
		} catch (ComponentException e) {
			logger.error("failed to get component realization", e);
		}
	}

	public ComponentActivityConfigurationBean(JsonNode json,
			ComponentFactory factory) throws MalformedURLException {
		super(getUrl(json), getFamily(json), getComponent(json),
				getVersion(json));
		this.factory = factory;
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

	public Version getVersion() throws ComponentException {
		return factory.getVersion(this);
	}

	private ActivityPortsDefinitionBean getPortsDefinition(WorkflowBundle w) {
		ActivityPortsDefinitionBean result = new ActivityPortsDefinitionBean();
		List<ActivityInputPortDefinitionBean> inputs = result
				.getInputPortDefinitions();
		List<ActivityOutputPortDefinitionBean> outputs = result
				.getOutputPortDefinitions();

		for (InputWorkflowPort iwp : w.getMainWorkflow().getInputPorts())
			inputs.add(makeInputDefinition(iwp));
		for (OutputWorkflowPort owp : w.getMainWorkflow().getOutputPorts()) {
			int depth = 0; //FIXME How to get the depth of an output?
			outputs.add(makeOutputDefinition(depth, owp.getName()));
		}

		try {
			eh = factory.getFamily(getRegistryBase(), getFamilyName())
					.getComponentProfile().getExceptionHandling();
			if (eh != null)
				outputs.add(makeOutputDefinition(1, ERROR_CHANNEL));
		} catch (net.sf.taverna.t2.component.api.ComponentException e) {
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
	public ActivityPortsDefinitionBean getPorts() throws ComponentException {
		if (ports == null)
			ports = getPortsDefinition(getVersion().getImplementation());
		return ports;
	}

	public ExceptionHandling getExceptionHandling() {
		return eh;
	}
}
