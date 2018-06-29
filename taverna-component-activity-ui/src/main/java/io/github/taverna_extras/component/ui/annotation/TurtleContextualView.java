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
import static io.github.taverna_extras.component.ui.annotation.SemanticAnnotationUtils.findSemanticAnnotation;
import static io.github.taverna_extras.component.ui.annotation.SemanticAnnotationUtils.getStrippedAnnotationContent;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.slf4j.Logger;

import org.apache.taverna.scufl2.api.annotation.Annotation;
import org.apache.taverna.scufl2.api.common.AbstractNamed;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.workbench.ui.views.contextualviews.ContextualView;

/**
 * @author alanrw
 */
public class TurtleContextualView extends ContextualView {
	private static final long serialVersionUID = -3401885589263647202L;
	private static final Logger log = getLogger(TurtleContextualView.class);
	private JPanel panel;
	private String annotationContent = "";

	public TurtleContextualView(AbstractNamed selection, WorkflowBundle bundle)  {
		Annotation annotation = findSemanticAnnotation(selection);
		try {
			if (annotation != null)
				annotationContent = getStrippedAnnotationContent(annotation);
		} catch (IOException e) {
			log.info("failed to read semantic annotation; using empty string", e);
		}
		initialise();
		initView();
	}

	@Override
	public JComponent getMainFrame() {
		return panel;
	}

	@Override
	public int getPreferredPosition() {
		return 512;
	}

	@Override
	public String getViewTitle() {
		return "Turtle representation";
	}

	@Override
	public void refreshView() {
		initialise();
	}

	protected final void initialise() {
		if (panel == null)
			panel = new JPanel(new BorderLayout());
		else
			panel.removeAll();
		JTextArea textArea = new JTextArea(20, 80);
		textArea.setEditable(false);
		textArea.setText(annotationContent);
		panel.add(textArea, CENTER);
		revalidate();
	}
}
