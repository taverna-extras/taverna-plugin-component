/*******************************************************************************
 * Copyright (C) 2012 The University of Manchester
 *
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package org.apache.taverna.component.ui.annotation;

import static java.lang.String.format;
import static org.apache.log4j.Logger.getLogger;

import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.workbench.file.FileManager;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.ComponentFactory;
import org.apache.taverna.component.api.Family;
import org.apache.taverna.component.api.Registry;
import org.apache.taverna.component.api.Version;
import org.apache.taverna.component.api.profile.Profile;
import org.apache.taverna.component.api.profile.SemanticAnnotationProfile;

import org.apache.taverna.scufl2.api.common.AbstractNamed;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;

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
