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

package io.github.taverna_extras.component.ui.menu.registry;

import static io.github.taverna_extras.component.ui.menu.registry.ComponentRegistryMenuSection.COMPONENT_REGISTRY_SECTION;

import java.net.URI;

import javax.swing.Action;

import io.github.taverna_extras.component.ui.serviceprovider.ComponentServiceIcon;
import org.apache.taverna.ui.menu.AbstractMenuAction;
import org.apache.taverna.workbench.configuration.workbench.ui.T2ConfigurationFrame;

/**
 * @author alanrw
 */
public class ComponentRegistryManageMenuAction extends AbstractMenuAction {
	private static final URI COMPONENT_REGISTRY_MANAGE_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentRegistryManage");

	private T2ConfigurationFrame configFrame;
	private ComponentServiceIcon icon;

	public ComponentRegistryManageMenuAction() {
		super(COMPONENT_REGISTRY_SECTION, 100, COMPONENT_REGISTRY_MANAGE_URI);
	}

	public void setConfigurationFrame(T2ConfigurationFrame configFrame) {
		this.configFrame = configFrame;
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	protected Action createAction() {
		return new ComponentRegistryManageAction(configFrame, icon);
	}
}
