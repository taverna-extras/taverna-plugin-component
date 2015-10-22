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

import java.net.URI;

import javax.swing.Action;

import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;
import org.apache.taverna.component.ui.util.Utils;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.views.graph.GraphViewComponent;

/**
 * @author alanrw
 */
public class ComponentWorkflowCreatorMenuAction extends AbstractComponentMenuAction {
	private static final URI COMPONENT_WORKFLOW_CREATE_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentCreate");

	private ComponentCreatorSupport support;
	private FileManager fm;
	private GraphViewComponent graphView;
	private ComponentServiceIcon icon;
	private Utils utils;

	public ComponentWorkflowCreatorMenuAction() {
		super(600, COMPONENT_WORKFLOW_CREATE_URI);
	}

	public void setSupport(ComponentCreatorSupport support) {
		this.support = support;
	}

	public void setFileManager(FileManager fm) {
		this.fm = fm;
	}

	public void setGraphView(GraphViewComponent graphView) {
		this.graphView = graphView;
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}

	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	@Override
	protected Action createAction() {
		return new ComponentWorkflowCreatorAction(support, fm, graphView, icon, utils);
	}
}
