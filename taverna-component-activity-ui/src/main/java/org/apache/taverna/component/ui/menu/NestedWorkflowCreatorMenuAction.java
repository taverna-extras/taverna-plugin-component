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

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.taverna.activities.dataflow.servicedescriptions.DataflowActivityIcon;

import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.ui.menu.AbstractContextualMenuAction;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.selection.SelectionManager;
import org.apache.taverna.workbench.views.graph.GraphViewComponent;

/**
 * @author alanrw
 */
public class NestedWorkflowCreatorMenuAction extends
		AbstractContextualMenuAction {
	private static final URI configureSection = URI
			.create("http://taverna.sf.net/2009/contextMenu/configure");

	private SelectionManager sm;
	private EditManager em;
	private GraphViewComponent gv;

	public NestedWorkflowCreatorMenuAction() {
		super(configureSection, 70);
	}

	public void setEditManager(EditManager editManager) {
		em = editManager;
	}
	public void setGraphView(GraphViewComponent graphView) {
		gv = graphView;
	}
	public void setSelectionManager(SelectionManager selectionManager) {
		sm = selectionManager;
	}

	@Override
	public boolean isEnabled() {
		Object selection = getContextualSelection().getSelection();
		if (!super.isEnabled() || selection == null)
			return false;
		if (selection instanceof Processor)
			return true;
		if (!(selection instanceof Workflow))
			return false;
		return !((Workflow) selection).getProcessors().isEmpty();
	}

	@Override
	protected Action createAction() {
		return new AbstractAction("Create nested workflow...",
				DataflowActivityIcon.getDataflowIcon()) {
			private static final long serialVersionUID = -3121307982540205215L;

			@Override
			public void actionPerformed(ActionEvent e) {
				createNestedWorkflow();
			}
		};
	}

	private void createNestedWorkflow() {
		Dialog dialog = new NestedWorkflowCreationDialog(null,
				getContextualSelection().getSelection(),
				sm.getSelectedWorkflow(), em, gv);
		dialog.setVisible(true);
	}
}
