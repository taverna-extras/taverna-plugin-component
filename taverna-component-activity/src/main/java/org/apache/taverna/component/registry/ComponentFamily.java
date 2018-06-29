package io.github.taverna_extras.component.registry;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.taverna_extras.component.api.Component;
import io.github.taverna_extras.component.api.ComponentException;
import io.github.taverna_extras.component.api.Registry;
import io.github.taverna_extras.component.api.Version;
import io.github.taverna_extras.component.api.profile.Profile;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;

/**
 * A ComponentFamily is a collection of Components that share the same
 * ComponentProfile.
 * 
 * @author David Withers
 */
public abstract class ComponentFamily implements
		io.github.taverna_extras.component.api.Family {
	private Registry parentRegistry;
	private String name;
	private String description;
	private Profile componentProfile;
	private ComponentUtil util;

	protected Map<String, Component> componentCache = new HashMap<>();

	public ComponentFamily(Registry componentRegistry, ComponentUtil util) {
		this.parentRegistry = componentRegistry;
		this.util = util;
	}

	@Override
	public Registry getComponentRegistry() {
		return parentRegistry;
	}

	@Override
	public final synchronized String getName() {
		if (name == null) {
			name = internalGetName();
		}
		return name;
	}

	protected abstract String internalGetName();

	@Override
	public final synchronized String getDescription() {
		if (description == null) {
			description = internalGetDescription();
		}
		return description;
	}

	protected abstract String internalGetDescription();

	@Override
	public final synchronized Profile getComponentProfile()
			throws ComponentException {
		if (componentProfile == null)
			componentProfile = internalGetComponentProfile();
		if (componentProfile == null) {
			Profile baseProfile = util.getBaseProfile();
			if (baseProfile != null) {
				return baseProfile;
			}
		}
		return componentProfile;
	}

	protected abstract Profile internalGetComponentProfile()
			throws ComponentException;

	@Override
	public final List<Component> getComponents() throws ComponentException {
		checkComponentCache();
		return new ArrayList<>(componentCache.values());
	}

	private void checkComponentCache() throws ComponentException {
		synchronized (componentCache) {
			if (componentCache.isEmpty())
				populateComponentCache();
		}
	}

	protected abstract void populateComponentCache() throws ComponentException;

	@Override
	public final Component getComponent(String componentName)
			throws ComponentException {
		checkComponentCache();
		return componentCache.get(componentName);
	}

	@Override
	public final Version createComponentBasedOn(String componentName,
			String description, WorkflowBundle bundle) throws ComponentException {
		if (componentName == null)
			throw new ComponentException("Component name must not be null");
		if (bundle == null)
			throw new ComponentException("workflow must not be null");
		checkComponentCache();
		if (componentCache.containsKey(componentName))
			throw new ComponentException("Component name already used");
		Version version = internalCreateComponentBasedOn(componentName,
				description, bundle);
		synchronized (componentCache) {
			Component c = version.getComponent();
			componentCache.put(componentName, c);
		}
		return version;
	}

	protected abstract Version internalCreateComponentBasedOn(
			String componentName, String description, WorkflowBundle bundle)
			throws ComponentException;

	@Override
	public final void removeComponent(Component component)
			throws ComponentException {
		if (component != null) {
			checkComponentCache();
			synchronized (componentCache) {
				componentCache.remove(component.getName());
			}
			internalRemoveComponent(component);
		}
	}

	protected abstract void internalRemoveComponent(Component component)
			throws ComponentException;

	@Override
	public void delete() throws ComponentException {
		getComponentRegistry().removeComponentFamily(this);
	}
}
