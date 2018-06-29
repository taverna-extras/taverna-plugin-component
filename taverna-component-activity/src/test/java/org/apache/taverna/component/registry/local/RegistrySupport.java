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

import static java.lang.System.getProperty;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static io.github.taverna_extras.component.registry.Harness.componentRegistry;
import static io.github.taverna_extras.component.registry.Harness.componentRegistryUrl;

import java.io.File;

import io.github.taverna_extras.component.registry.local.LocalComponentRegistryFactory;

class RegistrySupport {
	private static File testRegistry;
	final static LocalComponentRegistryFactory factory = new LocalComponentRegistryFactory();

	public static void pre() throws Exception {
		testRegistry = new File(getProperty("java.io.tmpdir"), "TestRegistry");
		testRegistry.mkdir();
		componentRegistryUrl = testRegistry.toURI().toURL();
		componentRegistry = factory.getComponentRegistry(componentRegistryUrl);
	}

	public static void post() throws Exception {
		deleteDirectory(testRegistry);
	}
}
