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

import static io.github.taverna_extras.component.registry.Harness.componentRegistry;
import static io.github.taverna_extras.component.registry.Harness.componentRegistryUrl;

import java.net.URL;

import io.github.taverna_extras.component.api.Component;
import io.github.taverna_extras.component.api.Family;
import io.github.taverna_extras.component.api.profile.Profile;
import io.github.taverna_extras.component.registry.standard.NewComponent;
import io.github.taverna_extras.component.registry.standard.NewComponentFamily;
import io.github.taverna_extras.component.registry.standard.NewComponentRegistry;
import io.github.taverna_extras.component.registry.standard.NewComponentRegistryFactory;

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
