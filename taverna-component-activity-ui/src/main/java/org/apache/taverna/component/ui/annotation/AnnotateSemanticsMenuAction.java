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

package io.github.taverna_extras.component.ui.annotation;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.FlowLayout.TRAILING;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import io.github.taverna_extras.component.api.ComponentFactory;
import io.github.taverna_extras.component.api.Version;
import org.apache.taverna.lang.ui.DeselectingButton;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.AbstractNamed;
import org.apache.taverna.scufl2.api.port.ActivityPort;
import org.apache.taverna.ui.menu.AbstractContextualMenuAction;
import org.apache.taverna.workbench.file.FileManager;

/**
 * @author alanrw
 */
public class AnnotateSemanticsMenuAction extends AbstractContextualMenuAction {
	private static final String ANNOTATE_SEMANTICS = "Annotate semantics...";
	private static final URI configureSection = URI
			.create("http://taverna.sf.net/2009/contextMenu/configure");
	private FileManager fileManager;
	private ComponentFactory factory;

	public AnnotateSemanticsMenuAction() {
		super(configureSection, 45);
	}

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
	}

	public void setFileManager(FileManager fm) {
		this.fileManager = fm;
	}

	@Override
	public boolean isEnabled() {
		Object selection = getContextualSelection().getSelection();
		Object dataflowSource = fileManager.getDataflowSource(fileManager
				.getCurrentDataflow());
		if (dataflowSource instanceof Version.ID)
			return (selection instanceof AbstractNamed)
					&& !(selection instanceof Activity || selection instanceof ActivityPort);
		return false;
	}

	@SuppressWarnings("serial")
	@Override
	protected Action createAction() {
		return new AbstractAction(ANNOTATE_SEMANTICS) {
			@Override
			public void actionPerformed(ActionEvent e) {
				showAnnotateSemanticsPanel();
			}
		};
	}

	private void showAnnotateSemanticsPanel() {
		SemanticAnnotationContextualView view = new SemanticAnnotationContextualView(
				fileManager, factory, (AbstractNamed) getContextualSelection()
						.getSelection());

		final JDialog dialog = new JDialog((Frame) null, "Annotate semantics");
		dialog.setLayout(new BorderLayout());
		dialog.add(new JScrollPane(view), CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(TRAILING));
		buttonPanel.add(new DeselectingButton("OK", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		}));

		dialog.add(buttonPanel, SOUTH);
		dialog.setSize(new Dimension(400, 300));
		dialog.setLocationRelativeTo(null);
		dialog.setModal(true);
		dialog.setVisible(true);
	}
}
