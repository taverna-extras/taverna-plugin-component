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

package org.apache.taverna.component.ui.menu.component;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.apache.log4j.Logger.getLogger;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.Version;
import org.apache.taverna.component.ui.ComponentAction;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;
import org.apache.taverna.component.ui.util.Utils;
import org.apache.taverna.lang.observer.Observable;
import org.apache.taverna.lang.observer.Observer;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.file.events.FileManagerEvent;
import org.apache.taverna.workbench.views.graph.GraphViewComponent;

/**
 * @author alanrw
 */
public class ComponentWorkflowCreatorAction extends ComponentAction implements
		Observer<FileManagerEvent> {
	private static final long serialVersionUID = -299685223430721587L;
	private static Logger logger = getLogger(ComponentWorkflowCreatorAction.class);
	private static final String CREATE_COMPONENT = "Create component from current workflow...";

	private ComponentCreatorSupport support;
	private FileManager fileManager;
	private Utils utils;

	public ComponentWorkflowCreatorAction(ComponentCreatorSupport support,
			FileManager fm, GraphViewComponent graphView,
			ComponentServiceIcon icon, Utils utils) {
		super(CREATE_COMPONENT, graphView);
		this.support = support;
		this.utils = utils;
		fm.addObserver(this);
		this.setIcon(icon);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		WorkflowBundle bundle = fileManager.getCurrentDataflow();
		try {
			Version.ID ident = support.getNewComponentIdentification(bundle.getName());//TODO is this right
			if (ident == null)
				return;
			support.saveWorkflowAsComponent(bundle, ident);
		} catch (Exception e) {
			showMessageDialog(graphView, e.getCause().getMessage(),
					"Component creation failure", ERROR_MESSAGE);
			logger.error("failed to save workflow as component", e);
		}
	}

	@Override
	public void notify(Observable<FileManagerEvent> sender,
			FileManagerEvent message) throws Exception {
		setEnabled(!utils.currentDataflowIsComponent());
	}
}
