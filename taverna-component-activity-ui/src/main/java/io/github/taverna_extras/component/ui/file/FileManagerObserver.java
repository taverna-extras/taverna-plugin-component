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

package io.github.taverna_extras.component.ui.file;

import static java.awt.Color.WHITE;
import static java.awt.Font.BOLD;
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.SwingUtilities.isEventDispatchThread;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

import org.apache.batik.swing.JSVGCanvas;
import io.github.taverna_extras.component.api.Version;
import io.github.taverna_extras.component.ui.util.Utils;
import org.apache.taverna.lang.observer.Observable;
import org.apache.taverna.lang.observer.Observer;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.workbench.StartupSPI;
import org.apache.taverna.workbench.configuration.colour.ColourManager;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.file.events.FileManagerEvent;
import org.apache.taverna.workbench.models.graph.svg.SVGGraphController;
import org.apache.taverna.workbench.views.graph.GraphViewComponent;

public class FileManagerObserver implements StartupSPI {
	private static final Color COLOR = new Color(230, 147, 210);

	private FileManager fileManager;
	private ColourManager colours;
	private GraphViewComponent graphView;
	private Utils utils;

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setColourManager(ColourManager colours) {
		this.colours = colours;
	}

	public void setGraphView(GraphViewComponent graphView) {
		this.graphView = graphView;
	}

	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	@Override
	public boolean startup() {
		colours.setPreferredColour(
				"io.github.taverna_extras.component.registry.Component", COLOR);
		colours.setPreferredColour(
				"io.github.taverna_extras.component.ComponentActivity", COLOR);
		fileManager.addObserver(new Observer<FileManagerEvent>() {
			@Override
			public void notify(Observable<FileManagerEvent> observable,
					FileManagerEvent event) throws Exception {
				FileManagerObserverRunnable runnable = new FileManagerObserverRunnable();
				if (isEventDispatchThread())
					runnable.run();
				else
					invokeLater(runnable);
			}
		});
		return true;
	}

	@Override
	public int positionHint() {
		return 200;
	}

	public class FileManagerObserverRunnable implements Runnable {
		@Override
		public void run() {
			WorkflowBundle currentDataflow = fileManager.getCurrentDataflow();
			if (currentDataflow == null)
				return;
			SVGGraphController graphController = (SVGGraphController) graphView
					.getGraphController(currentDataflow.getMainWorkflow());
			if (graphController == null)
				return;
			JSVGCanvas svgCanvas = graphController.getSVGCanvas();
			Object dataflowSource = fileManager
					.getDataflowSource(currentDataflow);
			if (utils.currentDataflowIsComponent())
				svgCanvas.setBorder(new ComponentBorder(
						(Version.ID) dataflowSource));
			else
				svgCanvas.setBorder(null);
			svgCanvas.repaint();
		}
	}

	static class ComponentBorder implements Border {
		private final Insets insets = new Insets(25, 0, 0, 0);
		private final String text;

		public ComponentBorder(Version.ID identification) {
			text = "Component : " + identification.getComponentName();
		}

		@Override
		public Insets getBorderInsets(java.awt.Component c) {
			return insets;
		}

		@Override
		public boolean isBorderOpaque() {
			return true;
		}

		@Override
		public void paintBorder(java.awt.Component c, Graphics g, int x, int y,
				int width, int height) {
			g.setColor(COLOR);
			g.fillRect(x, y, width, 20);
			g.setFont(g.getFont().deriveFont(BOLD));
			g.setColor(WHITE);
			g.drawString(text, x + 5, y + 15);
		}
	}
}
