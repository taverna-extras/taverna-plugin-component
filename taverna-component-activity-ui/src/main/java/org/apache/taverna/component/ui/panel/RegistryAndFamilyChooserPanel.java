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

package org.apache.taverna.component.ui.panel;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.WEST;
import static org.apache.log4j.Logger.getLogger;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.Family;
import org.apache.taverna.component.api.Registry;
import org.apache.taverna.component.ui.preference.ComponentPreference;
import org.apache.taverna.lang.observer.Observable;
import org.apache.taverna.lang.observer.Observer;

/**
 * @author alanrw
 */
public class RegistryAndFamilyChooserPanel extends JPanel implements
		Observer<ProfileChoiceMessage>, Observable<FamilyChoiceMessage> {
	private static Logger logger = getLogger(RegistryAndFamilyChooserPanel.class);
	private static final long serialVersionUID = -535518473593617735L;
	final RegistryChooserPanel registryPanel;
	final FamilyChooserPanel familyPanel;

	public RegistryAndFamilyChooserPanel(ComponentPreference prefs) {
		super(new GridBagLayout());
		registryPanel = new RegistryChooserPanel(prefs);
		familyPanel = new FamilyChooserPanel(registryPanel);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = WEST;
		gbc.fill = BOTH;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		add(registryPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weighty = 1;
		add(familyPanel, gbc);
	}

	public Registry getChosenRegistry() {
		return registryPanel.getChosenRegistry();
	}

	public Family getChosenFamily() {
		return familyPanel.getChosenFamily();
	}

	@Override
	public void addObserver(Observer<FamilyChoiceMessage> observer) {
		familyPanel.addObserver(observer);
	}

	@Override
	public List<Observer<FamilyChoiceMessage>> getObservers() {
		return familyPanel.getObservers();
	}

	@Override
	public void removeObserver(Observer<FamilyChoiceMessage> observer) {
		familyPanel.removeObserver(observer);
	}

	@Override
	public void notify(Observable<ProfileChoiceMessage> sender,
			ProfileChoiceMessage message) {
		try {
			familyPanel.notify(sender, message);
		} catch (Exception e) {
			logger.error("problem handling notification about profile choice",
					e);
		}
	}
}
