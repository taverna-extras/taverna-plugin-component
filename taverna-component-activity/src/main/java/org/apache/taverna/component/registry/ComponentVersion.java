package org.apache.taverna.component.registry;
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

import org.apache.taverna.component.api.Component;
import org.apache.taverna.component.api.ComponentException;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;

public abstract class ComponentVersion implements
		org.apache.taverna.component.api.Version {
	private Integer versionNumber;
	private String description;
	private Component component;

	protected ComponentVersion(Component component) {
		this.component = component;
	}

	@Override
	public final synchronized Integer getVersionNumber() {
		if (versionNumber == null)
			versionNumber = internalGetVersionNumber();
		return versionNumber;
	}

	protected abstract Integer internalGetVersionNumber();

	@Override
	public final synchronized String getDescription() {
		if (description == null)
			description = internalGetDescription();

		return description;
	}

	protected abstract String internalGetDescription();

	@Override
	public final synchronized WorkflowBundle getImplementation()
			throws ComponentException {
		// Cached in dataflow cache
		return internalGetImplementation();
	}

	protected abstract WorkflowBundle internalGetImplementation()
			throws ComponentException;

	@Override
	public final Component getComponent() {
		return component;
	}

	@Override
	public ID getID() {
		Component c = getComponent();
		return new ComponentVersionIdentification(c.getRegistry()
				.getRegistryBase(), c.getFamily().getName(), c.getName(),
				getVersionNumber());
	}
}
