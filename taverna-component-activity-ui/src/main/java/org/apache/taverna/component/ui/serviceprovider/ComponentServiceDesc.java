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

import static java.util.Arrays.asList;
import static org.apache.log4j.Logger.getLogger;
import static io.github.taverna_extras.component.api.config.ComponentPropertyNames.COMPONENT_NAME;
import static io.github.taverna_extras.component.api.config.ComponentPropertyNames.COMPONENT_VERSION;
import static io.github.taverna_extras.component.api.config.ComponentPropertyNames.FAMILY_NAME;
import static io.github.taverna_extras.component.api.config.ComponentPropertyNames.REGISTRY_BASE;
import static io.github.taverna_extras.component.ui.ComponentConstants.ACTIVITY_URI;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;

import org.apache.log4j.Logger;
import io.github.taverna_extras.component.api.ComponentException;
import io.github.taverna_extras.component.api.ComponentFactory;
import io.github.taverna_extras.component.api.Version;
import io.github.taverna_extras.component.api.Version.ID;
import io.github.taverna_extras.component.ui.preference.ComponentPreference;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.configurations.Configuration;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.taverna.servicedescriptions.ServiceDescription;

public class ComponentServiceDesc extends ServiceDescription {
	private static Logger logger = getLogger(ComponentServiceDesc.class);

	private Version.ID identification;
	private final ComponentPreference preference;
	private final ComponentFactory factory;
	private final ComponentServiceIcon iconProvider;

	public ComponentServiceDesc(ComponentPreference preference,
			ComponentFactory factory, ComponentServiceIcon iconProvider,
			Version.ID identification) {
		this.preference = preference;
		this.factory = factory;
		this.identification = identification;
		this.iconProvider = iconProvider;
	}

	/**
	 * The configuration bean which is to be used for configuring the
	 * instantiated activity. This is built from the component identifier.
	 */
	@Override
	public Configuration getActivityConfiguration() {
		Configuration config = new Configuration();
		installActivityConfiguration(config);
		return config;
	}

	/**
	 * Make the given activity be configured to be using the component that this
	 * class identifies.
	 */
	public void installActivityConfiguration(Activity activity) {
		installActivityConfiguration(activity.getConfiguration());
	}

	/**
	 * Update the given configuration to have the fields for the component that
	 * this class identifies.
	 */
	public void installActivityConfiguration(Configuration config) {
		ObjectNode c = config.getJsonAsObjectNode();
		ID id = getIdentification();
		c.put(REGISTRY_BASE, id.getRegistryBase().toExternalForm());
		c.put(FAMILY_NAME, id.getFamilyName());
		c.put(COMPONENT_NAME, id.getComponentName());
		c.put(COMPONENT_VERSION, id.getComponentVersion());
		config.setJson(c);
	}

	/**
	 * An icon to represent this service description in the service palette.
	 */
	@Override
	public Icon getIcon() {
		return iconProvider.getIcon();
	}

	/**
	 * The display name that will be shown in service palette and will be used
	 * as a template for processor name when added to workflow.
	 */
	@Override
	public String getName() {
		return getIdentification().getComponentName();
	}

	/**
	 * The path to this service description in the service palette. Folders will
	 * be created for each element of the returned path.
	 */
	@Override
	public List<String> getPath() {
		return asList("Components",
				preference.getRegistryName(identification.getRegistryBase()),
				identification.getFamilyName());
	}

	/**
	 * Returns a list of data values uniquely identifying this component
	 * description (i.e., no duplicates).
	 */
	@Override
	protected List<? extends Object> getIdentifyingData() {
		return Arrays.asList(identification.getRegistryBase(),
				identification.getFamilyName(),
				identification.getComponentName());
	}

	@Override
	public String toString() {
		return "Component " + getName();
	}

	/**
	 * @return the identification
	 */
	public Version.ID getIdentification() {
		return identification;
	}

	/**
	 * @param identification
	 *            the identification to set
	 */
	public void setIdentification(Version.ID identification) {
		this.identification = identification;
	}
	
	public URL getHelpURL() {
		try {
			return factory.getVersion(getIdentification()).getHelpURL();
		} catch (ComponentException e) {
			logger.error(
					"failed to get component in order to determine its help URL",
					e);
			return null;
		}
	}

	@Override
	public URI getActivityType() {
		return ACTIVITY_URI;
	}
}
