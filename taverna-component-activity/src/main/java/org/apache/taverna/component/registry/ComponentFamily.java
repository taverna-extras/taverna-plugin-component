/*******************************************************************************
 * Copyright (C) 2012 The University of Manchester
 *
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package org.apache.taverna.component.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.taverna.component.api.Component;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.Registry;
import org.apache.taverna.component.api.Version;
import org.apache.taverna.component.api.profile.Profile;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;

/**
 * A ComponentFamily is a collection of Components that share the same
 * ComponentProfile.
 * 
 * @author David Withers
 */
public abstract class ComponentFamily implements
		org.apache.taverna.component.api.Family {
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
