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

package io.github.taverna_extras.component.ui.menu;

import java.net.URI;

import javax.swing.Action;

import io.github.taverna_extras.component.api.ComponentFactory;
import io.github.taverna_extras.component.ui.preference.ComponentPreference;
import io.github.taverna_extras.component.ui.serviceprovider.ComponentServiceIcon;

import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.ui.menu.AbstractContextualMenuAction;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.selection.SelectionManager;

/**
 * @author alanrw
 */
public class ReplaceByComponentMenuAction extends AbstractContextualMenuAction {
	private static final URI configureSection = URI
			.create("http://taverna.sf.net/2009/contextMenu/configure");

	private ComponentPreference preferences;
	private EditManager editManager;
	private SelectionManager selectionManager;
	private ComponentFactory factory;
	private ComponentServiceIcon icon;

	public ReplaceByComponentMenuAction() {
		super(configureSection, 75);
	}

	public void setPreferences(ComponentPreference preferences) {
		this.preferences = preferences;
	}

	public void setEditManager(EditManager editManager) {
		this.editManager = editManager;
	}

	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}

	@Override
	public boolean isEnabled() {
		Object selection = getContextualSelection().getSelection();
		if (!super.isEnabled())
			return false;
		return (selection instanceof Processor);
	}

	@Override
	protected Action createAction() {
		ReplaceByComponentAction action = new ReplaceByComponentAction(
				preferences, factory, editManager, selectionManager, icon);
		action.setSelection((Processor) getContextualSelection().getSelection());
		return action;
	}
}
