package org.apache.taverna.component.registry.standard;
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

import static org.apache.taverna.component.utils.SystemUtils.getElementString;

import java.util.List;

import org.apache.taverna.component.api.Component;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.Version;
import org.apache.taverna.component.api.profile.Profile;
import org.apache.taverna.component.registry.ComponentFamily;
import org.apache.taverna.component.registry.ComponentUtil;
import org.apache.taverna.component.registry.api.ComponentFamilyType;
import org.apache.taverna.component.registry.api.Description;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;

/**
 * A family of components in the new-interface registry.
 * 
 * @author Donal Fellows
 */
class NewComponentFamily extends ComponentFamily {
	static final String ELEMENTS = "title,description";

	private final NewComponentRegistry registry;
	private final NewComponentProfile profile;
	private final String id;
	private final String name;
	private final String description;
	private final String uri;
	private final String resource;

	NewComponentFamily(NewComponentRegistry componentRegistry,
			NewComponentProfile profile, Description familyDesc,
			ComponentUtil util) throws ComponentException {
		super(componentRegistry, util);
		uri = familyDesc.getUri();
		registry = componentRegistry;
		this.profile = profile;
		id = familyDesc.getId().trim();
		name = getElementString(familyDesc, "title");
		description = getElementString(familyDesc, "description");
		resource = familyDesc.getResource();
	}

	public NewComponentFamily(NewComponentRegistry componentRegistry,
			NewComponentProfile profile, ComponentFamilyType cft,
			ComponentUtil util) {
		super(componentRegistry, util);
		uri = cft.getUri();
		registry = componentRegistry;
		this.profile = profile;
		id = cft.getId();
		name = cft.getTitle();
		description = cft.getDescription();
		resource = cft.getResource();
	}

	@Override
	protected String internalGetName() {
		return name;
	}

	@Override
	protected String internalGetDescription() {
		return description;
	}

	@Override
	protected Profile internalGetComponentProfile() throws ComponentException {
		return profile;
	}

	public List<Component> getMemberComponents() throws ComponentException {
		return registry.listComponents(this);
	}

	@Override
	protected void populateComponentCache() throws ComponentException {
		for (Component c : getMemberComponents()) {
			NewComponent component = (NewComponent) c;
			componentCache.put(component.getName(), component);
		}
	}

	@Override
	protected Version internalCreateComponentBasedOn(String componentName,
			String description, WorkflowBundle bundle) throws ComponentException {
		if (componentName == null)
			componentName = registry.annUtils.getTitle(bundle, "Untitled");
		if (description == null)
			componentName = registry.annUtils.getDescription(bundle,
					"Undescribed");
		return registry.createComponentFrom(this, componentName, description,
				bundle, registry.getPreferredLicense(),
				registry.getDefaultSharingPolicy());
	}

	@Override
	protected void internalRemoveComponent(Component component)
			throws ComponentException {
		registry.deleteComponent((NewComponent) component);
	}

	String getId() {
		return id;
	}

	public String getUri() {
		return uri;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof NewComponentFamily) {
			NewComponentFamily other = (NewComponentFamily) o;
			return registry.equals(other.registry) && id.equals(other.id);
		}
		return false;
	}

	private static final int BASEHASH = NewComponentFamily.class.hashCode();

	@Override
	public int hashCode() {
		return BASEHASH ^ registry.hashCode() ^ id.hashCode();
	}

	public String getResourceLocation() {
		return resource;
	}
}
