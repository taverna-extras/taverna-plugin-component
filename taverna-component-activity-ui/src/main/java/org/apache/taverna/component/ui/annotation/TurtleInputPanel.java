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

package org.apache.taverna.component.ui.annotation;

import static org.apache.jena.rdf.model.ModelFactory.createOntologyModel;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;
import static java.awt.BorderLayout.SOUTH;
import static org.apache.taverna.component.ui.annotation.SemanticAnnotationUtils.populateModelFromString;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.taverna.lang.ui.DeselectingButton;
import org.apache.taverna.lang.ui.ReadOnlyTextArea;

/**
 * @author alanrw
 */
@SuppressWarnings("serial")
public class TurtleInputPanel extends JPanel {
	JTextArea turtleTextArea = new JTextArea(30, 80);
	ReadOnlyTextArea errors = new ReadOnlyTextArea(1, 80);
	private OntClass clazz;

	public TurtleInputPanel(OntClass clazz) {
		super(new BorderLayout());
		this.clazz = clazz;

		add(new JScrollPane(turtleTextArea), CENTER);

		turtleTextArea.setText("<#changeme> a <" + clazz.getURI() + ">\n\n\n.");

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		JButton validateButton = new DeselectingButton(new AbstractAction(
				"Validate") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				getContentAsModel();
			}
		});
		buttonPanel.add(errors, CENTER);
		errors.setOpaque(false);
		buttonPanel.add(validateButton, EAST);
		add(buttonPanel, SOUTH);
	}

	public OntModel getContentAsModel() {
		OntModel result = createOntologyModel();
		try {
			populateModelFromString(result, getContentAsString());

			// Check it is not still called changeme
			List<Individual> individuals = result.listIndividuals(clazz)
					.toList();
			if (individuals.isEmpty()) {
				errors.setText("No valid individuals");
				return null;
			}
			for (Individual i : individuals)
				if (i.getURI().endsWith("changeme")) {
					errors.setText("Name has not been changed");
					return null;
				}

			errors.setText("No errors found");
			return result;
		} catch (Throwable ex) { // syntax error?
			errors.setText(ex.getMessage());
			return null;
		}
	}

	public String getContentAsString() {
		return turtleTextArea.getText();
	}
}
