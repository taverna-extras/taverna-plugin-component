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
package net.sf.taverna.t2.component.registry;

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

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;

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
