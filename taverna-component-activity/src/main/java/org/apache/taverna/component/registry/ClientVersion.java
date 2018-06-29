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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ClientVersion {
	private static final String DEFAULT_VERSION = "1.1.0";
	public static final String VERSION;

	private ClientVersion() {
	}

	static {
		InputStream is = ClientVersion.class
				.getResourceAsStream("version.properties");
		String version = DEFAULT_VERSION;
		if (is != null)
			try {
				Properties p = new Properties();
				p.load(is);
				version = p.getProperty("project.version", DEFAULT_VERSION);
			} catch (IOException e) {
			} finally {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		VERSION = version;
	}

}
