package org.apache.taverna.component.registry.local;
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

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.Registry;
import org.apache.taverna.component.registry.ComponentUtil;
import org.apache.taverna.component.utils.SystemUtils;
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
			throws ComponentException {
		if (!registries.containsKey(registryDir))
			registries.put(registryDir, new LocalComponentRegistry(registryDir,
					util, system));
		return registries.get(registryDir);
	}

	public Registry getComponentRegistry(URL componentRegistryBase)
			throws ComponentException {
		@SuppressWarnings("deprecation")
		String hackedPath = URLDecoder.decode(componentRegistryBase.getPath());
		return getComponentRegistry(new File(hackedPath));
	}
}
