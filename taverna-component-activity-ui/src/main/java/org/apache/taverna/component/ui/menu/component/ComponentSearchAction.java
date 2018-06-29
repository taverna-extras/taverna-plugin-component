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

package io.github.taverna_extras.component.ui.menu.component;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.WEST;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.JOptionPane.showOptionDialog;
import static org.apache.log4j.Logger.getLogger;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Map.Entry;
import java.util.SortedMap;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;
import io.github.taverna_extras.component.api.ComponentFactory;
import io.github.taverna_extras.component.api.Registry;
import io.github.taverna_extras.component.api.Version;
import io.github.taverna_extras.component.api.profile.Profile;
import io.github.taverna_extras.component.ui.panel.PrefixPanel;
import io.github.taverna_extras.component.ui.panel.ProfileChooserPanel;
import io.github.taverna_extras.component.ui.panel.RegistryChooserPanel;
import io.github.taverna_extras.component.ui.panel.SearchChoicePanel;
import io.github.taverna_extras.component.ui.preference.ComponentPreference;
import io.github.taverna_extras.component.ui.serviceprovider.ComponentServiceDesc;
import io.github.taverna_extras.component.ui.serviceprovider.ComponentServiceIcon;
import org.apache.taverna.services.ServiceRegistry;
import org.apache.taverna.ui.menu.MenuManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.selection.SelectionManager;
import org.apache.taverna.workbench.ui.workflowview.WorkflowView;

/**
 * @author alanrw
 */
public class ComponentSearchAction extends AbstractAction {
	private static final String WFDESC_PREFIX = "wfdesc";
	private static final long serialVersionUID = -7780471499146286881L;
	@SuppressWarnings("unused")
	private static final Logger logger = getLogger(ComponentSearchAction.class);
	private static final String SEARCH_FOR_COMPONENTS = "Search for components...";

	private final ComponentPreference prefs;
	private final ComponentFactory factory;
	private final EditManager em;
	private final MenuManager mm;
	private final SelectionManager sm;
	private final ServiceRegistry sr;
	private final ComponentServiceIcon icon;

	public ComponentSearchAction(ComponentPreference prefs,
			ComponentFactory factory, EditManager em, MenuManager mm,
			SelectionManager sm, ServiceRegistry sr, ComponentServiceIcon icon) {
		super(SEARCH_FOR_COMPONENTS, icon.getIcon());
		this.prefs = prefs;
		this.factory = factory;
		this.em = em;
		this.mm = mm;
		this.sm = sm;
		this.sr = sr;
		this.icon = icon;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JPanel overallPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		RegistryChooserPanel registryPanel = new RegistryChooserPanel(prefs);

		gbc.insets.left = 5;
		gbc.insets.right = 5;
		gbc.gridx = 0;
		gbc.anchor = WEST;
		gbc.fill = BOTH;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.gridy++;
		overallPanel.add(registryPanel, gbc);

		ProfileChooserPanel profilePanel = new ProfileChooserPanel(registryPanel);
		gbc.gridx = 0;
		gbc.gridy++;
		overallPanel.add(profilePanel, gbc);

		PrefixPanel prefixPanel = new PrefixPanel(profilePanel);
		gbc.gridx = 0;
		gbc.gridy++;
		overallPanel.add(prefixPanel, gbc);

		JTextArea queryPane = new JTextArea(20, 80);
		gbc.gridx = 0;
		gbc.weighty = 1;
		gbc.gridy++;
		overallPanel.add(new JScrollPane(queryPane), gbc);

		int answer = showConfirmDialog(null, overallPanel,
				"Search for components", OK_CANCEL_OPTION);
		if (answer == OK_OPTION)
			doSearch(registryPanel.getChosenRegistry(),
					profilePanel.getChosenProfile(),
					prefixPanel.getPrefixMap(), queryPane.getText());
	}

	private void doSearch(Registry chosenRegistry, Profile chosenProfile,
			SortedMap<String, String> prefixMap, String queryString) {
		if (chosenRegistry == null) {
			showMessageDialog(null, "Unable to determine registry",
					"Component Registry Problem", ERROR_MESSAGE);
			return;
		}
		if (chosenProfile == null) {
			showMessageDialog(null, "Unable to determine profile",
					"Component Profile Problem", ERROR_MESSAGE);
			return;
		}
		StringBuilder prefixString = new StringBuilder();
		for (Entry<String, String> entry : prefixMap.entrySet())
			if (!entry.getKey().equals(WFDESC_PREFIX))
				prefixString.append(constructPrefixString(entry));

		SearchChoicePanel searchChoicePanel = new SearchChoicePanel(
				chosenRegistry, prefixString.toString(), queryString);
		int answer = showOptionDialog(null, searchChoicePanel,
				"Matching components", OK_CANCEL_OPTION, QUESTION_MESSAGE,
				null, new String[] { "Add to workflow", "Cancel" }, "Cancel");
		if (answer == OK_OPTION) {
			Version.ID ident = searchChoicePanel.getVersionIdentification();
			if (ident != null)
				WorkflowView.importServiceDescription(new ComponentServiceDesc(prefs,
						factory, icon, ident), false, em, mm, sm, sr);
		}
	}

	private static String constructPrefixString(Entry<String, String> entry) {
		String key = entry.getKey();
		String value = entry.getValue();
		return String.format("PREFIX %s:<%s>\n", key, value);
	}
}
