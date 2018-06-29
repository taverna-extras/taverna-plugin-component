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

import static org.apache.log4j.Logger.getLogger;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.log4j.Logger;
import io.github.taverna_extras.component.ui.serviceprovider.ComponentServiceIcon;
import io.github.taverna_extras.component.ui.util.Utils;
import org.apache.taverna.lang.observer.Observable;
import org.apache.taverna.lang.observer.Observer;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.file.events.FileManagerEvent;

/**
 * @author alanrw
 */
public class ComponentCloseAction extends AbstractAction implements
		Observer<FileManagerEvent> {
	private static final long serialVersionUID = -153986599735293879L;
	private static final String CLOSE_COMPONENT = "Close component";
	@SuppressWarnings("unused")
	private static Logger logger = getLogger(ComponentCloseAction.class);

	private Action closeAction;
	private final Utils utils;

	public ComponentCloseAction(Action closeWorkflowAction, FileManager fm,
			ComponentServiceIcon icon, Utils utils) {
		super(CLOSE_COMPONENT, icon.getIcon());
		closeAction = closeWorkflowAction;
		this.utils = utils;
		fm.addObserver(this);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		closeAction.actionPerformed(arg0);
	}

	@Override
	public boolean isEnabled() {
		return utils.currentDataflowIsComponent();
	}

	@Override
	public void notify(Observable<FileManagerEvent> sender,
			FileManagerEvent message) throws Exception {
		setEnabled(utils.currentDataflowIsComponent());
	}
}
