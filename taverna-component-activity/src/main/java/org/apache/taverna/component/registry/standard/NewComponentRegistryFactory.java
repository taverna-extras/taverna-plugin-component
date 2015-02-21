package org.apache.taverna.component.registry.standard;

import static org.apache.taverna.component.registry.standard.NewComponentRegistry.jaxbContext;
import static org.apache.taverna.component.registry.standard.NewComponentRegistry.logger;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.sf.taverna.t2.security.credentialmanager.CredentialManager;

import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.registry.ComponentRegistry;
import org.apache.taverna.component.registry.ComponentUtil;
import org.apache.taverna.component.utils.AnnotationUtils;
import org.apache.taverna.component.utils.SystemUtils;
import org.springframework.beans.factory.annotation.Required;

public class NewComponentRegistryFactory {
	private final Map<String, NewComponentRegistry> componentRegistries = new HashMap<>();
	private CredentialManager cm;
	private ComponentUtil util;
	private SystemUtils system;
	private AnnotationUtils annUtils;

	@Required
	public void setCredentialManager(CredentialManager cm) {
		this.cm = cm;
	}

	@Required
	public void setComponentUtil(ComponentUtil util) {
		this.util = util;
	}

	@Required
	public void setSystemUtils(SystemUtils system) {
		this.system = system;
	}

	@Required
	public void setAnnotationUtils(AnnotationUtils annUtils) {
		this.annUtils = annUtils;
	}

	public synchronized ComponentRegistry getComponentRegistry(URL registryBase)
			throws ComponentException {
		if (!componentRegistries.containsKey(registryBase.toExternalForm())) {
			logger.debug("constructing registry instance for " + registryBase);
			componentRegistries.put(registryBase.toExternalForm(),
					new NewComponentRegistry(cm, registryBase, util, system,
							annUtils));
		}
		return componentRegistries.get(registryBase.toExternalForm());
	}

	public boolean verifyBase(URL registryBase) {
		try {
			return new Client(jaxbContext, registryBase, false, cm).verify();
		} catch (Exception e) {
			logger.info("failed to construct connection client to "
					+ registryBase, e);
			return false;
		}
	}
}
