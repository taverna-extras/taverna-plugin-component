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

package org.apache.taverna.component.ui.menu.profile;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.WEST;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.apache.log4j.Logger.getLogger;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.profile.Profile;
import org.apache.taverna.component.ui.panel.ProfileChooserPanel;
import org.apache.taverna.component.ui.panel.RegistryChooserPanel;
import org.apache.taverna.component.ui.preference.ComponentPreference;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;

/**
 * @author alanrw
 */
public class ComponentProfileDeleteAction extends AbstractAction {
	private static final long serialVersionUID = -5697971204434020559L;
	private static final Logger log = getLogger(ComponentProfileDeleteAction.class);
	private static final String DELETE_PROFILE = "Delete profile...";

	private final ComponentPreference prefs;

	public ComponentProfileDeleteAction(ComponentPreference prefs,
			ComponentServiceIcon icon) {
		super(DELETE_PROFILE, icon.getIcon());
		// FIXME Should we switch this on?
		setEnabled(false);
		this.prefs = prefs;
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		JPanel overallPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		RegistryChooserPanel registryPanel = new RegistryChooserPanel(prefs);
		registryPanel.setBorder(new TitledBorder("Registry"));

		gbc.insets = new Insets(0, 5, 0, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = WEST;
		gbc.fill = BOTH;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		overallPanel.add(registryPanel, gbc);

		ProfileChooserPanel profilePanel = new ProfileChooserPanel(
				registryPanel);
		profilePanel.setBorder(new TitledBorder("Profile"));

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weighty = 1;
		overallPanel.add(profilePanel, gbc);

		int answer = showConfirmDialog(null, overallPanel,
				"Delete Component Profile", OK_CANCEL_OPTION);
		try {
			if (answer == OK_OPTION)
				doDelete(profilePanel.getChosenProfile());
		} catch (ComponentException e) {
			log.error("failed to delete profile", e);
			showMessageDialog(null,
					"Unable to delete profile: " + e.getMessage(),
					"Registry Exception", ERROR_MESSAGE);
		}
	}

	private void doDelete(Profile profile) throws ComponentException {
		if (profile == null) {
			showMessageDialog(null, "Unable to determine profile",
					"Component Profile Problem", ERROR_MESSAGE);
			return;
		}
		profile.delete();
	}
}
