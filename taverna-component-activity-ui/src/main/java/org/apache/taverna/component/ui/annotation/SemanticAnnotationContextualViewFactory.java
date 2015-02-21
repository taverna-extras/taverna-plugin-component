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

import java.util.Arrays;
import java.util.List;

import org.apache.taverna.component.api.ComponentFactory;
import org.apache.taverna.component.api.Version;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.port.ActivityPort;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ContextualViewFactory;

/**
 * @author David Withers
 */
public class SemanticAnnotationContextualViewFactory implements
		ContextualViewFactory<AbstractNamed> {
	private FileManager fileManager;
	private ComponentFactory factory;

	private WorkflowBundle bundle;

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
	}

	public void setFileManager(FileManager fm) {
		this.fileManager = fm;
	}

	@Override
	public boolean canHandle(Object selection) {
		bundle = fileManager.getCurrentDataflow();
		return fileManager.getDataflowSource(bundle) instanceof Version.ID
				&& selection instanceof AbstractNamed
				&& !(selection instanceof Activity || selection instanceof ActivityPort);
	}

	@Override
	public List<ContextualView> getViews(AbstractNamed selection) {
		return Arrays.asList(new SemanticAnnotationContextualView(fileManager,
				factory, selection), new TurtleContextualView(selection, bundle));
	}
}
