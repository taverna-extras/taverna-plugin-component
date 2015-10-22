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

import java.net.URI;

import javax.swing.Action;

import org.apache.taverna.component.ui.preference.ComponentPreference;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;
import org.apache.taverna.component.ui.util.Utils;
import org.apache.taverna.ui.menu.AbstractMenuAction;
import org.apache.taverna.workbench.file.FileManager;

/**
 * @author alanrw
 */
public class ComponentFamilyDeleteMenuAction extends AbstractMenuAction {
	private static final URI COMPONENT_FAMILY_DELETE_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentFamilyDelete");

	private FileManager fm;
	private ComponentPreference prefs;
	private ComponentServiceIcon icon;
	private Utils utils;

	public ComponentFamilyDeleteMenuAction() {
		super(ComponentFamilyMenuSection.COMPONENT_FAMILY_SECTION, 500,
				COMPONENT_FAMILY_DELETE_URI);
	}

	public void setFileManager(FileManager fm) {
		this.fm = fm;
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}
	
	public void setPreferences(ComponentPreference prefs) {
		this.prefs = prefs;
	}

	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	@Override
	protected Action createAction() {
		return new ComponentFamilyDeleteAction(fm, prefs, icon, utils);
	}
}
