package io.github.taverna_extras.component.registry;
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


import static java.util.Collections.synchronizedSortedMap;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.SortedMap;
import java.util.TreeMap;

import io.github.taverna_extras.component.api.ComponentException;
import io.github.taverna_extras.component.api.Version;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;

/**
 * A Component is a building block for creating Taverna workflows. Components
 * and must comply with the ComponentProfile of their ComponentFamily.
 * 
 * @author David Withers
 */
public abstract class Component implements
		io.github.taverna_extras.component.api.Component {
	private String name;
	private String description;
	private URL url;
	/**
	 * Mapping from version numbers to version implementations.
	 */
	protected SortedMap<Integer, Version> versionMap = new TreeMap<>();

	protected Component(URL url) {
		this.url = url;
	}
	
	protected Component(String url) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			// nothing
		}
	}

	protected Component(File fileDir) {
		try {
			this.url = fileDir.toURI().toURL();
		} catch (MalformedURLException e) {
			// nothing
		}
	}

	@Override
	public final synchronized String getName() {
		if (name == null)
			name = internalGetName();
		return name;
	}

	/**
	 * The real implementation of the name fetching. Caching already handled.
	 * 
	 * @return The name of the component.
	 */
	protected abstract String internalGetName();

	@Override
	public final synchronized String getDescription() {
		if (description == null)
			description = internalGetDescription();
		return description;
	}

	/**
	 * The real implementation of the description fetching. Caching already
	 * handled.
	 * 
	 * @return The description of the component.
	 */
	protected abstract String internalGetDescription();

	@Override
	public final SortedMap<Integer, Version> getComponentVersionMap() {
		synchronized (versionMap) {
			checkComponentVersionMap();
			return synchronizedSortedMap(versionMap);
		}
	}

	private void checkComponentVersionMap() {
		if (versionMap.isEmpty())
			populateComponentVersionMap();
	}

	/**
	 * Create the contents of the {@link #versionMap} field.
	 */
	protected abstract void populateComponentVersionMap();

	@Override
	public final Version getComponentVersion(Integer version)
			throws ComponentException {
		synchronized (versionMap) {
			checkComponentVersionMap();
			return versionMap.get(version);
		}
	}

	@Override
	public final Version addVersionBasedOn(WorkflowBundle bundle,
			String revisionComment) throws ComponentException {
		Version result = internalAddVersionBasedOn(bundle, revisionComment);
		synchronized (versionMap) {
			checkComponentVersionMap();
			versionMap.put(result.getVersionNumber(), result);
		}
		return result;
	}

	/**
	 * Manufacture a new version of a component. Does not add to the overall
	 * version map.
	 * 
	 * @param bundle
	 *            The definition of the component.
	 * @param revisionComment
	 *            The description of the version.
	 * @return The new version of the component.
	 * @throws RegistryException
	 */
	protected abstract Version internalAddVersionBasedOn(WorkflowBundle bundle,
			String revisionComment) throws ComponentException;

	@Override
	public final URL getComponentURL() {
		return url;
	}

	@Override
	public void delete() throws ComponentException {
		getFamily().removeComponent(this);
	}
}
