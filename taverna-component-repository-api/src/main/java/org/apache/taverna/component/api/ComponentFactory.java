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

import org.apache.taverna.component.api.profile.Profile;

public interface ComponentFactory {
	public Registry getRegistry(URL registryBase) throws ComponentException;

	public Family getFamily(URL registryBase, String familyName)
			throws ComponentException;

	public Component getComponent(URL registryBase, String familyName,
			String componentName) throws ComponentException;

	public Version getVersion(URL registryBase, String familyName,
			String componentName, Integer componentVersion)
			throws ComponentException;

	public Version getVersion(Version.ID ident) throws ComponentException;

	public Component getComponent(Version.ID ident) throws ComponentException;

	public Profile getProfile(URL url) throws ComponentException;

	public Profile getBaseProfile() throws ComponentException;
}
