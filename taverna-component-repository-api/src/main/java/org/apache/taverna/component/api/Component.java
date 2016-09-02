package org.apache.taverna.component.api;
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

import java.net.URL;
import java.util.SortedMap;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;

/**
 * The abstract interface supported by a component.
 * 
 * @author Donal Fellows
 * @author David Withers
 */
public interface Component extends NamedItem {
	/**
	 * @return the name of the Component.
	 */
	@Override
	String getName();

	/**
	 * Returns the URL for the Component.
	 * 
	 * @return the URL for the Component.
	 */
	URL getComponentURL();

	/**
	 * Creates a new version of this Component.
	 * 
	 * @param bundle
	 *            the workflow that the new ComponentVersion will use.
	 * @return a new version of this Component.
	 * @throws ComponentException
	 *             if there is a problem accessing the ComponentRegistry.
	 */
	Version addVersionBasedOn(WorkflowBundle bundle, String revisionComment)
			throws ComponentException;

	/**
	 * Returns the ComponentVersion that has the specified version number.
	 * 
	 * @param version
	 *            the version number of the ComponentVersion to return.
	 * @return the ComponentVersion that has the specified version number.
	 * @throws ComponentException
	 *             if there is a problem accessing the ComponentRegistry.
	 */
	Version getComponentVersion(Integer version) throws ComponentException;

	/**
	 * @return the description of the Component.
	 */
	@Override
	String getDescription();

	/**
	 * Returns a SortedMap of version number to ComponentVersion.
	 * <p>
	 * The returned map is sorted increasing numeric order.
	 * 
	 * @return a SortedMap of version number to ComponentVersion.
	 */
	SortedMap<Integer, Version> getComponentVersionMap();

	Registry getRegistry();

	Family getFamily();

	void delete() throws ComponentException;
}
