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

package io.github.taverna_extras.component.ui.view;

import static io.github.taverna_extras.component.api.config.ComponentConfig.URI;

import java.util.Arrays;
import java.util.List;

import io.github.taverna_extras.component.api.Version;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.ui.views.contextualviews.ContextualView;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.ContextualViewFactory;

public class ComponentContextViewFactory implements
		ContextualViewFactory<WorkflowBundle> {
	private FileManager fileManager;

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	@Override
	public boolean canHandle(Object selection) {
		if (selection instanceof WorkflowBundle) {
			Object dataflowSource = fileManager
					.getDataflowSource((WorkflowBundle) selection);
			//FIXME Is this right?
			return dataflowSource instanceof Version.ID;
		}
		return selection instanceof Activity
				&& ((Activity) selection).getType().equals(URI);
	}

	@Override
	public List<ContextualView> getViews(WorkflowBundle selection) {
		Object dataflowSource = fileManager.getDataflowSource(selection);
		return Arrays.<ContextualView> asList(new ComponentContextualView(
				(Version.ID) dataflowSource));
	}
}
