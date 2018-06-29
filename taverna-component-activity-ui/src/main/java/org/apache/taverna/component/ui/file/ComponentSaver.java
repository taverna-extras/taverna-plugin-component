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

package io.github.taverna_extras.component.ui.file;

import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static org.apache.log4j.Logger.getLogger;
import static io.github.taverna_extras.component.ui.annotation.SemanticAnnotationUtils.checkComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;
import io.github.taverna_extras.component.api.Component;
import io.github.taverna_extras.component.api.ComponentException;
import io.github.taverna_extras.component.api.ComponentFactory;
import io.github.taverna_extras.component.api.Family;
import io.github.taverna_extras.component.api.Registry;
import io.github.taverna_extras.component.api.Version;
import io.github.taverna_extras.component.api.profile.SemanticAnnotationProfile;
import io.github.taverna_extras.component.ui.serviceprovider.ComponentServiceProvider;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.validation.ValidationReport;
import org.apache.taverna.scufl2.validation.structural.StructuralValidator;
import org.apache.taverna.workbench.file.AbstractDataflowPersistenceHandler;
import org.apache.taverna.workbench.file.DataflowInfo;
import org.apache.taverna.workbench.file.DataflowPersistenceHandler;
import org.apache.taverna.workbench.file.FileType;
import org.apache.taverna.workbench.file.exceptions.SaveException;

/**
 * @author alanrw
 */
public class ComponentSaver extends AbstractDataflowPersistenceHandler
		implements DataflowPersistenceHandler {
	private static final String UNSATISFIED_PROFILE_WARNING = "The component does not satisfy the profile.\n"
			+ "See validation report.\nDo you still want to save?";
	private static final Logger logger = getLogger(ComponentSaver.class);

	private ComponentFactory factory;
	private ComponentServiceProvider provider;
	private FileType cft;

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
	}

	public void setFileType(FileType fileType) {
		this.cft = fileType;
	}

	public void setServiceProvider(ComponentServiceProvider provider) {
		this.provider = provider;
	}

	@Override
	public DataflowInfo saveDataflow(WorkflowBundle bundle, FileType fileType,
			Object destination) throws SaveException {
		if (!getSaveFileTypes().contains(fileType))
			throw new IllegalArgumentException("Unsupported file type "
					+ fileType);
		if (!(destination instanceof Version.ID))
			throw new IllegalArgumentException("Unsupported destination type "
					+ destination.getClass().getName());

		ValidationReport structuralValidity = new StructuralValidator()
				.validate(bundle);
		if (structuralValidity.detectedProblems())
			throw new SaveException(
					"Cannot save a structurally invalid workflow as a component",
					structuralValidity.getException());

		/*
		 * Saving an invalid dataflow is OK. Validity check is done to get
		 * predicted depth for output (if possible)
		 */

		Version.ID ident = (Version.ID) destination;

		if (ident.getComponentVersion() == -1) {
			Version.ID newIdent = new Version.Identifier(
					ident.getRegistryBase(), ident.getFamilyName(),
					ident.getComponentName(), 0);
			return new DataflowInfo(cft, newIdent, bundle);
		}

		Family family;
		try {
			Registry registry = factory.getRegistry(ident.getRegistryBase());
			family = registry.getComponentFamily(ident.getFamilyName());
		} catch (ComponentException e) {
			throw new SaveException("Unable to read component", e);
		}

		Version newVersion = null;
		try {
			List<SemanticAnnotationProfile> problemProfiles = new ArrayList<>(
					checkComponent(bundle, family.getComponentProfile()));

			if (!problemProfiles.isEmpty()) {
				int answer = showConfirmDialog(null,
						UNSATISFIED_PROFILE_WARNING, "Profile problem",
						OK_CANCEL_OPTION);
				if (answer != OK_OPTION)
					throw new SaveException("Saving cancelled");
			}

			JTextArea descriptionArea = new JTextArea(10, 60);
			descriptionArea.setLineWrap(true);
			descriptionArea.setWrapStyleWord(true);
			final JScrollPane descriptionScrollPane = new JScrollPane(
					descriptionArea);
			if (ident.getComponentVersion() == 0) {
				int answer = showConfirmDialog(null, descriptionScrollPane,
						"Component description", OK_CANCEL_OPTION);
				if (answer != OK_OPTION)
					throw new SaveException("Saving cancelled");
				newVersion = family.createComponentBasedOn(
						ident.getComponentName(), descriptionArea.getText(),
						bundle);
			} else {
				Component component = family.getComponent(ident
						.getComponentName());
				int answer = showConfirmDialog(null, descriptionScrollPane,
						"Version description", OK_CANCEL_OPTION);
				if (answer != OK_OPTION)
					throw new SaveException("Saving cancelled");
				newVersion = component.addVersionBasedOn(bundle,
						descriptionArea.getText());
			}
		} catch (ComponentException e) {
			logger.error("Unable to save new version of component", e);
			throw new SaveException("Unable to save new version of component",
					e);
		}

		Version.ID newIdent = new Version.Identifier(ident.getRegistryBase(),
				ident.getFamilyName(), ident.getComponentName(),
				newVersion.getVersionNumber());
		provider.refreshProvidedComponent(ident);
		return new DataflowInfo(cft, newIdent, bundle);
	}

	@Override
	public List<FileType> getSaveFileTypes() {
		return Arrays.<FileType> asList(cft);
	}

	@Override
	public List<Class<?>> getSaveDestinationTypes() {
		return Arrays.<Class<?>> asList(Version.ID.class);
	}

	@Override
	public boolean wouldOverwriteDataflow(WorkflowBundle dataflow,
			FileType fileType, Object destination, DataflowInfo lastDataflowInfo) {
		if (!getSaveFileTypes().contains(fileType))
			throw new IllegalArgumentException("Unsupported file type "
					+ fileType);
		return false;
	}
}
