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

import static io.github.taverna_extras.component.ui.menu.component.ComponentMenuSection.COMPONENT_SECTION;

import java.net.URI;

import javax.swing.Action;
import org.apache.taverna.ui.menu.AbstractMenuAction;

/**
 * Basis for all menu actions. Intended to be configured by Spring.
 * 
 * @author Donal Fellows
 */
public class ComponentMenuAction extends AbstractMenuAction {
	/**
	 * Construct a menu action to appear within the "Components" menu.
	 * @param positionHint
	 *            Where on the menu this should come.
	 * @param id
	 *            How this should be identified to Taverna.
	 */
	public ComponentMenuAction(int positionHint, String id) {
		super(COMPONENT_SECTION, positionHint, URI.create(id));
	}

	private Action action;

	public void setAction(Action action) {
		this.action = action;
	}

	@Override
	protected Action createAction() {
		return action;
	}
}
