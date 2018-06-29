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

import static javax.swing.JOptionPane.CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.showOptionDialog;
import static org.apache.log4j.Logger.getLogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import io.github.taverna_extras.component.api.Component;
import io.github.taverna_extras.component.api.Family;
import io.github.taverna_extras.component.api.Registry;
import io.github.taverna_extras.component.api.Version;
import io.github.taverna_extras.component.ui.ComponentAction;
import io.github.taverna_extras.component.ui.panel.ComponentChoiceMessage;
import io.github.taverna_extras.component.ui.panel.ComponentVersionChooserPanel;
import io.github.taverna_extras.component.ui.preference.ComponentPreference;
import io.github.taverna_extras.component.ui.serviceprovider.ComponentServiceIcon;
import org.apache.taverna.lang.observer.Observable;
import org.apache.taverna.lang.observer.Observer;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.file.FileType;
import org.apache.taverna.workbench.file.exceptions.OpenException;
import org.apache.taverna.workbench.views.graph.GraphViewComponent;

/**
 * @author alanrw
 */
public class OpenWorkflowFromComponentAction extends ComponentAction {
	private static final long serialVersionUID = 7382677337746318211L;
	private static final Logger logger = getLogger(OpenWorkflowFromComponentAction.class);
	private static final String ACTION_NAME = "Open component...";
	private static final String ACTION_DESCRIPTION = "Open the workflow that implements a component";

	private final FileManager fm;
	private final FileType ft;
	private final ComponentPreference prefs;

	public OpenWorkflowFromComponentAction(FileManager fm, FileType ft,
			ComponentPreference prefs, GraphViewComponent graphView,
			ComponentServiceIcon icon) {
		super(ACTION_NAME, graphView);
		this.fm = fm;
		this.ft = ft;
		this.prefs = prefs;
		setIcon(icon);
		putValue(SHORT_DESCRIPTION, ACTION_DESCRIPTION);
	}

	@Override
	public void actionPerformed(ActionEvent arg) {
		final ComponentVersionChooserPanel panel = new ComponentVersionChooserPanel(prefs);	
		
		final JButton okay = new JButton("OK");
		okay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getOptionPane((JComponent) e.getSource()).setValue(OK_OPTION);
				doOpen(panel.getChosenRegistry(), panel.getChosenFamily(),
						panel.getChosenComponent(),
						panel.getChosenComponentVersion());
			}
		});
		okay.setEnabled(false);
		// Only enable the OK button of a component is not null
		panel.getComponentChooserPanel().addObserver(
				new Observer<ComponentChoiceMessage>() {
					@Override
					public void notify(
							Observable<ComponentChoiceMessage> sender,
							ComponentChoiceMessage message) throws Exception {
						okay.setEnabled(message.getChosenComponent() != null);
					}
				});

		final JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
                getOptionPane((JComponent)e.getSource()).setValue(CANCEL_OPTION);
		    }
		});

		showOptionDialog(graphView, panel, "Component version choice",
				YES_NO_OPTION, QUESTION_MESSAGE, null, new Object[] { okay,
						cancel }, okay);
	}
	
    protected JOptionPane getOptionPane(JComponent parent) {
		if (parent instanceof JOptionPane)
			return (JOptionPane) parent;
		return getOptionPane((JComponent) parent.getParent());
    }

	private void doOpen(Registry registry, Family family, Component component,
			Version version) {
		Version.ID ident = new Version.Identifier(
				registry.getRegistryBase(), family.getName(),
				component.getName(), version.getVersionNumber());

		try {
			WorkflowBundle d = fm.openDataflow(ft, ident);
			markGraphAsBelongingToComponent(d);
		} catch (OpenException e) {
			logger.error("Failed to open component definition", e);
		}
	}
}
