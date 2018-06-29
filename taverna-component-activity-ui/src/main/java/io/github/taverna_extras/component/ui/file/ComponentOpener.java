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

import static org.apache.log4j.Logger.getLogger;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import io.github.taverna_extras.component.api.ComponentException;
import io.github.taverna_extras.component.api.ComponentFactory;
import io.github.taverna_extras.component.api.Version;
import io.github.taverna_extras.component.api.Version.ID;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.workbench.file.AbstractDataflowPersistenceHandler;
import org.apache.taverna.workbench.file.DataflowInfo;
import org.apache.taverna.workbench.file.DataflowPersistenceHandler;
import org.apache.taverna.workbench.file.FileType;
import org.apache.taverna.workbench.file.exceptions.OpenException;

/**
 * @author alanrw
 */
public class ComponentOpener extends AbstractDataflowPersistenceHandler
		implements DataflowPersistenceHandler {
	private static Logger logger = getLogger(ComponentOpener.class);

	private ComponentFactory factory;
	private FileType fileType;

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}
	
	@Override
	public DataflowInfo openDataflow(FileType fileType, Object source)
			throws OpenException {
		if (!getOpenFileTypes().contains(fileType))
			throw new IllegalArgumentException("Unsupported file type "
					+ fileType);
		if (!(source instanceof Version.ID))
			throw new IllegalArgumentException("Unsupported source type "
					+ source.getClass().getName());

		WorkflowBundle d;
		try {
			d = factory.getVersion((ID) source).getImplementation();
		} catch (ComponentException e) {
			logger.error("Unable to read dataflow", e);
			throw new OpenException("Unable to read dataflow", e);
		}
		return new DataflowInfo(fileType, source, d, new Date());
	}

	@Override
	public List<FileType> getOpenFileTypes() {
		return Arrays.<FileType> asList(fileType);
	}

	@Override
	public List<Class<?>> getOpenSourceTypes() {
		return Arrays.<Class<?>> asList(Version.ID.class);
	}
}
