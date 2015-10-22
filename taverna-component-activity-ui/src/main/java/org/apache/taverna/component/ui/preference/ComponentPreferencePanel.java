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

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static org.apache.log4j.Logger.getLogger;
import static org.apache.taverna.component.ui.util.Utils.URL_PATTERN;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.ComponentFactory;
import org.apache.taverna.component.api.Registry;
import org.apache.taverna.lang.ui.DeselectingButton;
import org.apache.taverna.lang.ui.ValidatingUserInputDialog;
import org.apache.taverna.workbench.helper.Helper;

public class ComponentPreferencePanel extends JPanel {
	private static final String BAD_URL_MESSAGE = "Invalid URL";
	private static final String SET_URL_MESSAGE = "Set the URL of the profile";
	private static final String HELP_LABEL = "Help";
	private static final String RESET_LABEL = "Reset";
	private static final String APPLY_LABEL = "Apply";
	private static final String ADD_REMOTE_TITLE = "Add Remote Component Registry";
	private static final String ADD_LOCAL_TITLE = "Add Local Component Registry";
	private static final String ADD_REMOTE_LABEL = "Add remote registry";
	private static final String ADD_LOCAL_LABEL = "Add local registry";
	private static final String REMOVE_LABEL = "Remove registry";
	private static final String TITLE = "Component registry management";
	private static final String VALIDATION_MESSAGE = "Set the registry name";
	private static final String EXCEPTION_MESSAGE = "Unable to access registry at ";
	private static final String EXCEPTION_TITLE = "Component registry problem";
	private static final String INVALID_NAME = "Invalid registry name";
	private static final String DUPLICATE = "Duplicate registry name";
	private static final long serialVersionUID = 1310173658718093383L;

	private final Logger logger = getLogger(ComponentPreferencePanel.class);

	private ComponentFactory factory;
	private ComponentPreference prefs;
	private RegistryTableModel tableModel = new RegistryTableModel();

	@SuppressWarnings("serial")
	private JTable registryTable = new JTable(tableModel) {
		@Override
		public String getToolTipText(MouseEvent me) {
			int row = rowAtPoint(me.getPoint());
			if (row >= 0)
				return tableModel.getRowTooltipText(row);
			return super.getToolTipText(me);
		}
	};

