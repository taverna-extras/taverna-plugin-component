package net.sf.taverna.t2.component.registry.standard;

import static net.sf.taverna.t2.component.registry.standard.NewComponentRegistry.jaxbContext;
import static net.sf.taverna.t2.component.registry.standard.NewComponentRegistry.logger;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.sf.taverna.t2.component.api.RegistryException;
import net.sf.taverna.t2.component.registry.ComponentRegistry;
import net.sf.taverna.t2.component.registry.ComponentUtil;
import net.sf.taverna.t2.component.utils.AnnotationUtils;
import net.sf.taverna.t2.component.utils.SystemUtils;
import net.sf.taverna.t2.security.credentialmanager.CredentialManager;

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
			throws RegistryException {
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
