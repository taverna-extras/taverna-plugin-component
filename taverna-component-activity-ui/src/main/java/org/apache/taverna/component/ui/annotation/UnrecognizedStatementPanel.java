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

import static java.lang.String.format;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hp.hpl.jena.rdf.model.Statement;

/**
 * @author alanrw
 * 
 */
@SuppressWarnings("serial")
public class UnrecognizedStatementPanel extends JPanel {
	public UnrecognizedStatementPanel(Statement statement) {
		setLayout(new BorderLayout());
		setBorder(new GreyBorder());
		add(new JLabel(format("Unable to find %s in the profile",
				statement.getPredicate())));
	}
}
