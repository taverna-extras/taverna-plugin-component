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

import static io.github.taverna_extras.component.utils.SystemUtils.getElementString;

import java.net.MalformedURLException;
import java.net.URL;

import io.github.taverna_extras.component.api.ComponentException;
import io.github.taverna_extras.component.profile.BaseProfileLocator;
import io.github.taverna_extras.component.profile.ComponentProfileImpl;
import io.github.taverna_extras.component.registry.api.ComponentProfileType;
import io.github.taverna_extras.component.registry.api.Description;

/**
 * Profiles managed by the new-interface component registry.
 * 
 * @author Donal Fellows
 */
class NewComponentProfile extends ComponentProfileImpl {
	private static final String LOCATION = "content-uri";
	static final String ELEMENTS = LOCATION;

	private final NewComponentRegistry registry;
	private String id;
	private String location;
	private String resource;
	private final String uri;

	private static URL contentUrl(ComponentProfileType cpt)
			throws ComponentException {
		try {
			return new URL(cpt.getContentUri());
		} catch (MalformedURLException e) {
			throw new ComponentException("bad profile location", e);
		}
	}

	private static URL getLocationURL(Description cpd) throws ComponentException {
		try {
			return new URL(getElementString(cpd, LOCATION));
		} catch (MalformedURLException e) {
			throw new ComponentException("bad profile location", e);
		}
	}

	NewComponentProfile(NewComponentRegistry registry,
			ComponentProfileType profile, BaseProfileLocator base)
			throws ComponentException {
		super(registry, contentUrl(profile), base);
		this.registry = registry;
		uri = profile.getUri();
		id = profile.getId();
		location = profile.getContentUri();
		resource = profile.getResource();
	}

	NewComponentProfile(NewComponentRegistry registry, Description cpd,
			BaseProfileLocator base) throws ComponentException {
		super(registry, getLocationURL(cpd), base);
		this.registry = registry;
		uri = cpd.getUri();
		id = cpd.getId();
		location = getElementString(cpd, LOCATION);
		resource = cpd.getResource();
	}

	public String getLocation() {
		return location;
	}

	public String getID() {
		return id;
	}

	public String getUri() {
		return uri;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof NewComponentProfile) {
			NewComponentProfile other = (NewComponentProfile) o;
			return registry.equals(other.registry) && id.equals(other.id);
		}
		return false;
	}

	private static final int BASEHASH = NewComponentProfile.class.hashCode();

	@Override
	public int hashCode() {
		return BASEHASH ^ registry.hashCode() ^ id.hashCode();
	}

	@Override
	public String toString() {
		return "Remote Component Profile[" + location + "]";
	}

	public String getResourceLocation() {
		return resource;
	}
}
