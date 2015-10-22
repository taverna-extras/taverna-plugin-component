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
import static org.apache.log4j.Logger.getLogger;
import static org.apache.taverna.component.ui.util.Utils.LONG_STRING;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import org.apache.taverna.component.api.Registry;
import org.apache.taverna.component.api.SharingPolicy;
import org.apache.taverna.lang.observer.Observable;
import org.apache.taverna.lang.observer.Observer;

/**
 * @author alanrw
 */
public class SharingPolicyChooserPanel extends JPanel {
	private static final String SHARING_LABEL = "Sharing policy:";
	private static final String READING_MSG = "Reading sharing policies";
	private static final String NO_PERMISSIONS_MSG = "No permissions available";
	private static final long serialVersionUID = 2175274929391537032L;
	private static final Logger logger = getLogger(SharingPolicyChooserPanel.class);

	private final JComboBox<String> permissionBox = new JComboBox<>();
	private final SortedMap<String, SharingPolicy> permissionMap = new TreeMap<>();
	private Registry registry;

	public SharingPolicyChooserPanel(RegistryChooserPanel registryPanel) {
		this();
		registryPanel.addObserver(new Observer<RegistryChoiceMessage>(){
			@Override
			public void notify(Observable<RegistryChoiceMessage> sender,
					RegistryChoiceMessage message) throws Exception {
				try {
					registry = message.getChosenRegistry();
					updateProfileModel();
				} catch (Exception e) {
					logger.error("problem when handling notification of registry", e);
				}
			}
		});
	}
	public SharingPolicyChooserPanel() {
		super();
		permissionBox.setPrototypeDisplayValue(LONG_STRING);
		this.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = WEST;
		gbc.fill = NONE;
		this.add(new JLabel(SHARING_LABEL), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.fill = BOTH;
		this.add(permissionBox, gbc);

		permissionBox.setEditable(false);
	}

	private void updateProfileModel() {
		permissionMap.clear();
		permissionBox.removeAllItems();
		permissionBox.addItem(READING_MSG);
		permissionBox.setEnabled(false);
		new SharingPolicyUpdater().execute();
	}

	public SharingPolicy getChosenPermission() {
		if (permissionBox.getSelectedIndex() < 0)
			return null;
		return permissionMap.get(permissionBox.getSelectedItem());
	}

	private class SharingPolicyUpdater extends SwingWorker<String, Object> {
		@Override
		protected String doInBackground() throws Exception {
			List<SharingPolicy> sharingPolicies;
			if (registry == null)
				return null;
			try {
				sharingPolicies = registry.getPermissions();
				if (sharingPolicies == null)
					return null;
			} catch (ComponentException e) {
				logger.error("problem getting permissions", e);
				throw e;
			} catch (NullPointerException e) {
				logger.error("null pointer getting permissions", e);
				throw e;
			}

			for (SharingPolicy policy : sharingPolicies)
				try {
					permissionMap.put(policy.getName(), policy);
				} catch (NullPointerException e) {
					logger.error("problem getting name of policy", e);
				}
			return null;
		}

		@Override
		protected void done() {
			permissionBox.removeAllItems();
			try {
				get();
				for (String name : permissionMap.keySet())
					permissionBox.addItem(name);
				if (!permissionMap.isEmpty()) {
					String firstKey = permissionMap.firstKey();
					permissionBox.setSelectedItem(firstKey);
				} else {
					permissionBox.addItem(NO_PERMISSIONS_MSG);
				}
			} catch (InterruptedException | ExecutionException e) {
				logger.error(e);
				permissionBox.addItem("Unable to read permissions");
			}
			permissionBox.setEnabled(!permissionMap.isEmpty());
		}
	}
}
