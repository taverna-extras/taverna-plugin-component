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

package org.apache.taverna.component.ui.menu;

import java.net.URI;

import org.apache.taverna.component.api.config.ComponentConfig;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.ui.menu.AbstractContextualMenuAction;

public abstract class AbstractContextComponentMenuAction extends AbstractContextualMenuAction {
	public AbstractContextComponentMenuAction(URI parentId, int positionHint) {
		super(parentId, positionHint);
	}

	public AbstractContextComponentMenuAction(URI parentId, int positionHint, URI id) {
		super(parentId, positionHint, id);
	}

	protected boolean isComponentActivity(Activity act) {
		if (act == null)
			return false;
		return act.getType().equals(ComponentConfig.URI);
	}

	protected Activity findActivity() {
		if (getContextualSelection() == null)
			return null;
		Object selection = getContextualSelection().getSelection();
		if (selection instanceof Processor) {
			Processor processor = (Processor) selection;
			return processor.getParent().getParent().getMainProfile()
					.getProcessorBindings().getByName(processor.getName())
					.getBoundActivity();
		} else if (selection instanceof Activity)
			return (Activity) selection;
		return null;
	}
}
