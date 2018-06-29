package io.github.taverna_extras.component.registry.standard;
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

import io.github.taverna_extras.component.api.License;
import io.github.taverna_extras.component.registry.api.LicenseType;

class NewComponentLicense implements License {
	private NewComponentRegistry registry;
	private String id;
	private String title;
	private String description;
	private String abbreviation;

	static final String ELEMENTS = "title,description,unique-name";

	NewComponentLicense(NewComponentRegistry newComponentRegistry,
			LicenseType lt) {
		registry = newComponentRegistry;
		id = lt.getId();
		title = lt.getTitle();
		description = lt.getDescription();
		abbreviation = lt.getUniqueName();
	}

	String getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof NewComponentLicense))
			return false;
		NewComponentLicense other = (NewComponentLicense) o;
		return registry.equals(other.registry) && id.equals(other.id);
	}

	private static final int BASEHASH = NewComponentLicense.class.hashCode();

	@Override
	public int hashCode() {
		return BASEHASH ^ registry.hashCode() ^ id.hashCode();
	}

	@Override
	public String getName() {
		return title;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getAbbreviation() {
		return abbreviation;
	}

}
