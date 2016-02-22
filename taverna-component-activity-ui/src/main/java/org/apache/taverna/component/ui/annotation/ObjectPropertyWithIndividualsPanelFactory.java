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

import static java.awt.FlowLayout.RIGHT;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.lang.Integer.MIN_VALUE;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showInputDialog;
import static org.apache.taverna.component.ui.annotation.SemanticAnnotationUtils.getDisplayName;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.apache.taverna.component.api.profile.SemanticAnnotationProfile;
import org.apache.taverna.component.ui.localworld.LocalWorld;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.taverna.lang.ui.DeselectingButton;

/**
 * @author David Withers
 * @author Alan Williams
 */
public class ObjectPropertyWithIndividualsPanelFactory extends
		PropertyPanelFactorySPI {
	/*
	 * TODO Consider what sort of sharing model is appropriate for the local
	 * world
	 */
	private static LocalWorld localWorld = LocalWorld.getInstance();

	@Override
	public int getRatingForSemanticAnnotation(
			SemanticAnnotationProfile semanticAnnotationProfile) {
		OntProperty property = semanticAnnotationProfile.getPredicate();
		if ((property != null) && property.isObjectProperty()
				/*
				 * && !semanticAnnotationProfile.getIndividuals().isEmpty()
				 */)
			return 100;
		return MIN_VALUE;
	}

	@Override
	public JComponent getInputComponent(
			SemanticAnnotationProfile semanticAnnotationProfile,
			Statement statement) {
		return new ComboBoxWithAdd(semanticAnnotationProfile, statement);
	}

	@Override
	public RDFNode getNewTargetNode(Statement originalStatement,
			JComponent component) {
		ComboBoxWithAdd panel = (ComboBoxWithAdd) component;
		RDFNode newNode = panel.getSelectedItem();
		if ((originalStatement == null)
				|| !originalStatement.getObject().equals(newNode))
			return newNode;
		return null;
	}


	private static class ComboBoxWithAdd extends JPanel {
		private static final long serialVersionUID = -9156213096428945270L;
		private static DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
		OntClass rangeClass = null;
		JComboBox<Individual> resources;

		public ComboBoxWithAdd(
				SemanticAnnotationProfile semanticAnnotationProfile,
				Statement statement) {
			super(new GridBagLayout());

			rangeClass = semanticAnnotationProfile.getRangeClass();

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.anchor = NORTHWEST;
			List<Individual> individuals = semanticAnnotationProfile
					.getIndividuals();
			if (rangeClass != null)
				individuals
						.addAll(localWorld.getIndividualsOfClass(rangeClass));

			resources = new JComboBox<Individual>(new Vector<>(individuals));
			resources.setRenderer(new ListCellRenderer<Individual>() {
				@Override
				public Component getListCellRendererComponent(
						JList<? extends Individual> list, Individual value,
						int index, boolean isSelected, boolean cellHasFocus) {
					return defaultRenderer.getListCellRendererComponent(list,
							getDisplayName(value), index, isSelected,
							cellHasFocus);
				}
			});
			resources.setEditable(false);
			if (statement != null) {
				Object origResource = statement.getObject();
				if (origResource != null)
					resources.setSelectedItem(origResource);
			}
			this.add(resources, gbc);

			gbc.gridy++;

			JPanel buttonPanel = new JPanel(new FlowLayout(RIGHT));
			buttonPanel.add(new DeselectingButton("Add external",
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							addExternal();
						}
					}));
			buttonPanel.add(new DeselectingButton("Add local",
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							addLocal();
						}
					}));
			gbc.anchor = EAST;
			this.add(buttonPanel, gbc);
		}

		private void addExternal() {
			String answer = showInputDialog("Please enter the URL for the resource");
			resources.addItem(localWorld.createIndividual(answer, rangeClass));
		}

		private void addLocal() {
			TurtleInputPanel turtlePanel = new TurtleInputPanel(rangeClass);
			if (showConfirmDialog(null, turtlePanel, "Turtle input",
					OK_CANCEL_OPTION, QUESTION_MESSAGE) == OK_OPTION) {
				OntModel addedModel = turtlePanel.getContentAsModel();
				for (Individual i : addedModel.listIndividuals(rangeClass)
						.toList())
					resources.addItem(i);
				localWorld.addModelFromString(turtlePanel.getContentAsString());
			}
		}

		public RDFNode getSelectedItem() {
			return (RDFNode) resources.getSelectedItem();
		}
	}

	@Override
	public JComponent getDisplayComponent(
			SemanticAnnotationProfile semanticAnnotationProfile,
			Statement statement) {
		JComponent result = getDefaultDisplayComponent(
				semanticAnnotationProfile, statement);
		return result;
	}
}
