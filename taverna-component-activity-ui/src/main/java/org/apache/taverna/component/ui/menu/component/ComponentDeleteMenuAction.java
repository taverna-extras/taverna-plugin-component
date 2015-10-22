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

package org.apache.taverna.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

import org.apache.taverna.component.ui.preference.ComponentPreference;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;
import org.apache.taverna.component.ui.util.Utils;
import org.apache.taverna.workbench.file.FileManager;

/**
 * @author alanrw
 */
public class ComponentDeleteMenuAction extends AbstractComponentMenuAction {
	private static final URI DELETE_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentDelete");

	private FileManager fm;
	private ComponentServiceIcon icon;
	private ComponentPreference prefs;
	private Utils utils;

	public ComponentDeleteMenuAction() {
		super(1200, DELETE_COMPONENT_URI);
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
		return new ComponentDeleteAction(fm, prefs, icon, utils);
	}
}
