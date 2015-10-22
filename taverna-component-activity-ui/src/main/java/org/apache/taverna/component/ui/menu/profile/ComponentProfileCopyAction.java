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
import org.apache.taverna.component.api.License;
import org.apache.taverna.component.api.Registry;
import org.apache.taverna.component.api.SharingPolicy;
import org.apache.taverna.component.api.profile.Profile;
import org.apache.taverna.component.ui.panel.LicenseChooserPanel;
import org.apache.taverna.component.ui.panel.ProfileChooserPanel;
import org.apache.taverna.component.ui.panel.RegistryChooserPanel;
import org.apache.taverna.component.ui.panel.SharingPolicyChooserPanel;
import org.apache.taverna.component.ui.preference.ComponentPreference;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;

/**
 * @author alanrw
 */
public class ComponentProfileCopyAction extends AbstractAction {
	private static final long serialVersionUID = 6332253931049645259L;
	private static final Logger log = getLogger(ComponentProfileCopyAction.class);
	private static final String COPY_PROFILE = "Copy profile...";

	private final ComponentPreference prefs;

	public ComponentProfileCopyAction(ComponentPreference prefs,
			ComponentServiceIcon iconProvider) {
		super(COPY_PROFILE, iconProvider.getIcon());
		this.prefs = prefs;
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		JPanel overallPanel = new JPanel();
		overallPanel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		RegistryChooserPanel sourceRegistryPanel = new RegistryChooserPanel(prefs);
		sourceRegistryPanel.setBorder(new TitledBorder("Source registry"));

		gbc.insets = new Insets(0, 5, 0, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		overallPanel.add(sourceRegistryPanel, gbc);

		ProfileChooserPanel profilePanel = new ProfileChooserPanel(sourceRegistryPanel);
		profilePanel.setBorder(new TitledBorder("Source profile"));

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weighty = 1;
		overallPanel.add(profilePanel, gbc);

		RegistryChooserPanel targetRegistryPanel = new RegistryChooserPanel(prefs);
		targetRegistryPanel.setBorder(new TitledBorder("Target registry"));
		gbc.gridy = 2;
		overallPanel.add(targetRegistryPanel, gbc);

		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridy++;
		SharingPolicyChooserPanel permissionPanel = new SharingPolicyChooserPanel(targetRegistryPanel);
		overallPanel.add(permissionPanel, gbc);

		gbc.gridy++;
		LicenseChooserPanel licensePanel = new LicenseChooserPanel();
		targetRegistryPanel.addObserver(licensePanel);
		overallPanel.add(licensePanel, gbc);

		int answer = showConfirmDialog(null, overallPanel,
				"Copy Component Profile", OK_CANCEL_OPTION);
		try {
			if (answer == OK_OPTION) 
				doCopy(sourceRegistryPanel.getChosenRegistry(),
						profilePanel.getChosenProfile(),
						targetRegistryPanel.getChosenRegistry(),
						permissionPanel.getChosenPermission(),
						licensePanel.getChosenLicense());
		} catch (ComponentException e) {
			log.error("failed to copy profile", e);
			showMessageDialog(null, "Unable to save profile: " + e.getMessage(),
					"Registry Exception", ERROR_MESSAGE);
		}
	}

	private void doCopy(Registry sourceRegistry, Profile sourceProfile,
			Registry targetRegistry, SharingPolicy permission, License license)
			throws ComponentException {
		if (sourceRegistry == null) {
			showMessageDialog(null, "Unable to determine source registry",
					"Component Registry Problem", ERROR_MESSAGE);
			return;
		}
		if (targetRegistry == null) {
			showMessageDialog(null, "Unable to determine target registry",
					"Component Registry Problem", ERROR_MESSAGE);
			return;
		}
		if (sourceRegistry.equals(targetRegistry)) {
			showMessageDialog(null, "Cannot copy to the same registry",
					"Copy Problem", ERROR_MESSAGE);
			return;
		}
		if (sourceProfile == null) {
			showMessageDialog(null, "Unable to determine source profile",
					"Component Profile Problem", ERROR_MESSAGE);
			return;
		}
		for (Profile p : targetRegistry.getComponentProfiles()) {
			if (p.getName().equals(sourceProfile.getName())) {
				showMessageDialog(null,
						"Target registry already contains a profile named "
								+ sourceProfile.getName(), "Copy Problem",
						ERROR_MESSAGE);
				return;
			}
			String sourceId = sourceProfile.getId();
			if (sourceId == null) {
				showMessageDialog(null,
						"Source profile \"" + sourceProfile.getName()
								+ "\" has no id ", "Copy Problem",
						ERROR_MESSAGE);
				return;
			}
			String id = p.getId();
			if (sourceId.equals(id)) {
				showMessageDialog(null,
						"Target registry already contains a profile with id "
								+ sourceId, "Copy Problem", ERROR_MESSAGE);
				return;
			}
		}
		targetRegistry.addComponentProfile(sourceProfile, license, permission);
	}
}
