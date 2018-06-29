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

package io.github.taverna_extras.component.ui.config;

import static java.awt.event.ItemEvent.SELECTED;
import static org.apache.log4j.Logger.getLogger;
import static io.github.taverna_extras.component.api.config.ComponentPropertyNames.COMPONENT_NAME;
import static io.github.taverna_extras.component.api.config.ComponentPropertyNames.COMPONENT_VERSION;
import static io.github.taverna_extras.component.api.config.ComponentPropertyNames.FAMILY_NAME;
import static io.github.taverna_extras.component.api.config.ComponentPropertyNames.REGISTRY_BASE;
import static io.github.taverna_extras.component.ui.util.Utils.SHORT_STRING;

import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.SortedMap;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.apache.log4j.Logger;
import io.github.taverna_extras.component.api.Component;
import io.github.taverna_extras.component.api.ComponentException;
import io.github.taverna_extras.component.api.ComponentFactory;
import io.github.taverna_extras.component.api.Version;
import io.github.taverna_extras.component.ui.panel.ComponentListCellRenderer;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.services.ServiceRegistry;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.ActivityConfigurationPanel;

@SuppressWarnings("serial")
public class ComponentConfigurationPanel extends ActivityConfigurationPanel {
	private static Logger logger = getLogger(ComponentConfigurationPanel.class);

	private ComponentFactory factory;//FIXME beaninject
	private ServiceRegistry sr;

	private final JComboBox<Object> componentVersionChoice = new JComboBox<>();

	public ComponentConfigurationPanel(Activity activity,
			ComponentFactory factory, ServiceRegistry serviceRegistry) {
		super(activity);
		sr = serviceRegistry;
		this.factory = factory;
		componentVersionChoice.setPrototypeDisplayValue(SHORT_STRING);
		initGui();
	}

	private Version getSelectedVersion() {
		return (Version) componentVersionChoice.getSelectedItem();
	}
	private URI getRegistryBase() {
		return URI.create(getProperty(REGISTRY_BASE));
	}
	private String getFamilyName() {
		return getProperty(FAMILY_NAME);
	}
	private String getComponentName() {
		return getProperty(COMPONENT_NAME);
	}
	private Integer getComponentVersion() {
		return Integer.parseInt(getProperty(COMPONENT_VERSION));
	}

	protected void initGui() {
		removeAll();
		setLayout(new GridLayout(0, 2));

		componentVersionChoice.setRenderer(new ComponentListCellRenderer<>());
		componentVersionChoice.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == SELECTED)
					updateToolTipText();
			}
		});
		updateComponentVersionChoice();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 5, 0, 5);
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 2;
		this.add(new JLabel("Component version:"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1;
		this.add(componentVersionChoice, gbc);

		// Populate fields from activity configuration bean
		refreshConfiguration();
	}

	/**
	 * Check that user values in UI are valid
	 */
	@Override
	public boolean checkValues() {
		return true;
	}

	/**
	 * Check if the user has changed the configuration from the original
	 */
	@Override
	public boolean isConfigurationChanged() {
		return !getSelectedVersion().getVersionNumber().equals(
				getComponentVersion());
	}

	/**
	 * Prepare a new configuration bean from the UI, to be returned with
	 * getConfiguration()
	 */
	@Override
	public void noteConfiguration() {
		setProperty(COMPONENT_VERSION, getSelectedVersion().getVersionNumber()
				.toString());
		//FIXME is this right at all???
		configureInputPorts(sr);
		configureOutputPorts(sr);
	}

	private void updateComponentVersionChoice() {
		Component component;
		componentVersionChoice.removeAllItems();
		componentVersionChoice.setToolTipText(null);
		try {
			component = factory.getComponent(getRegistryBase().toURL(),
					getFamilyName(), getComponentName());
		} catch (ComponentException | MalformedURLException e) {
			logger.error("failed to get component", e);
			return;
		}
		SortedMap<Integer, Version> componentVersionMap = component
				.getComponentVersionMap();
		for (Version v : componentVersionMap.values())
			componentVersionChoice.addItem(v);
		componentVersionChoice.setSelectedItem(componentVersionMap
				.get(getComponentVersion()));
		updateToolTipText();
	}

	private void updateToolTipText() {
		Version selectedVersion = (Version) componentVersionChoice
				.getSelectedItem();
		componentVersionChoice.setToolTipText(selectedVersion.getDescription());
	}
}
