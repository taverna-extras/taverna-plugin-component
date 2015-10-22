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
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.event.ItemEvent.SELECTED;
import static org.apache.log4j.Logger.getLogger;
import static org.apache.taverna.component.ui.util.Utils.LONG_STRING;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.License;
import org.apache.taverna.component.api.Registry;
import org.apache.taverna.lang.observer.Observable;
import org.apache.taverna.lang.observer.Observer;

/**
 * @author alanrw
 */
public class LicenseChooserPanel extends JPanel implements
		Observer<RegistryChoiceMessage> {
	private static final long serialVersionUID = 2175274929391537032L;
	private static final Logger logger = getLogger(LicenseChooserPanel.class);

	private JComboBox<String> licenseBox = new JComboBox<>();
	private SortedMap<String, License> licenseMap = new TreeMap<>();
	private Registry registry;

	public LicenseChooserPanel() {
		super();
		licenseBox.setPrototypeDisplayValue(LONG_STRING);
		this.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = WEST;
		gbc.fill = NONE;
		this.add(new JLabel("License:"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.fill = BOTH;
		this.add(licenseBox, gbc);

		licenseBox.setEditable(false);
		licenseBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == SELECTED)
					setLicense(licenseMap.get(licenseBox.getSelectedItem()));
			}
		});
	}

	protected void setLicense(License license) {
		if (license != null)
			licenseBox.setToolTipText("<html>" + license.getDescription()
					+ "</html>");
		else
			licenseBox.setToolTipText(null);
	}

	@Override
	public void notify(Observable<RegistryChoiceMessage> sender,
			RegistryChoiceMessage message) throws Exception {
		try {
			registry = message.getChosenRegistry();
			updateLicenseModel();
		} catch (Exception e) {
			logger.error("failure when handling license choice", e);
		}
	}

	private void updateLicenseModel() {
		licenseMap.clear();
		licenseBox.removeAllItems();
		licenseBox.setToolTipText(null);
		licenseBox.addItem("Reading licenses");
		licenseBox.setEnabled(false);
		new LicenseUpdater().execute();
	}

	public License getChosenLicense() {
		if (licenseBox.getSelectedIndex() < 0)
			return null;
		Object selectedItem = licenseBox.getSelectedItem();
		return licenseMap.get(selectedItem);
	}

	private class LicenseUpdater extends SwingWorker<String, Object> {
		@Override
		protected String doInBackground() throws Exception {
			List<License> licenses;
			if (registry == null)
				return null;
			try {
				licenses = registry.getLicenses();
				if (licenses == null)
					return null;
			} catch (ComponentException e) {
				logger.error("failure when reading licenses from registry", e);
				throw e;
			} catch (NullPointerException e) {
				logger.error("unexpected exception when reading licenses", e);
				throw e;
			}
			for (License license : licenses)
				try {
					String name = license.getName();
					licenseMap.put(name, license);
				} catch (NullPointerException e) {
					logger.error("could not get name of license", e);
				}
			return null;
		}

		@Override
		protected void done() {
			licenseBox.removeAllItems();
			try {
				get();
			} catch (InterruptedException | ExecutionException e1) {
				logger.error(e1);
				licenseBox.addItem("Unable to read licenses");
				licenseBox.setEnabled(false);
				return;
			}
			for (String name : licenseMap.keySet())
				licenseBox.addItem(name);
			if (licenseMap.isEmpty()) {
				licenseBox.addItem("No licenses available");
				licenseBox.setEnabled(false);
				return;
			}

			String firstKey = licenseMap.firstKey();
			License preferredLicense = null;
			try {
				preferredLicense = registry.getPreferredLicense();
			} catch (ComponentException e) {
				logger.error("failed to get preferred license", e);
			}
			if (preferredLicense != null) {
				licenseBox.setSelectedItem(preferredLicense.getName());
				setLicense(preferredLicense);
			} else {
				licenseBox.setSelectedItem(firstKey);
				setLicense(licenseMap.get(firstKey));
			}
			licenseBox.setEnabled(true);
		}
	}
}
