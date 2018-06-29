/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package io.github.taverna_extras.component.ui.preference;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.taverna.configuration.app.ApplicationConfiguration;

/**
 * Factored out defaults location system.
 * 
 * @author Donal Fellows
 */
public class ComponentDefaults {
    public static final String REGISTRY_LIST = "REGISTRY_NAMES";
    private static final String LOCAL_NAME = "local registry";
    private static final String MYEXPERIMENT_NAME = "myExperiment";
    private static final String MYEXPERIMENT_SITE = "http://www.myexperiment.org";
    public static final String DEFAULT_REGISTRY_LIST = LOCAL_NAME + "," + MYEXPERIMENT_NAME;

    public static Map<String, String> getDefaultProperties() {
    	// Capacity = 3; we know that this is going to have 3 entries
    	Map<String, String> defaults = new LinkedHashMap<>(3);
    	defaults.put(LOCAL_NAME, calculateComponentsDirectoryPath());
    	defaults.put(MYEXPERIMENT_NAME, MYEXPERIMENT_SITE);
    	defaults.put(REGISTRY_LIST, DEFAULT_REGISTRY_LIST);
    	return defaults;
    }

    static ApplicationConfiguration config;//FIXME beaninject (and beanify!)

	public static String calculateComponentsDirectoryPath() {
		return new File(config.getApplicationHomeDir(), "components").toURI()
				.toASCIIString();
	}

	private ComponentDefaults() {
	}
}
