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

package org.apache.taverna.component.ui.menu.family;

import static org.apache.taverna.component.ui.menu.family.ComponentFamilyMenuSection.COMPONENT_FAMILY_SECTION;

import java.net.URI;

import javax.swing.Action;

import org.apache.taverna.component.ui.preference.ComponentPreference;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;
import org.apache.taverna.ui.menu.AbstractMenuAction;

/**
 * @author alanrw
 */
public class ComponentFamilyCreateMenuAction extends AbstractMenuAction {
	private static final URI COMPONENT_FAMILY_CREATE_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentFamilyCreate");

	private ComponentPreference prefs;
	private ComponentServiceIcon iconProvider;

	public ComponentFamilyCreateMenuAction() {
		super(COMPONENT_FAMILY_SECTION, 400, COMPONENT_FAMILY_CREATE_URI);
	}

	public void setPreferences(ComponentPreference prefs) {
		this.prefs = prefs;
	}

	public void setIcon(ComponentServiceIcon iconProvider) {
		this.iconProvider = iconProvider;
	}

	@Override
	protected Action createAction() {
		return new ComponentFamilyCreateAction(prefs, iconProvider);
	}
}
