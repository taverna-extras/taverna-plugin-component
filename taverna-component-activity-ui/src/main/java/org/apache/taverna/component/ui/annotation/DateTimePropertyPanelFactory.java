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

import static org.apache.jena.datatypes.xsd.XSDDatatype.XSDdateTime;
import static org.apache.jena.rdf.model.ResourceFactory.createTypedLiteral;
import static java.lang.Integer.MIN_VALUE;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.text.DefaultCaret;

import org.apache.taverna.component.api.profile.SemanticAnnotationProfile;

import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

/**
 * 
 * 
 * @author Alan Williams
 */
public class DateTimePropertyPanelFactory extends PropertyPanelFactorySPI {

	private static String DateTimeString = XSDdateTime.getURI();

	public DateTimePropertyPanelFactory() {
		super();
	}

	@Override
	public JComponent getInputComponent(
			SemanticAnnotationProfile semanticAnnotationProfile,
			Statement statement) {
		Date now = new Date();
		SpinnerDateModel dateModel = new SpinnerDateModel(now, null, now,
				Calendar.DAY_OF_MONTH);
		JSpinner s = new JSpinner(dateModel);
		JSpinner.DateEditor de = new JSpinner.DateEditor(s,
				"yyyy-MM-dd-HH-mm-ss");

		/*
		 * Suggested hack from
		 * http://www.coderanch.com/t/345684/GUI/java/JSpinner-DateEditor-Set-default-focus
		 */

		de.getTextField().setCaret(new DefaultCaret() {
			private static final long serialVersionUID = 6779256780590610172L;
			private boolean diverted = false;

			@Override
			public void setDot(int dot) {
				diverted = (dot == 0);
				if (diverted)
					dot = getComponent().getDocument().getLength();
				super.setDot(dot);
			}

			@Override
			public void moveDot(int dot) {
				if (diverted) {
					super.setDot(0);
					diverted = false;
				}
				super.moveDot(dot);
			}
		});
		s.setEditor(de);
		if (statement != null) {
			Object o = statement.getObject().asLiteral().getValue();
			if (o instanceof XSDDateTime)
				dateModel.setValue(((XSDDateTime) o).asCalendar().getTime());
		}
		return s;
	}

	@Override
	public RDFNode getNewTargetNode(Statement originalStatement,
			JComponent component) {
		JSpinner spinner = (JSpinner) component;
		Date d = (Date) spinner.getValue();
		if ((originalStatement == null)
				|| !originalStatement.getObject().asLiteral().getValue()
						.equals(d)) {
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(d);
			return createTypedLiteral(cal);
		}
		return null;
	}

	@Override
	public int getRatingForSemanticAnnotation(
			SemanticAnnotationProfile semanticAnnotationProfile) {
		OntProperty property = semanticAnnotationProfile.getPredicate();
		if ((property != null) && property.isDatatypeProperty()
				&& DateTimeString.equals(semanticAnnotationProfile
						.getClassString()))
			return 200;
		return MIN_VALUE;
	}

	@Override
	public JComponent getDisplayComponent(
			SemanticAnnotationProfile semanticAnnotationProfile,
			Statement statement) {
		JComponent result = getInputComponent(semanticAnnotationProfile,
				statement);
		result.setEnabled(false);
		return result;
	}
}
