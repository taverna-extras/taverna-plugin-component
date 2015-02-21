package net.sf.taverna.t2.component.ui.serviceprovider;

import static net.sf.taverna.t2.component.api.config.ComponentPropertyNames.FAMILY_NAME;
import static net.sf.taverna.t2.component.api.config.ComponentPropertyNames.REGISTRY_BASE;
import static net.sf.taverna.t2.component.ui.serviceprovider.ComponentServiceProvider.providerId;

import java.net.URL;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import net.sf.taverna.t2.component.api.Family;
import net.sf.taverna.t2.component.api.Version;

public class ComponentServiceProviderConfig {
	private URL registryBase;
	private String familyName;

	public ComponentServiceProviderConfig() {
	}

	public ComponentServiceProviderConfig(Family family) {
		registryBase = family.getComponentRegistry().getRegistryBase();
		familyName = family.getName();
	}

	public ComponentServiceProviderConfig(Version.ID ident) {
		registryBase = ident.getRegistryBase();
		familyName = ident.getFamilyName();
	}

	/**
	 * @return the registryBase
	 */
	public URL getRegistryBase() {
		return registryBase;
	}

	/**
	 * @param registryBase
	 *            the registryBase to set
	 */
	public void setRegistryBase(URL registryBase) {
		this.registryBase = registryBase;
	}

	/**
	 * @return the familyName
	 */
	public String getFamilyName() {
		return familyName;
	}

	/**
	 * @param familyName
	 *            the familyName to set
	 */
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public Configuration getConfiguration() {
		Configuration c = new Configuration();
		c.getJsonAsObjectNode().put(REGISTRY_BASE,
				registryBase.toExternalForm());
		c.getJsonAsObjectNode().put(FAMILY_NAME, familyName);
		c.setType(providerId);
		return c;
	}
}
