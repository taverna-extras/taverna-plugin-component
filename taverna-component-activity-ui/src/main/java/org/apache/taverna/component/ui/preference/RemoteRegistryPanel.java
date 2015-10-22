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

package org.apache.taverna.component.ui.preference;

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * @author alanrw
 *
 */
public class RemoteRegistryPanel extends JPanel {
	private static final String LOCATION_LABEL = "Location:";
	private static final String NAME_LABEL = "Name:";
	private static final long serialVersionUID = 8833815753329010062L;

	private JTextField registryNameField = new JTextField(20);
	private JTextField locationField = new JTextField(20);
	
	public RemoteRegistryPanel() {
		super(new GridBagLayout());

		setBorder(new EmptyBorder(10, 10, 10, 10));
		
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.anchor = WEST;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.ipadx = 20;
		add(new JLabel(NAME_LABEL), constraints);

		constraints.gridx = 1;
		constraints.gridwidth = 2;
		constraints.ipadx = 0;
		constraints.weightx = 1d;
		constraints.fill = HORIZONTAL;
		add(registryNameField, constraints);
		
		constraints.gridy++;
		constraints.gridx = 0;
		constraints.ipadx = 20;
		constraints.fill = NONE;
		add(new JLabel(LOCATION_LABEL), constraints);
		
		constraints.gridx = 1;
		constraints.gridwidth = 2;
		constraints.ipadx = 0;
		constraints.weightx = 1d;
		constraints.fill = HORIZONTAL;
		add(locationField, constraints);
	}

	/**
	 * @return the registryNameField
	 */
	public JTextField getRegistryNameField() {
		return registryNameField;
	}

	/**
	 * @return the locationField
	 */
	public JTextField getLocationField() {
		return locationField;
	}

}
