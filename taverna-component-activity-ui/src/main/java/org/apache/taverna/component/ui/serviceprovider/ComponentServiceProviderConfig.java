/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package io.github.taverna_extras.component.ui.serviceprovider;

import static io.github.taverna_extras.component.api.config.ComponentPropertyNames.FAMILY_NAME;
import static io.github.taverna_extras.component.api.config.ComponentPropertyNames.REGISTRY_BASE;
import static io.github.taverna_extras.component.ui.serviceprovider.ComponentServiceProvider.providerId;

import java.net.URL;

import io.github.taverna_extras.component.api.Family;
import io.github.taverna_extras.component.api.Version;

import org.apache.taverna.scufl2.api.configurations.Configuration;

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
