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

package io.github.taverna_extras.component.ui.menu;

import static org.apache.log4j.Logger.getLogger;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;
import io.github.taverna_extras.component.api.ComponentFactory;
import io.github.taverna_extras.component.api.Version;
import io.github.taverna_extras.component.ui.ComponentAction;
import io.github.taverna_extras.component.ui.ComponentActivityConfigurationBean;
import io.github.taverna_extras.component.ui.serviceprovider.ComponentServiceIcon;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.file.FileType;
import org.apache.taverna.workbench.file.exceptions.OpenException;
import org.apache.taverna.workbench.views.graph.GraphViewComponent;

/**
 * @author alanrw
 */
@SuppressWarnings("serial")
public class OpenComponentFromComponentActivityAction extends ComponentAction {
	private static Logger logger = getLogger(OpenComponentFromComponentActivityAction.class);

	private final FileManager fileManager;
	private final ComponentFactory factory;
	private final FileType fileType;

	public OpenComponentFromComponentActivityAction(FileManager fileManager,
			ComponentFactory factory, FileType ft,
			GraphViewComponent graphView, ComponentServiceIcon icon) {
		super("Open component...", graphView);
		this.fileManager = fileManager;
		this.factory = factory;
		this.fileType = ft;
		setIcon(icon);
	}

	private Activity selection;

	@Override
	public void actionPerformed(ActionEvent ev) {
		try {
			Version.ID ident = new ComponentActivityConfigurationBean(
					selection.getConfiguration(), factory);
			WorkflowBundle d = fileManager.openDataflow(fileType, ident);
			markGraphAsBelongingToComponent(d);
		} catch (OpenException e) {
			logger.error("failed to open component", e);
		} catch (MalformedURLException e) {
			logger.error("bad URL in component description", e);
		}
	}

	public void setSelection(Activity selection) {
		this.selection = selection;
	}
}
