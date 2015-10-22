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

package org.apache.taverna.component.ui.view;

import static java.lang.String.format;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JEditorPane;

import org.apache.taverna.component.api.Version;
import org.apache.taverna.lang.ui.HtmlUtils;
import org.apache.taverna.workbench.configuration.colour.ColourManager;
import org.apache.taverna.workbench.ui.views.contextualviews.ContextualView;

@SuppressWarnings("serial")
public class ComponentContextualView extends ContextualView {
	private JEditorPane editorPane;
	private final Version.ID component;
	ColourManager colourManager;//FIXME beaninject
	ViewUtil viewUtils;//FIXME beaninject;

	public ComponentContextualView(Version.ID component) {
		this.component = component;
		initView();
	}

	@Override
	public JComponent getMainFrame() {
		editorPane = HtmlUtils.createEditorPane(buildHtml());
		return HtmlUtils.panelForHtml(editorPane);
	}

	private String buildHtml() {
		StringBuilder html = new StringBuilder(HtmlUtils.getHtmlHead(getBackgroundColour()));
		html.append(HtmlUtils.buildTableOpeningTag());
		viewUtils.getRawTablesHtml(component, html);
		return html.append("</table></body></html>").toString();
	}

	public String getBackgroundColour() {
		Color colour = colourManager.getPreferredColour(
				"org.apache.taverna.component.registry.Component");
		return format("#%1$2x%2$2x%3$2x", colour.getRed(), colour.getGreen(),
				colour.getBlue());
	}

	@Override
	public int getPreferredPosition() {
		return 50;
	}

	private static int MAX_LENGTH = 50;

	private String limitName(String fullName) {
		if (fullName.length() > MAX_LENGTH)
			return fullName.substring(0, MAX_LENGTH - 3) + "...";
		return fullName;
	}

	@Override
	public String getViewTitle() {
		return "Component " + limitName(component.getComponentName());
	}

	@Override
	public void refreshView() {
		editorPane.setText(buildHtml());
		repaint();
	}
}
