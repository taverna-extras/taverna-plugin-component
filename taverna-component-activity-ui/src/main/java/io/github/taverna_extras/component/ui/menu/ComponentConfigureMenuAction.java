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

import static javax.swing.Action.NAME;
import static io.github.taverna_extras.component.ui.ComponentConstants.ACTIVITY_URI;

import javax.swing.Action;

import io.github.taverna_extras.component.api.ComponentFactory;
import io.github.taverna_extras.component.ui.config.ComponentConfigureAction;
import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.services.ServiceRegistry;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.activitytools.AbstractConfigureActivityMenuAction;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;

public class ComponentConfigureMenuAction extends
		AbstractConfigureActivityMenuAction {
	public ComponentConfigureMenuAction() {
		super(ACTIVITY_URI);
	}

	private ActivityIconManager aim;
	private ServiceDescriptionRegistry sdr;
	private EditManager em;
	private FileManager fm;
	private ServiceRegistry str;
	private ComponentFactory factory;

	public void setActivityIconManager(ActivityIconManager aim) {
		this.aim = aim;
	}

	public void setServiceDescriptionRegistry(ServiceDescriptionRegistry sdr) {
		this.sdr = sdr;
	}

	public void setEditManager(EditManager em) {
		this.em = em;
	}

	public void setFileManager(FileManager fm) {
		this.fm = fm;
	}

	public void setServiceTypeRegistry(ServiceRegistry str) {
		this.str = str;
	}

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
	}

	@Override
	protected Action createAction() {
		Action result = new ComponentConfigureAction(findActivity(),
				getParentFrame(), factory, aim, sdr, em, fm, str);
		result.putValue(NAME, "Configure component");
		addMenuDots(result);
		return result;
	}
}
