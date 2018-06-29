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

import io.github.taverna_extras.component.ui.serviceprovider.ComponentServiceIcon;
import io.github.taverna_extras.component.ui.util.Utils;
import org.apache.taverna.workbench.file.FileManager;

/**
 * @author alanrw
 */
public class ComponentCloseMenuAction extends AbstractComponentMenuAction {
	private static final URI CLOSE_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentClose");

	private Action action;
	private FileManager fm;
	private ComponentServiceIcon icon;
	private Utils utils;

	public ComponentCloseMenuAction() {
		super(1000, CLOSE_COMPONENT_URI);
	}
	
	public void setCloseWorkflowAction(Action action) {
		this.action = action;
	}

	public void setFileManager(FileManager fm) {
		this.fm = fm;
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}

	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	@Override
	protected Action createAction() {
		return new ComponentCloseAction(action, fm, icon, utils);
	}
}
