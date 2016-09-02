package org.apache.taverna.component.registry;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;

import org.apache.taverna.component.api.Family;
import org.apache.taverna.component.api.Version;
import org.apache.taverna.component.api.profile.Profile;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;

/**
 * 
 * 
 * @author David Withers
 */
@Ignore
public class ComponentVersionTest extends Harness {
	private Family componentFamily;
	private WorkflowBundle bundle;
	private Version componentVersion;

	@Before
	public void setUp() throws Exception {
		URL dataflowUrl = getClass().getClassLoader().getResource(
				"beanshell_test.t2flow");
		assertNotNull(dataflowUrl);
		bundle = new WorkflowBundleIO().readBundle(dataflowUrl, null);
		URL componentProfileUrl = getClass().getClassLoader().getResource(
				"ValidationComponent.xml");
		assertNotNull(componentProfileUrl);
		Profile componentProfile = util.getProfile(componentProfileUrl);
		componentFamily = componentRegistry.createComponentFamily(
				"Test Component Family", componentProfile, "Some description",
				null, null);
		componentVersion = componentFamily.createComponentBasedOn(
				"Test Component", "Some description", bundle);
	}

	@After
	public void tearDown() throws Exception {
		componentRegistry.removeComponentFamily(componentFamily);
	}

	@Test
	public void testGetVersionNumber() throws Exception {
		assertNotNull(componentVersion.getVersionNumber());
		assertEquals(componentVersion.getVersionNumber(),
				componentVersion.getVersionNumber());
	}

	@Test
	public void testGetDescription() throws Exception {
		assertNotNull(componentVersion.getDescription());
	}

	@Test
	public void testGetDataflow() throws Exception {
		assertNotNull(componentVersion.getImplementation());
		assertEquals(bundle.getIdentifier(), componentVersion.getImplementation()
				.getIdentifier());
	}

	@Test
	public void testGetComponent() throws Exception {
		assertNotNull(componentVersion.getComponent());
	}

}
