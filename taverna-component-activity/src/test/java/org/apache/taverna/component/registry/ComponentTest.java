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
import static org.junit.Assert.assertNull;

import java.net.URL;

import org.apache.taverna.component.api.Component;
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
public class ComponentTest extends Harness {
	private Family componentFamily;
	private Component component;
	private WorkflowBundle bundle;

	@Before
	public void setUp() throws Exception {
		URL dataflowUrl = getClass().getClassLoader().getResource(
				"beanshell_test.t2flow");
		assertNotNull(dataflowUrl);
		bundle = new WorkflowBundleIO().readBundle(dataflowUrl, null);
		URL componentProfileUrl = getClass().getClassLoader().getResource(
				"ValidationComponent.xml");
		assertNotNull(componentProfileUrl);
		Profile componentProfile = util
				.getProfile(componentProfileUrl);
		componentFamily = componentRegistry.createComponentFamily(
				"Test Component Family", componentProfile, "Some description",
				null, null);
		component = componentFamily.createComponentBasedOn("Test Component",
				"Some description", bundle).getComponent();
	}

	@After
	public void tearDown() throws Exception {
		componentRegistry.removeComponentFamily(componentFamily);
	}

	@Test
	public void testGetName() throws Exception {
		assertEquals("Test Component", component.getName());
		assertEquals("Test Component", component.getName());
	}

	@Test
	public void testGetComponentVersionMap() throws Exception {
		assertNotNull(component.getComponentVersionMap());
		assertEquals(1, component.getComponentVersionMap().size());
		assertEquals(component, component.getComponentVersionMap().get(1)
				.getComponent());
	}

	@Test
	public void testGetComponentVersion() throws Exception {
		assertNotNull(component.getComponentVersion(1));
		assertNull(component.getComponentVersion(2));
	}

	@Test
	public void testAddVersionBasedOn() throws Exception {
		assertNotNull(component.getComponentVersion(1));
		assertNull(component.getComponentVersion(2));
		Version componentVersion = component.addVersionBasedOn(bundle,
				"Some description");
		assertNotNull(componentVersion);
		assertEquals(component, componentVersion.getComponent());
		assertEquals(2, componentVersion.getVersionNumber().intValue());
		assertEquals(bundle.getIdentifier(), componentVersion.getImplementation()
				.getIdentifier());
	}

	@Test
	public void testGetComponentURL() throws Exception {
		assertNotNull(component.getComponentURL());
	}

}
