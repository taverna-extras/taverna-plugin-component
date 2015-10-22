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

package org.apache.taverna.component.ui;

import static java.awt.Color.RED;
import static javax.swing.SwingUtilities.invokeLater;
import static org.apache.log4j.Logger.getLogger;

import javax.swing.AbstractAction;

import org.apache.log4j.Logger;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.workbench.models.graph.GraphController;
import org.apache.taverna.workbench.models.graph.svg.SVGGraph;
import org.apache.taverna.workbench.views.graph.GraphViewComponent;

@SuppressWarnings("serial")
public abstract class ComponentAction extends AbstractAction {
	private static Logger logger = getLogger(ComponentAction.class);

	protected GraphViewComponent graphView;

	protected ComponentAction(String title, GraphViewComponent graphView) {
		this.graphView = graphView;
	}

	public void setIcon(ComponentServiceIcon icon) {
        putValue(SMALL_ICON, icon.getIcon());
	}

	protected void markGraphAsBelongingToComponent(WorkflowBundle bundle) {
		final GraphController gc = graphView.getGraphController(bundle
				.getMainWorkflow());
		invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					SVGGraph g = (SVGGraph) gc.getGraph();
					g.setFillColor(RED);
					gc.redraw();
				} catch (NullPointerException e) {
					logger.error(e);
				}
			}
		});
	}
}
