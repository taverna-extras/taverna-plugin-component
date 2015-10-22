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

import org.apache.taverna.component.ui.menu.AbstractContextComponentMenuAction;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.workbench.selection.SelectionManager;

/**
 * @author alanrw
 */
public class ComponentServiceCreatorMenuAction extends
		AbstractContextComponentMenuAction {
	private static final URI configureSection = URI
			.create("http://taverna.sf.net/2009/contextMenu/configure");

	private ComponentCreatorSupport support;
	private SelectionManager sm;
	private ComponentServiceIcon icon;

	public ComponentServiceCreatorMenuAction() {
		super(configureSection, 60);
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}

	public void setSelectionManager(SelectionManager sm) {
		this.sm = sm;
	}
	
	public void setSupport(ComponentCreatorSupport support) {
		this.support = support;
	}

	@Override
	public boolean isEnabled() {
		if (!super.isEnabled())
			return false;
		Activity a = findActivity();
		if (a == null)
			return false;
		return !isComponentActivity(a);
	}

	@Override
	protected Action createAction() {
		return new ComponentServiceCreatorAction(
				(Processor) getContextualSelection().getSelection(), sm,
				support, icon);
	}
}
