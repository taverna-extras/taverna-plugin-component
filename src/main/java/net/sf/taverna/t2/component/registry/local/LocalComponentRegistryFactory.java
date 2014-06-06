package net.sf.taverna.t2.component.registry.local;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import net.sf.taverna.t2.component.api.Registry;
import net.sf.taverna.t2.component.api.RegistryException;
import net.sf.taverna.t2.component.registry.ComponentUtil;
import net.sf.taverna.t2.component.utils.SystemUtils;

import org.springframework.beans.factory.annotation.Required;

public class LocalComponentRegistryFactory {
	private final Map<File, Registry> registries = new HashMap<>();
	private ComponentUtil util;
	private SystemUtils system;

	@Required
	public void setComponentUtil(ComponentUtil util) {
		this.util = util;
	}

	@Required
	public void setSystemUtils(SystemUtils system) {
		this.system = system;
	}

	public synchronized Registry getComponentRegistry(File registryDir)
			throws RegistryException {
		if (!registries.containsKey(registryDir))
			registries.put(registryDir, new LocalComponentRegistry(registryDir,
					util, system));
		return registries.get(registryDir);
	}

	public Registry getComponentRegistry(URL componentRegistryBase)
			throws RegistryException {
		@SuppressWarnings("deprecation")
		String hackedPath = URLDecoder.decode(componentRegistryBase.getPath());
		return getComponentRegistry(new File(hackedPath));
	}
}
