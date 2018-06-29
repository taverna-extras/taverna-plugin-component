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

package io.github.taverna_extras.component.ui.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import io.github.taverna_extras.component.api.Family;
import io.github.taverna_extras.component.api.Registry;
import io.github.taverna_extras.component.api.Version;
import io.github.taverna_extras.component.ui.preference.ComponentPreference;

/**
 * @author alanrw
 */
public class RegistryAndFamilyChooserComponentEntryPanel extends JPanel {
	private static final long serialVersionUID = -6675545311458594678L;
	private static final String T2FLOW = ".t2flow";
	private static final String WFBUNDLE = ".wfbundle";

	private JTextField componentNameField = new JTextField(20);
	private RegistryAndFamilyChooserPanel registryAndFamilyChooserPanel;

	public RegistryAndFamilyChooserComponentEntryPanel(ComponentPreference prefs) {
		super(new GridBagLayout());
		registryAndFamilyChooserPanel = new RegistryAndFamilyChooserPanel(prefs);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		this.add(registryAndFamilyChooserPanel, gbc);
		gbc.gridy = 1;

		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		this.add(new JLabel("Component name:"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(componentNameField, gbc);
	}

	public String getComponentName() {
		return componentNameField.getText();
	}

	public void setComponentName(String name) {
		componentNameField.setText(name);
	}

	public Version.ID getComponentVersionIdentification() {
		String componentName = getComponentName();
		Family familyChoice = registryAndFamilyChooserPanel.getChosenFamily();
		Registry registry = registryAndFamilyChooserPanel.getChosenRegistry();

		if (familyChoice == null || registry == null || componentName == null
				|| componentName.isEmpty())
			return null;

		return new Version.Identifier(registry.getRegistryBase(),
				familyChoice.getName(), trim(componentName), -1);
	}

	private static String trim(String name) {
		if (name.endsWith(WFBUNDLE))
			return name.substring(0, name.length() - WFBUNDLE.length());
		else if (name.endsWith(T2FLOW))
			return name.substring(0, name.length() - T2FLOW.length());
		return name;
	}
}
