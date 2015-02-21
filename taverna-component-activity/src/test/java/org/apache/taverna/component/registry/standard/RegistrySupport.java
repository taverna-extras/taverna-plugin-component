package org.apache.taverna.component.registry.standard;

import static org.apache.taverna.component.registry.Harness.componentRegistry;
import static org.apache.taverna.component.registry.Harness.componentRegistryUrl;

import java.net.URL;

import org.apache.taverna.component.api.Component;
import org.apache.taverna.component.api.Family;
import org.apache.taverna.component.api.profile.Profile;
import org.apache.taverna.component.registry.standard.NewComponent;
import org.apache.taverna.component.registry.standard.NewComponentFamily;
import org.apache.taverna.component.registry.standard.NewComponentRegistry;
import org.apache.taverna.component.registry.standard.NewComponentRegistryFactory;

class RegistrySupport {
	static final String DEPLOYMENT = "http://aeon.cs.man.ac.uk:3006";
	static final NewComponentRegistryFactory factory = new NewComponentRegistryFactory();// FIXME

	public static void pre() throws Exception {
		componentRegistryUrl = new URL(DEPLOYMENT);
		componentRegistry = factory.getComponentRegistry(componentRegistryUrl);
	}

	public static void post() throws Exception {
		NewComponentRegistry registry = (NewComponentRegistry) factory
				.getComponentRegistry(componentRegistryUrl);
		for (Profile p : registry.getComponentProfiles())
			registry.client.delete("/file.xml", "id=" + p.getId());
		for (Family f : registry.getComponentFamilies()) {
			for (Component c : f.getComponents())
				registry.deleteComponent((NewComponent) c);
			registry.client.delete("/pack.xml", "id="
					+ ((NewComponentFamily) f).getId());
		}
	}
}
