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

import org.apache.taverna.component.ui.preference.ComponentPreference;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.file.FileType;
import org.apache.taverna.workbench.views.graph.GraphViewComponent;

/**
 * @author alanrw
 */
public class FileOpenFromComponentMenuAction extends
		AbstractComponentMenuAction {
	private static final URI FILE_OPEN_FROM_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentOpen");
	private FileManager fm;
	private FileType ft;
	private ComponentPreference prefs;
	private GraphViewComponent graphView;
	private ComponentServiceIcon icon;

	public FileOpenFromComponentMenuAction() {
		super(700, FILE_OPEN_FROM_COMPONENT_URI);
	}

	public void setFileManager(FileManager fm) {
		this.fm = fm;
	}

	public void setFileType(FileType ft) {
		this.ft = ft;
	}

	public void setPreferences(ComponentPreference prefs) {
		this.prefs = prefs;
	}

	public void setGraphView(GraphViewComponent graphView) {
		this.graphView = graphView;
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}

	@Override
	protected Action createAction() {
		return new OpenWorkflowFromComponentAction(fm, ft, prefs, graphView,
				icon);
	}
}
