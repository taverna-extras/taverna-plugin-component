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

package io.github.taverna_extras.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

import io.github.taverna_extras.component.api.ComponentFactory;
import io.github.taverna_extras.component.ui.preference.ComponentPreference;
import io.github.taverna_extras.component.ui.serviceprovider.ComponentServiceIcon;
import org.apache.taverna.services.ServiceRegistry;
import org.apache.taverna.ui.menu.MenuManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.selection.SelectionManager;

/**
 * @author alanrw
 */
public class ComponentSearchMenuAction extends AbstractComponentMenuAction {
	private static final URI SEARCH_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentSearch");
	private ComponentPreference prefs;
	private ComponentFactory factory;
	private EditManager em;
	private MenuManager mm;
	private SelectionManager sm;
	private ServiceRegistry serviceRegistry;
	private ComponentServiceIcon icon;

	public ComponentSearchMenuAction() {
		super(1500, SEARCH_COMPONENT_URI);
	}

	public void setPreferences(ComponentPreference prefs) {
		this.prefs = prefs;
	}

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
	}

	public void setEditManager(EditManager em) {
		this.em = em;
	}

	public void setMenuManager(MenuManager mm) {
		this.mm = mm;
	}

	public void setSelectionManager(SelectionManager sm) {
		this.sm = sm;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}

	@Override
	protected Action createAction() {
		return new ComponentSearchAction(prefs, factory, em, mm, sm,
				serviceRegistry, icon);
	}
}
