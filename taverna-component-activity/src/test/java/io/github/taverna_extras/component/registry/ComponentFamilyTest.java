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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import io.github.taverna_extras.component.api.Family;
import io.github.taverna_extras.component.api.Version;
import io.github.taverna_extras.component.api.profile.Profile;
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
public class ComponentFamilyTest extends Harness {
	private Family componentFamily;
	private Profile componentProfile;
	private WorkflowBundle bundle;

	@Before
	public void setup() throws Exception {
		URL componentProfileUrl = getClass().getClassLoader().getResource(
				"ValidationComponent.xml");
		assertNotNull(componentProfileUrl);
		componentProfile = util.getProfile(componentProfileUrl);
		componentRegistry.addComponentProfile(componentProfile, null, null);
		URL dataflowUrl = getClass().getClassLoader().getResource(
				"beanshell_test.t2flow");
		assertNotNull(dataflowUrl);
		bundle = new WorkflowBundleIO().readBundle(dataflowUrl, null);
		componentFamily = componentRegistry.createComponentFamily(
				"Test Component Family", componentProfile, "Some description",
				null, null);
	}

	@After
	public void tearDown() throws Exception {
		componentRegistry.removeComponentFamily(componentFamily);
	}

	@Test
	public void testGetComponentRegistry() throws Exception {
		assertEquals(componentRegistry, componentFamily.getComponentRegistry());
	}

	@Test
	public void testGetName() throws Exception {
		assertEquals("Test Component Family", componentFamily.getName());
		assertEquals("Test Component Family", componentFamily.getName());
	}

	@Test
	public void testGetComponentProfile() throws Exception {
		Profile componentProfile2 = componentFamily.getComponentProfile();
		assertNotNull(componentProfile2);
		String id = componentProfile.getId();
		String id2 = componentProfile2.getId();
		assertEquals(id, id2);
	}

	@Test
	public void testGetComponents() throws Exception {
		assertEquals(0, componentFamily.getComponents().size());
		assertEquals(0, componentFamily.getComponents().size());
		Version componentVersion = componentFamily.createComponentBasedOn(
				"Test Component", "Some description", bundle);
		assertEquals(1, componentFamily.getComponents().size());
		assertTrue(componentFamily.getComponents().contains(
				componentVersion.getComponent()));
		// componentFamily.removeComponent(componentVersion.getComponent());
		// assertEquals(0, componentFamily.getComponents().size());
	}

	@Test
	public void testCreateComponentBasedOn() throws Exception {
		Version componentVersion = componentFamily.createComponentBasedOn(
				"Test Component", "Some description", bundle);
		assertEquals("Test Component", componentVersion.getComponent()
				.getName());
	}

	@Test
	public void testGetComponent() throws Exception {
		assertNull(componentFamily.getComponent("Test Component"));
		Version componentVersion = componentFamily.createComponentBasedOn(
				"Test Component", "Some description", bundle);
		assertNotNull(componentFamily.getComponent("Test Component"));
		assertEquals(componentVersion.getComponent(),
				componentFamily.getComponent("Test Component"));
	}

}