	public ComponentPreferencePanel(ComponentFactory componentFactory,
			ComponentPreference preferences) {
		super(new GridBagLayout());
		factory = componentFactory;
		prefs = preferences;

		GridBagConstraints gbc = new GridBagConstraints();

		// Title describing what kind of settings we are configuring here
		JTextArea descriptionText = new JTextArea(TITLE);
		descriptionText.setLineWrap(true);
		descriptionText.setWrapStyleWord(true);
		descriptionText.setEditable(false);
		descriptionText.setFocusable(false);
		descriptionText.setBorder(new EmptyBorder(10, 10, 10, 10));
		gbc.anchor = WEST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.fill = HORIZONTAL;
		add(descriptionText, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(10, 0, 0, 0);

		registryTable.getColumnModel().getColumn(0).setPreferredWidth(20);
		registryTable.setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN);
		registryTable.setSelectionMode(SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(registryTable);
		// registryTable.setFillsViewportHeight(true);

		gbc.weighty = 1.0;
		gbc.fill = BOTH;

		add(scrollPane, gbc);

		// Add buttons panel
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.gridwidth = 1;
		gbc.fill = HORIZONTAL;
		gbc.anchor = CENTER;
		gbc.insets = new Insets(10, 0, 0, 0);
		add(createRegistryButtonPanel(), gbc);

		// Add buttons panel
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.gridwidth = 1;
		gbc.fill = HORIZONTAL;
		gbc.anchor = CENTER;
		gbc.insets = new Insets(10, 0, 0, 0);
		add(createButtonPanel(), gbc);

		setFields();
	}

	/**
	 * Create the buttons for managing the list of registries.
	 * @return
	 */
	@SuppressWarnings("serial")
	private Component createRegistryButtonPanel() {
		JPanel panel = new JPanel();
		panel.add(new DeselectingButton(new AbstractAction(REMOVE_LABEL) {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				remove();
			}
		}));
		panel.add(new DeselectingButton(new AbstractAction(ADD_LOCAL_LABEL) {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addLocal();
			}
		}));
		panel.add(new DeselectingButton(new AbstractAction(ADD_REMOTE_LABEL) {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addRemote();
			}
		}));
		return panel;
	}

	/**
	 * Create the panel to contain the buttons
	 * 
	 * @return
	 */
	@SuppressWarnings("serial")
	private JPanel createButtonPanel() {
		final JPanel panel = new JPanel();
		panel.add(new DeselectingButton(new AbstractAction(HELP_LABEL) {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Helper.showHelp(panel);
			}
		}));
		panel.add(new DeselectingButton(new AbstractAction(RESET_LABEL) {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setFields();
			}
		}));
		panel.add(new DeselectingButton(new AbstractAction(APPLY_LABEL) {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				applySettings();
				setFields();
			}
		}));
		return panel;
	}

	void remove() {
		int selectedRow = registryTable.getSelectedRow();
		if (selectedRow != -1)
			tableModel.removeRow(selectedRow);
	}

	void addLocal() {
		// Run the GUI
		LocalRegistryPanel inputPanel = new LocalRegistryPanel();
		ValidatingUserInputDialog vuid = new ValidatingUserInputDialog(
				ADD_LOCAL_TITLE, inputPanel);
		vuid.addTextComponentValidation(inputPanel.getRegistryNameField(),
				VALIDATION_MESSAGE, tableModel.getRegistryMap().keySet(),
				DUPLICATE, "[\\p{L}\\p{Digit}_.]+", INVALID_NAME);
		vuid.setSize(new Dimension(400, 250));
		if (!vuid.show(ComponentPreferencePanel.this))
			return;

		// Add the local registry
		String location = inputPanel.getLocationField().getText();
		File newDir = new File(location);
		try {
			tableModel.insertRegistry(inputPanel.getRegistryNameField()
					.getText(), getLocalRegistry(newDir));
		} catch (MalformedURLException e) {
			logger.error("bad url provided by user", e);
			showMessageDialog(null, EXCEPTION_MESSAGE + location,
					EXCEPTION_TITLE, ERROR_MESSAGE);
		} catch (ComponentException e) {
			logger.error("problem creating local registry", e);
			showMessageDialog(null, EXCEPTION_MESSAGE + location,
					EXCEPTION_TITLE, ERROR_MESSAGE);
		}
	}

	void addRemote() {
		RemoteRegistryPanel inputPanel = new RemoteRegistryPanel();
		ValidatingUserInputDialog vuid = new ValidatingUserInputDialog(
				ADD_REMOTE_TITLE, inputPanel);
		vuid.addTextComponentValidation(inputPanel.getRegistryNameField(),
				VALIDATION_MESSAGE, tableModel.getRegistryMap().keySet(),
				DUPLICATE, "[\\p{L}\\p{Digit}_.]+", INVALID_NAME);
		vuid.addTextComponentValidation(inputPanel.getLocationField(),
				SET_URL_MESSAGE, null, "", URL_PATTERN, BAD_URL_MESSAGE);
		vuid.setSize(new Dimension(400, 250));
		if (!vuid.show(ComponentPreferencePanel.this))
			return;

		String location = inputPanel.getLocationField().getText();
		try {
			tableModel.insertRegistry(inputPanel.getRegistryNameField()
					.getText(), getRemoteRegistry(location));
		} catch (MalformedURLException e) {
			logger.error("bad url provided by user", e);
			showMessageDialog(null, EXCEPTION_MESSAGE + location,
					EXCEPTION_TITLE, ERROR_MESSAGE);
		} catch (ComponentException e) {
			showMessageDialog(null, EXCEPTION_MESSAGE + location,
					EXCEPTION_TITLE, ERROR_MESSAGE);
			logger.error("problem creating remote registry", e);
		}
	}

	Registry getLocalRegistry(File location) throws ComponentException,
			MalformedURLException {
		return factory.getRegistry(location.toURI().toURL());
	}

	Registry getRemoteRegistry(String location) throws MalformedURLException,
			ComponentException {
		URL url = new URL(location);
		if (url.getProtocol() == null || url.getProtocol().equals("file"))
			throw new MalformedURLException(
					"may not use relative or local URLs for locating registry");
		return factory.getRegistry(url);
	}

	private void applySettings() {
		prefs.setRegistryMap(tableModel.getRegistryMap());
	}

	private void setFields() {
		tableModel.setRegistryMap(prefs.getRegistryMap());
	}
}
