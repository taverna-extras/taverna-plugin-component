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

package io.github.taverna_extras.component.ui.annotation;

import static java.lang.String.format;
import static org.apache.log4j.Logger.getLogger;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import io.github.taverna_extras.component.api.ComponentException;
import io.github.taverna_extras.component.api.ComponentFactory;
import io.github.taverna_extras.component.api.Family;
import io.github.taverna_extras.component.api.Registry;
import io.github.taverna_extras.component.api.Version;
import io.github.taverna_extras.component.api.profile.Profile;
import io.github.taverna_extras.component.api.profile.SemanticAnnotationProfile;

import org.apache.taverna.scufl2.api.common.AbstractNamed;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.workbench.file.FileManager;

/**
 * @author David Withers
 */
public class SemanticAnnotationContextualView extends
		AbstractSemanticAnnotationContextualView {
	private static final long serialVersionUID = -322165507536778154L;
	public static final String VIEW_TITLE = "Semantic Annotations";
	private static Logger logger = getLogger(SemanticAnnotationContextualView.class);

	private final FileManager fileManager;
	private final ComponentFactory factory;

	public SemanticAnnotationContextualView(FileManager fileManager,
			ComponentFactory factory, AbstractNamed selection) {
		super(fileManager, true);
		this.fileManager = fileManager;
		this.factory = factory;
		super.setAnnotated(selection);
		List<SemanticAnnotationProfile> profiles = new ArrayList<>();
		try {
			Profile componentProfile = getComponentProfile();
			if (componentProfile != null) {
				if (selection instanceof Workflow
						|| selection instanceof WorkflowBundle)
					profiles = componentProfile.getSemanticAnnotations();
				else if (selection instanceof InputWorkflowPort)
					profiles = componentProfile
							.getInputSemanticAnnotationProfiles();
				else if (selection instanceof OutputWorkflowPort)
					profiles = componentProfile
							.getOutputSemanticAnnotationProfiles();
				else if (selection instanceof Processor)
					profiles = componentProfile
							.getActivitySemanticAnnotationProfiles();
			}
		} catch (ComponentException e) {
			logger.error("failed to look up semantic annotations", e);
		}
		super.setSemanticAnnotationProfiles(profiles);
		super.initialise();
	}

	private Profile getComponentProfile() {
		Object dataflowSource = fileManager.getDataflowSource(fileManager
				.getCurrentDataflow());
		if (dataflowSource instanceof Version.ID) {
			Version.ID identification = (Version.ID) dataflowSource;
			try {
				Registry componentRegistry = factory.getRegistry(identification
						.getRegistryBase());
				Family componentFamily = componentRegistry
						.getComponentFamily(identification.getFamilyName());
				return componentFamily.getComponentProfile();
			} catch (ComponentException e) {
				logger.warn(
						format("No component profile found for component family %s at component registry %s",
								identification.getFamilyName(),
								identification.getRegistryBase()), e);
			}
		}
		return null;
	}

	@Override
	public String getViewTitle() {
		return VIEW_TITLE;
	}

/*
	public static void main(String[] args) throws Exception {
		JFrame frame = new JFrame();
		frame.setSize(400, 200);
		ComponentVersionIdentification identification = new ComponentVersionIdentification(
				new URL("http://sandbox.myexperiment.org"),
				"SCAPE Migration Action Components", "Image To Tiff", 2);
		Dataflow dataflow = fileManager.openDataflow(new ComponentFileType(),
				identification);

		Processor processor = edits.createProcessor("processor");
		try {
			editManager.doDataflowEdit(dataflow,
					edits.getAddProcessorEdit(dataflow, processor));
		} catch (EditException e) {
			e.printStackTrace();
		}
		final SemanticAnnotationContextualView view = new SemanticAnnotationContextualView(
				processor);
		editManager.addObserver(new Observer<EditManager.EditManagerEvent>() {
			@Override
			public void notify(Observable<EditManagerEvent> arg0,
					EditManagerEvent arg1) throws Exception {
				view.refreshView();
				view.repaint();
			}
		});
		frame.add(view);
		frame.setVisible(true);
	}
*/
}
