/**
 * 
 */
package org.apache.taverna.component.ui.view;

import static java.lang.String.format;
import static org.apache.log4j.Logger.getLogger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.Component;
import org.apache.taverna.component.api.ComponentFactory;
import org.apache.taverna.component.api.Family;
import org.apache.taverna.component.api.Version;
import org.apache.taverna.component.ui.ComponentActivityConfigurationBean;

import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;

/**
 * @author alanrw
 */
public class ViewUtil {
	private static Logger logger = getLogger(ViewUtil.class);

	private static final String VERSION_DESCRIPTION_LABEL = "Component version description";
	private static final String COMPONENT_DESCRIPTION_LABEL = "Component description";
	private static final String FAMILY_DESCRIPTION_LABEL = "Family description";

	private static final String plainFormat = "<tr><td><b>%1$s</b></td><td nowrap=\"wrap\" style=\"width:100px;\">%2$s</td></tr>";
	private static final String headerFormat = "<tr><th>%1$s</th><th>%2$s</th></tr>";
	private static final String rowFormat = "<tr><td><b>%1$s</b></td><td>%2$s</td></tr>";
	private static final String rowLinkFormat = "<tr><td><b>%1$s</b></td><td><a href=\"%3$s\">%2$s</a></td></tr>";
	private static final String descriptionFormat = "<tr><td colspan=\"2\"><b>%1$s</b></td></tr><tr><td colspan=\"2\" nowrap=\"wrap\" style=\"width:100px;\">%2$s</td></tr>";

	private ComponentFactory factory;//FIXME beaninject

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
	}

	public String getRawTablesHtml(Version.ID id) {
		StringBuilder html = new StringBuilder();
		getRawTablesHtml(id, html);
		return html.toString();
	}

	public String getRawTablesHtml(Configuration config) throws MalformedURLException {
		StringBuilder html = new StringBuilder();
		getRawTablesHtml(
				new ComponentActivityConfigurationBean(
						config.getJsonAsObjectNode(), factory), html);
		return html.toString();
	}

	public void getRawTablesHtml(Version.ID id, StringBuilder html) {
		URL registryBase = id.getRegistryBase();
		String registryLink = null;
		if (registryBase.getProtocol().startsWith("http"))
			registryLink = registryBase.toExternalForm();
		/*
		 * \u200b is a zero-width space, so the HTML renderer can know to break
		 * lines.
		 */
		String registryName = registryBase.toString().replaceAll("/", "\u200b/");
		appendRow(html, "Component registry base", registryName, registryLink);

		String familyName = id.getFamilyName();
		appendRow(html, "Component family", familyName, null);
		try {
			Family family = factory.getFamily(registryBase, familyName);
			if (family != null)
				appendDescriptionHtml(html, FAMILY_DESCRIPTION_LABEL,
						family.getDescription());
		} catch (Exception e) {
			logger.error("failed to get component family description", e);
		}

		String componentName = id.getComponentName();
		String helpLink = null;
		try {
			URL helpURL = factory.getVersion(id).getHelpURL();
			if (helpURL != null)
				helpLink = helpURL.toExternalForm();
		} catch (Exception e) {
			logger.error(e);
		}

		appendRow(html, "Component name", componentName, helpLink);
		try {
			Component component = factory.getComponent(registryBase,
					familyName, componentName);
			if (component != null)
				appendDescriptionHtml(html, COMPONENT_DESCRIPTION_LABEL,
						component.getDescription());
		} catch (Exception e) {
			logger.error("failed to get component description", e);
		}

		Integer componentVersion = id.getComponentVersion();

		if (componentVersion == null)
			appendRow(html, "Component version", "N/A", helpLink);
		else {
			appendRow(html, "Component version", componentVersion, helpLink);
			try {
				Version version = factory.getVersion(registryBase,
						familyName, componentName, componentVersion);
				if (version != null) {
					appendDescriptionHtml(html, VERSION_DESCRIPTION_LABEL,
							version.getDescription());
					WorkflowBundle impl = version.getImplementation();
					Set<InputWorkflowPort> inputs = impl.getMainWorkflow().getInputPorts();
					if (!inputs.isEmpty()) {
						appendHeaderRow(html, "Input Port Name", "Depth");
						for (InputWorkflowPort input : inputs)
							appendPlainRow(html, input.getName(), input.getDepth());
					}
					Set<OutputWorkflowPort> outputs = impl.getMainWorkflow().getOutputPorts();
					if (!outputs.isEmpty()) {
						appendHeaderRow(html, "Output Port Name", "Depth");
						for (OutputWorkflowPort output : outputs) {
							//FIXME get depth of output ports!
							appendPlainRow(html, output.getName(), -1 /*output.getDepth()*/);
						}
					}
				}
			} catch (Exception e) {
				logger.error("failed to get component version description", e);
			}
		}
	}

	private static void appendRow(StringBuilder html, Object label,
			Object value, String link) {
		if (link == null)
			html.append(format(rowFormat, label, value));
		else
			html.append(format(rowLinkFormat, label, value, link));
	}

	private static void appendHeaderRow(StringBuilder html, Object label1,
			Object label2) {
		html.append(format(headerFormat, label1, label2));
	}

	private static void appendPlainRow(StringBuilder html, Object value1,
			Object value2) {
		html.append(format(plainFormat, value1, value2));
	}

	private static void appendDescriptionHtml(StringBuilder html,
			String header, String description) {
		if ((description != null) && !description.isEmpty())
			html.append(format(descriptionFormat, header, description));
	}
}
