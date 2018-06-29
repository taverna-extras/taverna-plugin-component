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

import static javax.swing.BorderFactory.createEtchedBorder;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.border.TitledBorder.CENTER;
import static javax.swing.border.TitledBorder.TOP;

import java.awt.BorderLayout;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import io.github.taverna_extras.component.api.ComponentException;
import io.github.taverna_extras.component.api.profile.Profile;
import org.apache.taverna.lang.observer.Observable;
import org.apache.taverna.lang.observer.Observer;

/**
 * @author alanrw
 */
@SuppressWarnings("serial")
public class PrefixPanel extends JPanel {
	private DefaultTableModel prefixModel = new DefaultTableModel(10, 2) {
		@Override
		public boolean isCellEditable(int row, int column) {
			// all cells false
			return false;
		};
	};

	private JTable prefixTable = new JTable(prefixModel);

	public PrefixPanel(ProfileChooserPanel profilePanel) {
		this();
		profilePanel.addObserver(new Observer<ProfileChoiceMessage>() {
			@Override
			public void notify(Observable<ProfileChoiceMessage> sender,
					ProfileChoiceMessage message) throws Exception {
				profileChanged(message.getChosenProfile());
			}
		});
	}

	public PrefixPanel() {
		super(new BorderLayout());
		prefixModel.setColumnIdentifiers(new String[] { "Prefix", "URL" });
		add(new JScrollPane(prefixTable), BorderLayout.CENTER);
		setBorder(createTitledBorder(createEtchedBorder(), "Prefixes", CENTER,
				TOP));
	}

	public TreeMap<String, String> getPrefixMap() {
		TreeMap<String, String> result = new TreeMap<>();
		for (int i = 0; i < prefixModel.getRowCount(); i++)
			result.put((String) prefixModel.getValueAt(i, 0),
					(String) prefixModel.getValueAt(i, 1));
		return result;
	}

	private void profileChanged(Profile newProfile) throws ComponentException {
		prefixModel.setRowCount(0);
		if (newProfile != null)
			for (Entry<String, String> entry : newProfile.getPrefixMap()
					.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (!value.endsWith("#"))
					value += "#";
				prefixModel.addRow(new String[] { key, value });
			}
		validate();
	}
}
