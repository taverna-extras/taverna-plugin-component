package org.apache.taverna.component.api;
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


import java.net.URL;
import java.util.List;
import java.util.Set;

import org.apache.taverna.component.api.profile.Profile;

public interface Registry {

	License getPreferredLicense() throws ComponentException;

	Set<Version.ID> searchForComponents(String prefixString, String text)
			throws ComponentException;

	/**
	 * Returns all the ComponentFamilies in this ComponetRegistry.
	 * <p>
	 * If this ComponentRegistry does not contain any ComponentFamilies an empty
	 * list is returned.
	 * 
	 * @return all the ComponentFamilies in this ComponetRegistry.
	 * @throws ComponentException
	 *             if there is a problem accessing the ComponentRegistry.
	 */
	List<Family> getComponentFamilies() throws ComponentException;

	List<License> getLicenses() throws ComponentException;

	List<SharingPolicy> getPermissions() throws ComponentException;

	/**
	 * Adds a ComponentProfile to this ComponentRegistry.
	 * 
	 * @param componentProfile
	 *            the ComponentProfile to add. Must not be null.
	 * @param sharingPolicy
	 * @param license
	 * @return the ComponentProfile added to this ComponentRegistry.
	 * @throws ComponentException
	 *             <ul>
	 *             <li>if componentProfile is null,
	 *             <li>if there is a problem accessing the ComponentRegistry.
	 *             </ul>
	 */
	Profile addComponentProfile(Profile componentProfile, License license,
			SharingPolicy sharingPolicy) throws ComponentException;

	/**
	 * Returns all the ComponentProfiles in this ComponetRegistry.
	 * <p>
	 * If this ComponentRegistry does not contain any ComponentProfiles an empty
	 * list is returned.
	 * 
	 * @return all the ComponentProfiles in this ComponetRegistry.
	 * @throws ComponentException
	 *             if there is a problem accessing the ComponentRegistry.
	 */
	List<Profile> getComponentProfiles() throws ComponentException;

	/**
	 * Returns the ComponentProfile with the given ID in this ComponetRegistry.
	 * <p>
	 * If this ComponentRegistry does not contain the ComponentProfile, a
	 * <tt>null</tt> is returned.
	 * 
	 * @return the matching ComponentProfile in this ComponetRegistry, or
	 *         <tt>null</tt> if there is no such thing.
	 * @throws ComponentException
	 *             if there is a problem accessing the ComponentRegistry.
	 */
	Profile getComponentProfile(String id) throws ComponentException;

	String getRegistryBaseString();

	/**
	 * Returns the location of this ComponentRepository.
	 * 
	 * @return the location of this ComponentRepository
	 */
	URL getRegistryBase();

	/**
	 * Removes a the ComponentFamily with the specified name from this
	 * ComponentRegistry.
	 * <p>
	 * If this ComponentRegistry does not contain a ComponentFamily with the
	 * specified name this method has no effect.
	 * 
	 * @param componentFamily
	 *            the ComponentFamily to remove.
	 * @throws ComponentException
	 *             if there is a problem accessing the ComponentRegistry.
	 */
	void removeComponentFamily(Family componentFamily) throws ComponentException;

	/**
	 * Creates a new ComponentFamily and adds it to this ComponentRegistry.
	 * 
	 * @param familyName
	 *            the name of the ComponentFamily to create. Must not be null.
	 * @param componentProfile
	 *            the ComponentProfile for the new ComponentFamily. Must not be
	 *            null.
	 * @param sharingPolicy
	 *            the SharingPolicy to use for the new ComponentFamily.
	 * @return the new ComponentFamily
	 * @throws ComponentException
	 *             <ul>
	 *             <li>if familyName is null,
	 *             <li>if componentProfile is null,
	 *             <li>if a ComponentFamily with this name already exists,
	 *             <li>if there is a problem accessing the ComponentRegistry.
	 *             </ul>
	 */
	Family createComponentFamily(String familyName, Profile componentProfile,
			String description, License license, SharingPolicy sharingPolicy)
			throws ComponentException;

	/**
	 * Returns the ComponentFamily with the specified name.
	 * <p>
	 * If this ComponentRegistry does not contain a ComponentFamily with the
	 * specified name <code>null</code> is returned.
	 * 
	 * @param familyName
	 *            the name of the ComponentFamily to return. Must not be null.
	 * @return the ComponentFamily with the specified name in this
	 *         ComponentRepository or null if none exists.
	 * @throws ComponentException
	 *             if there is a problem accessing the ComponentRegistry.
	 */
	Family getComponentFamily(String familyName) throws ComponentException;

	/**
	 * @return A description of the type of registry this is.
	 */
	String getRegistryTypeName();
}
