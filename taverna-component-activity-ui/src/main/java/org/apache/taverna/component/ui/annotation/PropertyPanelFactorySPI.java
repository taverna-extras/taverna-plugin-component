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

import static java.awt.Color.WHITE;
import static org.apache.taverna.component.ui.annotation.SemanticAnnotationUtils.getObjectName;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.apache.taverna.component.api.profile.SemanticAnnotationProfile;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

/**
 * @author David Withers
 */
public abstract class PropertyPanelFactorySPI {
	public abstract JComponent getInputComponent(
			SemanticAnnotationProfile semanticAnnotationProfile,
			Statement statement);

	/**
	 * Returns null if the target node is the same as the original statement
	 * 
	 * @param origStatement
	 * @param inputComponent
	 * @return
	 */
	public abstract RDFNode getNewTargetNode(Statement origStatement,
			JComponent inputComponent);

	public abstract int getRatingForSemanticAnnotation(
			SemanticAnnotationProfile semanticAnnotationProfile);

	public abstract JComponent getDisplayComponent(
			SemanticAnnotationProfile semanticAnnotationProfile,
			Statement statement);

	public static JComponent getDefaultInputComponent(
			SemanticAnnotationProfile semanticAnnotationProfile,
			Statement statement) {
		JTextArea inputText = new JTextArea(20, 80);
		if (statement != null)
			inputText.setText(getObjectName(statement));
		inputText.setLineWrap(true);
		inputText.setWrapStyleWord(true);
		return inputText;
	}

	public static JComponent getDefaultDisplayComponent(
			SemanticAnnotationProfile semanticAnnotationProfile,
			Statement statement) {
		JTextArea value = new JTextArea(getObjectName(statement));
		value.setLineWrap(true);
		value.setWrapStyleWord(true);
		value.setEditable(false);
		value.setBackground(WHITE);
		value.setOpaque(true);
		value.setBorder(new EmptyBorder(2, 4, 2, 4));
		return value;
	}
}
