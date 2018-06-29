package io.github.taverna_extras.component.registry.standard;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import static io.github.taverna_extras.component.registry.standard.NewComponentRegistry.jaxbContext;
import static io.github.taverna_extras.component.registry.standard.NewComponentRegistry.logger;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import io.github.taverna_extras.component.api.ComponentException;
import io.github.taverna_extras.component.registry.ComponentRegistry;
import io.github.taverna_extras.component.registry.ComponentUtil;
import io.github.taverna_extras.component.utils.AnnotationUtils;
import io.github.taverna_extras.component.utils.SystemUtils;
import org.apache.taverna.security.credentialmanager.CredentialManager;
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
