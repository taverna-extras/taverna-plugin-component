package io.github.taverna_extras.component.registry.local;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import static java.lang.Integer.parseInt;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.log4j.Logger.getLogger;
import static io.github.taverna_extras.component.registry.local.LocalComponent.COMPONENT_FILENAME;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import io.github.taverna_extras.component.api.ComponentException;
import io.github.taverna_extras.component.registry.ComponentVersion;
import io.github.taverna_extras.component.utils.SystemUtils;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;

/**
 * @author alanrw
 * 
 */
class LocalComponentVersion extends ComponentVersion {
	private static Logger logger = getLogger(LocalComponentVersion.class);

	private final File componentVersionDir;
	private SystemUtils system;

	protected LocalComponentVersion(LocalComponent component,
			File componentVersionDir, SystemUtils system) {
		super(component);
		this.componentVersionDir = componentVersionDir;
		this.system = system;
	}

	@Override
	protected final String internalGetDescription() {
		File descriptionFile = new File(componentVersionDir, "description");
		try {
			if (descriptionFile.isFile())
				return readFileToString(descriptionFile);
		} catch (IOException e) {
			logger.error("failed to get description from " + descriptionFile, e);
		}
		return "";
	}

	@Override
	protected final Integer internalGetVersionNumber() {
		return parseInt(componentVersionDir.getName());
	}

	@Override
	protected final WorkflowBundle internalGetImplementation()
			throws ComponentException {
		File filename = new File(componentVersionDir, COMPONENT_FILENAME);
		try {
			return system.getBundle(filename);
		} catch (Exception e) {
			logger.error(
					"failed to get component realization from " + filename, e);
			throw new ComponentException("Unable to open dataflow", e);
		}
	}

	@Override
	public int hashCode() {
		return 31 + ((componentVersionDir == null) ? 0 : componentVersionDir
				.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocalComponentVersion other = (LocalComponentVersion) obj;
		if (componentVersionDir == null)
			return (other.componentVersionDir == null);
		return componentVersionDir.equals(other.componentVersionDir);
	}

	@Override
	public URL getHelpURL() {
		return null;
	}
}
