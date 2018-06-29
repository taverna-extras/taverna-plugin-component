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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.List;

import io.github.taverna_extras.component.api.ComponentException;
import io.github.taverna_extras.component.api.Family;
import io.github.taverna_extras.component.api.profile.Profile;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * 
 * @author David Withers
 */
@Ignore
public abstract class ComponentRegistryTest extends Harness {
	private Profile componentProfile;

	@Before
	public void setup() throws Exception {
		URL componentProfileUrl = getClass().getClassLoader().getResource(
				"ValidationComponent.xml");
		assertNotNull(componentProfileUrl);
		componentProfile = util.getProfile(componentProfileUrl);
	}

	@After
	public void tearDown() throws Exception {
		for (Family componentFamily : componentRegistry.getComponentFamilies()) {
			componentRegistry.removeComponentFamily(componentFamily);
		}
	}

	@Test
	public void testGetComponentFamilies() throws Exception {
		assertEquals(0, componentRegistry.getComponentFamilies().size());
		assertEquals(0, componentRegistry.getComponentFamilies().size());
		Family componentFamily = componentRegistry.createComponentFamily(
				"TestComponentFamily", componentProfile, "Some description",
				null, null);
		assertEquals(1, componentRegistry.getComponentFamilies().size());
		assertTrue(componentRegistry.getComponentFamilies().contains(
				componentFamily));
		componentRegistry.removeComponentFamily(componentFamily);
		assertEquals(0, componentRegistry.getComponentFamilies().size());
	}

	@Test
	public void testGetComponentFamily() throws Exception {
		assertNull(componentRegistry.getComponentFamily("TestComponentFamily"));
		assertNull(componentRegistry.getComponentFamily("TestComponentFamily"));
		Family componentFamily = componentRegistry.createComponentFamily(
				"TestComponentFamily", componentProfile, "Some description",
				null, null);
		assertNotNull(componentRegistry
				.getComponentFamily("TestComponentFamily"));
		assertNotNull(componentRegistry
				.getComponentFamily("TestComponentFamily"));
		assertEquals(componentFamily,
				componentRegistry.getComponentFamily("TestComponentFamily"));
		componentRegistry.removeComponentFamily(componentFamily);
		assertNull(componentRegistry.getComponentFamily("TestComponentFamily"));
	}

	@Test
	public void testCreateComponentFamily() throws Exception {
		assertEquals(0, componentRegistry.getComponentFamilies().size());
		assertNull(componentRegistry.getComponentFamily("TestComponentFamily"));
		Family componentFamily = componentRegistry.createComponentFamily(
				"TestComponentFamily", componentProfile, "Some description",
				null, null);
		assertEquals("TestComponentFamily", componentFamily.getName());
		assertEquals(componentRegistry, componentFamily.getComponentRegistry());
		assertEquals(0, componentFamily.getComponents().size());
		// assertEquals(componentProfile,
		// componentFamily.getComponentProfile());
		assertEquals(1, componentRegistry.getComponentFamilies().size());
		assertNotNull(componentRegistry
				.getComponentFamily("TestComponentFamily"));
		assertEquals(componentFamily,
				componentRegistry.getComponentFamily("TestComponentFamily"));
	}

	@Test(expected = ComponentException.class)
	public void testCreateComponentFamilyDuplicate() throws Exception {
		componentRegistry.createComponentFamily("TestComponentFamily",
				componentProfile, "Some description", null, null);
		componentRegistry.createComponentFamily("TestComponentFamily",
				componentProfile, "Some description", null, null);
	}

	@Test(expected = ComponentException.class)
	public void testCreateComponentFamilyNullProfile() throws Exception {
		componentRegistry.createComponentFamily("TestComponentFamily", null,
				"Some description", null, null);
	}

	@Test(expected = ComponentException.class)
	public void testCreateComponentFamilyNullName() throws Exception {
		componentRegistry.createComponentFamily(null, componentProfile,
				"Some description", null, null);
	}

	@Test
	public void testRemoveComponentFamily() throws Exception {
		assertEquals(0, componentRegistry.getComponentFamilies().size());
		assertNull(componentRegistry.getComponentFamily("TestComponentFamily"));
		Family componentFamily = componentRegistry.createComponentFamily(
				"TestComponentFamily", componentProfile, "Some description",
				null, null);
		assertEquals(1, componentRegistry.getComponentFamilies().size());
		assertNotNull(componentRegistry
				.getComponentFamily("TestComponentFamily"));
		assertEquals(componentFamily,
				componentRegistry.getComponentFamily("TestComponentFamily"));
		componentRegistry.removeComponentFamily(componentFamily);
		assertEquals(0, componentRegistry.getComponentFamilies().size());
		assertNull(componentRegistry.getComponentFamily("TestComponentFamily"));
	}

	@Test
	public void testGetResistryBase() throws Exception {
		assertEquals(componentRegistryUrl, componentRegistry.getRegistryBase());
	}

	@Test
	public void testGetComponentProfiles() throws Exception {
		assertNotNull(componentRegistry.getComponentProfiles());
	}

	@Test
	public void testAddComponentProfile() throws Exception {
		List<Profile> componentProfiles = componentRegistry
				.getComponentProfiles();
		boolean contained = false;
		for (Profile p : componentProfiles) {
			if (p.getId().equals(componentProfile.getId())) {
				contained = true;
			}
		}
		assertFalse(contained);
		int componentProfileCount = componentProfiles.size();
		componentRegistry.addComponentProfile(componentProfile, null, null);
		int newSize = componentRegistry.getComponentProfiles().size();
		assertEquals(componentProfileCount + 1, newSize);
	}

	@Test(expected = ComponentException.class)
	public void testAddComponentProfileNullProfile() throws Exception {
		componentRegistry.addComponentProfile(null, null, null);
	}

}
