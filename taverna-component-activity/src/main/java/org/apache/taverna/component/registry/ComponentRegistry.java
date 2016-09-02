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
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.Family;
import org.apache.taverna.component.api.License;
import org.apache.taverna.component.api.SharingPolicy;
import org.apache.taverna.component.api.Version;
import org.apache.taverna.component.api.profile.Profile;

/**
 * A ComponentRegistry contains ComponentFamilies and ComponentProfiles.
 * 
 * @author David Withers
 */
public abstract class ComponentRegistry implements
		org.apache.taverna.component.api.Registry {
	protected Map<String, Family> familyCache = new HashMap<>();
	protected List<Profile> profileCache = new ArrayList<>();
	protected List<SharingPolicy> permissionCache = new ArrayList<>();
	protected List<License> licenseCache = new ArrayList<>();

	private URL registryBase;

	protected ComponentRegistry(URL registryBase) throws ComponentException {
		this.registryBase = registryBase;
	}

	protected ComponentRegistry(File fileDir) throws ComponentException {
		try {
			this.registryBase = fileDir.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new ComponentException(e);
		}
	}

	@Override
	public final List<Family> getComponentFamilies() throws ComponentException {
		checkFamilyCache();
		return new ArrayList<Family>(familyCache.values());
	}

	private void checkFamilyCache() throws ComponentException {
		synchronized (familyCache) {
			if (familyCache.isEmpty())
				populateFamilyCache();
		}
	}

	protected abstract void populateFamilyCache() throws ComponentException;

	@Override
	public final Family getComponentFamily(String familyName)
			throws ComponentException {
		checkFamilyCache();
		return familyCache.get(familyName);
	}

	@Override
	public final Family createComponentFamily(String familyName,
			Profile componentProfile, String description, License license,
			SharingPolicy sharingPolicy) throws ComponentException {
		if (familyName == null)
			throw new ComponentException(
					"Component family name must not be null");
		if (componentProfile == null)
			throw new ComponentException("Component profile must not be null");
		if (getComponentFamily(familyName) != null)
			throw new ComponentException("Component family already exists");

		Family result = internalCreateComponentFamily(familyName,
				componentProfile, description, license, sharingPolicy);
		checkFamilyCache();
		synchronized (familyCache) {
			familyCache.put(familyName, result);
		}
		return result;
	}

	protected abstract Family internalCreateComponentFamily(String familyName,
			Profile componentProfile, String description, License license,
			SharingPolicy sharingPolicy) throws ComponentException;

	@Override
	public final void removeComponentFamily(Family componentFamily)
			throws ComponentException {
		if (componentFamily != null) {
			checkFamilyCache();
			synchronized (familyCache) {
				familyCache.remove(componentFamily.getName());
			}
		  internalRemoveComponentFamily(componentFamily);
		}
	}

	protected abstract void internalRemoveComponentFamily(Family componentFamily)
			throws ComponentException;

	@Override
	public final URL getRegistryBase() {
		return registryBase;
	}

	@Override
	public final String getRegistryBaseString() {
		String urlString = getRegistryBase().toString();
		if (urlString.endsWith("/"))
			urlString = urlString.substring(0, urlString.length() - 1);
		return urlString;
	}

	private void checkProfileCache() throws ComponentException {
		synchronized (profileCache) {
			if (profileCache.isEmpty())
				populateProfileCache();
		}
	}

	protected abstract void populateProfileCache() throws ComponentException;

	@Override
	public final List<Profile> getComponentProfiles() throws ComponentException {
		checkProfileCache();
		return profileCache;
	}

	@Override
	public final Profile getComponentProfile(String id)
			throws ComponentException {
		// TODO use a map instead of a *linear search*...
		for (Profile p : getComponentProfiles())
			if (p.getId().equals(id))
				return p;
		return null;
	}

	@Override
	public final Profile addComponentProfile(Profile componentProfile,
			License license, SharingPolicy sharingPolicy)
			throws ComponentException {
		if (componentProfile == null) {
			throw new ComponentException("componentProfile is null");
		}
		Profile result = null;
		checkProfileCache();
		for (Profile p : getComponentProfiles())
			if (p.getId().equals(componentProfile.getId())) {
				result = p;
				break;
			}

		if (result == null) {
			result = internalAddComponentProfile(componentProfile, license,
					sharingPolicy);
			synchronized (profileCache) {
				profileCache.add(result);
			}
		}
		return result;
	}

	protected abstract Profile internalAddComponentProfile(
			Profile componentProfile, License license,
			SharingPolicy sharingPolicy) throws ComponentException;

	private void checkPermissionCache() {
		synchronized (permissionCache) {
			if (permissionCache.isEmpty())
				populatePermissionCache();
		}
	}

	protected abstract void populatePermissionCache();

	@Override
	public final List<SharingPolicy> getPermissions() throws ComponentException {
		checkPermissionCache();
		return permissionCache;
	}

	private void checkLicenseCache() {
		synchronized (licenseCache) {
			if (licenseCache.isEmpty())
				populateLicenseCache();
		}
	}

	protected abstract void populateLicenseCache();

	@Override
	public final List<License> getLicenses() throws ComponentException {
		checkLicenseCache();
		return licenseCache;
	}

	protected License getLicenseByAbbreviation(String licenseString)
			throws ComponentException {
		checkLicenseCache();
		for (License l : getLicenses())
			if (l.getAbbreviation().equals(licenseString))
				return l;
		return null;
	}

	@Override
	public abstract License getPreferredLicense() throws ComponentException;

	@Override
	public abstract Set<Version.ID> searchForComponents(String prefixString,
			String text) throws ComponentException;

	@Override
	public String toString() {
		String[] names = getClass().getName().split("\\.");
		return names[names.length-1] + ": " + registryBase;
	}
}
