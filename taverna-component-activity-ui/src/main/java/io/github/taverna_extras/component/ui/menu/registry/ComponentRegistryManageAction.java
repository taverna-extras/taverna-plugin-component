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

import static io.github.taverna_extras.component.ui.preference.ComponentPreferenceUIFactory.DISPLAY_NAME;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import io.github.taverna_extras.component.ui.serviceprovider.ComponentServiceIcon;
import org.apache.taverna.workbench.configuration.workbench.ui.T2ConfigurationFrame;

//import net.sf.taverna.t2.workbench.configuration.workbench.ui.T2ConfigurationFrame;

/**
 * @author alanrw
 */
public class ComponentRegistryManageAction extends AbstractAction {
	private static final long serialVersionUID = 8993945811345164194L;
	private static final String MANAGE_REGISTRY = "Manage registries...";

	private final T2ConfigurationFrame configFrame;

	public ComponentRegistryManageAction(T2ConfigurationFrame configFrame,
			ComponentServiceIcon icon) {
		super(MANAGE_REGISTRY, icon.getIcon());
		this.configFrame = configFrame;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		configFrame.showConfiguration(DISPLAY_NAME);
	}
}
