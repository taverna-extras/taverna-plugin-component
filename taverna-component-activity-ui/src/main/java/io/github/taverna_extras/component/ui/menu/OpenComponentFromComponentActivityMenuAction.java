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

import java.net.URI;

import javax.swing.Action;

import io.github.taverna_extras.component.api.ComponentFactory;
import io.github.taverna_extras.component.ui.serviceprovider.ComponentServiceIcon;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.ui.menu.AbstractContextualMenuAction;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.file.FileType;
import org.apache.taverna.workbench.selection.SelectionManager;
import org.apache.taverna.workbench.views.graph.GraphViewComponent;

/**
 * @author alanrw
 */
public class OpenComponentFromComponentActivityMenuAction extends
		AbstractContextualMenuAction {
	private static final URI configureSection = URI
			.create("http://taverna.sf.net/2009/contextMenu/configure");

	private SelectionManager sm;
	private FileManager fileManager;
	private ComponentFactory factory;
	private FileType fileType;
	private GraphViewComponent graphView;
	private ComponentServiceIcon icon;

	public OpenComponentFromComponentActivityMenuAction() {
		super(configureSection, 75);
	}

	public void setSelectionManager(SelectionManager sm) {
		this.sm = sm;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	public void setGraphView(GraphViewComponent graphView) {
		this.graphView = graphView;
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}

	@Override
	public boolean isEnabled() {
		return getSelectedActivity() != null;
	}

	@Override
	protected Action createAction() {
		OpenComponentFromComponentActivityAction action = new OpenComponentFromComponentActivityAction(
				fileManager, factory, fileType, graphView, icon);
		action.setSelection(getSelectedActivity());
		return action;
	}

	private Activity getSelectedActivity() {
		Object selection = getContextualSelection().getSelection();
		if (!super.isEnabled() || !(selection instanceof Processor))
			return null;

		try {
			return ((Processor) selection).getActivity(sm.getSelectedProfile());
		} catch (RuntimeException e) {
			return null;
		}
	}
}
