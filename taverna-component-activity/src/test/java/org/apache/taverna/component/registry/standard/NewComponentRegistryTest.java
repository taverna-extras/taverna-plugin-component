package org.apache.taverna.component.registry.standard;
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
import static org.junit.Assert.assertSame;

import java.net.URL;

import org.apache.taverna.component.api.Version;
import org.apache.taverna.component.registry.ComponentRegistryTest;
import org.apache.taverna.component.registry.standard.NewComponent;
import org.apache.taverna.component.registry.standard.NewComponentRegistry;
import org.apache.taverna.component.registry.standard.Policy;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;

/**
 * 
 * 
 * @author David Withers
 */
@Ignore("affects remote service")
public class NewComponentRegistryTest extends ComponentRegistryTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RegistrySupport.pre();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		RegistrySupport.post();
	}

	@Test
	public void testGetComponentRegistry() throws Exception {
		assertSame(componentRegistry,
				RegistrySupport.factory.getComponentRegistry(componentRegistryUrl));
	}

	@Test
	public void testUploadWorkflow() throws Exception {
		URL dataflowUrl = getClass().getClassLoader().getResource(
				"beanshell_test.t2flow");
		WorkflowBundle bundle = new WorkflowBundleIO().readBundle(dataflowUrl, null);

		NewComponentRegistry registry = (NewComponentRegistry) RegistrySupport.factory.getComponentRegistry(componentRegistryUrl);
		Version v = registry.createComponentFrom(null, "Test Workflow",
				"test description", bundle, null, Policy.PRIVATE);
		assertEquals("test description", v.getDescription());
		registry.deleteComponent((NewComponent) v.getComponent());
	}

}
