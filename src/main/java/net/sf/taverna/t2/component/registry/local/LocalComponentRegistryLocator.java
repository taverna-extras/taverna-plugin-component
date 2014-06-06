package net.sf.taverna.t2.component.registry.local;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import net.sf.taverna.t2.component.api.Registry;
import net.sf.taverna.t2.component.api.RegistryException;
import net.sf.taverna.t2.component.registry.ComponentUtil;

import org.springframework.beans.factory.annotation.Required;

public class LocalComponentRegistryLocator {
	private final Map<File, Registry> registries = new HashMap<>();
	private ComponentUtil util;

	@Required
	public void setComponentUtil(ComponentUtil util) {
		this.util = util;
	}

	public synchronized Registry getComponentRegistry(File registryDir)
			throws RegistryException {
		if (!registries.containsKey(registryDir))
			registries.put(registryDir, new LocalComponentRegistry(registryDir,
					util));
		return registries.get(registryDir);
	}

	public Registry getComponentRegistry(URL componentRegistryBase)
			throws RegistryException {
		@SuppressWarnings("deprecation")
		String hackedPath = URLDecoder.decode(componentRegistryBase.getPath());
		return getComponentRegistry(new File(hackedPath));
	}
}
