package net.sf.taverna.t2.component.ui.serviceprovider;

import static java.util.Arrays.asList;
import static net.sf.taverna.t2.component.api.config.ComponentPropertyNames.COMPONENT_NAME;
import static net.sf.taverna.t2.component.api.config.ComponentPropertyNames.COMPONENT_VERSION;
import static net.sf.taverna.t2.component.api.config.ComponentPropertyNames.FAMILY_NAME;
import static net.sf.taverna.t2.component.api.config.ComponentPropertyNames.REGISTRY_BASE;
import static net.sf.taverna.t2.component.ui.ComponentConstants.ACTIVITY_URI;
import static org.apache.log4j.Logger.getLogger;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;

import net.sf.taverna.t2.component.api.ComponentException;
import net.sf.taverna.t2.component.api.ComponentFactory;
import net.sf.taverna.t2.component.api.Version;
import net.sf.taverna.t2.component.api.Version.ID;
import net.sf.taverna.t2.component.preference.ComponentPreference;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;

import org.apache.log4j.Logger;

import uk.org.taverna.scufl2.api.configurations.Configuration;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class ComponentServiceDesc extends ServiceDescription {
	private static Logger logger = getLogger(ComponentServiceDesc.class);

	private Version.ID identification;
	private ComponentPreference preference;
	private ComponentFactory factory;

	public ComponentServiceDesc(ComponentPreference preference,
			ComponentFactory factory, Version.ID identification) {
		this.preference = preference;
		this.factory = factory;
		this.identification = identification;
	}

	/**
	 * The configuration bean which is to be used for configuring the
	 * instantiated activity. This is built from the component identifier.
	 */
	@Override
	public Configuration getActivityConfiguration() {
		Configuration config = new Configuration();
		ObjectNode c = config.getJsonAsObjectNode();
		ID id = getIdentification();
		c.put(REGISTRY_BASE, id.getRegistryBase().toExternalForm());
		c.put(FAMILY_NAME, id.getFamilyName());
		c.put(COMPONENT_NAME, id.getComponentName());
		c.put(COMPONENT_VERSION, id.getComponentVersion());
		config.setJson(c);
		return config;
	}

	/**
	 * An icon to represent this service description in the service palette.
	 */
	@Override
	public Icon getIcon() {
		return ComponentServiceIcon.getIcon();
	}

	/**
	 * The display name that will be shown in service palette and will be used
	 * as a template for processor name when added to workflow.
	 */
	@Override
	public String getName() {
		return getIdentification().getComponentName();
	}

	/**
	 * The path to this service description in the service palette. Folders will
	 * be created for each element of the returned path.
	 */
	@Override
	public List<String> getPath() {
		return asList("Components",
				preference.getRegistryName(identification.getRegistryBase()),
				identification.getFamilyName());
	}

	/**
	 * Returns a list of data values uniquely identifying this component
	 * description (i.e., no duplicates).
	 */
	@Override
	protected List<? extends Object> getIdentifyingData() {
		return Arrays.asList(identification.getRegistryBase(),
				identification.getFamilyName(),
				identification.getComponentName());
	}

	@Override
	public String toString() {
		return "Component " + getName();
	}

	/**
	 * @return the identification
	 */
	public Version.ID getIdentification() {
		return identification;
	}

	/**
	 * @param identification
	 *            the identification to set
	 */
	public void setIdentification(Version.ID identification) {
		this.identification = identification;
	}
	
	public URL getHelpURL() {
		try {
			return factory.getVersion(getIdentification()).getHelpURL();
		} catch (ComponentException e) {
			logger.error(
					"failed to get component in order to determine its help URL",
					e);
			return null;
		}
	}

	@Override
	public URI getActivityType() {
		return ACTIVITY_URI;
	}
}
