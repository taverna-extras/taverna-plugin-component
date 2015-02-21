/**
 *
 */
package org.apache.taverna.component.registry.local;

import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.apache.log4j.Logger.getLogger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.Family;
import org.apache.taverna.component.api.License;
import org.apache.taverna.component.api.SharingPolicy;
import org.apache.taverna.component.api.Version;
import org.apache.taverna.component.api.profile.Profile;
import org.apache.taverna.component.profile.ComponentProfileImpl;
import org.apache.taverna.component.registry.ComponentRegistry;
import org.apache.taverna.component.registry.ComponentUtil;
import org.apache.taverna.component.utils.SystemUtils;

/**
 * A component registry implemented using the local file system. Note that the
 * components it contains are <i>not</i> shareable.
 * 
 * @author alanrw
 */
class LocalComponentRegistry extends ComponentRegistry {
	private static final Logger logger = getLogger(LocalComponentRegistry.class);
	static final String ENC = "utf-8";
	private ComponentUtil util;
	private SystemUtils system;
	private File baseDir;

	public LocalComponentRegistry(File registryDir, ComponentUtil util,
			SystemUtils system) throws ComponentException {
		super(registryDir);
		baseDir = registryDir;
		this.util = util;
		this.system = system;
	}

	@Override
	public Family internalCreateComponentFamily(String name,
			Profile componentProfile, String description, License license,
			SharingPolicy sharingPolicy) throws ComponentException {
		File newFamilyDir = new File(getComponentFamiliesDir(), name);
		newFamilyDir.mkdirs();
		File profileFile = new File(newFamilyDir, "profile");
		try {
			writeStringToFile(profileFile, componentProfile.getName(), ENC);
		} catch (IOException e) {
			throw new ComponentException("Could not write out profile", e);
		}
		File descriptionFile = new File(newFamilyDir, "description");
		try {
			writeStringToFile(descriptionFile, description, ENC);
		} catch (IOException e) {
			throw new ComponentException("Could not write out description", e);
		}
		return new LocalComponentFamily(this, newFamilyDir, util, system);
	}

	@Override
	protected void populateFamilyCache() throws ComponentException {
		File familiesDir = getComponentFamiliesDir();
		for (File subFile : familiesDir.listFiles()) {
			if (!subFile.isDirectory())
				continue;
			LocalComponentFamily newFamily = new LocalComponentFamily(this,
					subFile, util, system);
			familyCache.put(newFamily.getName(), newFamily);
		}
	}

	@Override
	protected void populateProfileCache() throws ComponentException {
		File profilesDir = getComponentProfilesDir();
		for (File subFile : profilesDir.listFiles())
			if (subFile.isFile() && (!subFile.isHidden())
					&& subFile.getName().endsWith(".xml"))
				try {
					profileCache.add(new LocalComponentProfile(subFile));
				} catch (MalformedURLException e) {
					logger.error("Unable to read profile", e);
				}
	}

	@Override
	protected void internalRemoveComponentFamily(Family componentFamily)
			throws ComponentException {
		try {
			deleteDirectory(new File(getComponentFamiliesDir(),
					componentFamily.getName()));
		} catch (IOException e) {
			throw new ComponentException("Unable to delete component family", e);
		}
	}

	private File getBaseDir() {
		baseDir.mkdirs();
		return baseDir;
	}

	private File getComponentFamiliesDir() {
		File componentFamiliesDir = new File(getBaseDir(), "componentFamilies");
		componentFamiliesDir.mkdirs();
		return componentFamiliesDir;
	}

	private File getComponentProfilesDir() {
		File componentProfilesDir = new File(getBaseDir(), "componentProfiles");
		componentProfilesDir.mkdirs();
		return componentProfilesDir;
	}

	@Override
	public Profile internalAddComponentProfile(Profile componentProfile,
			License license, SharingPolicy sharingPolicy)
			throws ComponentException {
		String name = componentProfile.getName().replaceAll("\\W+", "")
				+ ".xml";
		String inputString = componentProfile.getXML();
		File outputFile = new File(getComponentProfilesDir(), name);
		try {
			writeStringToFile(outputFile, inputString);
		} catch (IOException e) {
			throw new ComponentException("Unable to save profile", e);
		}

		try {
			return new LocalComponentProfile(outputFile);
		} catch (MalformedURLException e) {
			throw new ComponentException("Unable to create profile", e);
		}
	}

	@Override
	public int hashCode() {
		return 31 + ((baseDir == null) ? 0 : baseDir.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocalComponentRegistry other = (LocalComponentRegistry) obj;
		if (baseDir == null)
			return (other.baseDir == null);
		return baseDir.equals(other.baseDir);
	}

	@Override
	public void populatePermissionCache() {
		return;
	}

	@Override
	public void populateLicenseCache() {
		return;
	}

	@Override
	public License getPreferredLicense() {
		return null;
	}

	@Override
	public Set<Version.ID> searchForComponents(String prefixString, String text)
			throws ComponentException {
		throw new ComponentException("Local registries cannot be searched yet");
	}

	@Override
	public String getRegistryTypeName() {
		return "File System";
	}

	class LocalComponentProfile extends ComponentProfileImpl {
		URI uri;

		LocalComponentProfile(File file) throws MalformedURLException,
				ComponentException {
			super(LocalComponentRegistry.this, file.toURI(), util
					.getBaseProfileLocator());
			uri = file.toURI();
		}

		@Override
		public String toString() {
			return "Local Component Profile[" + uri + "]";
		}
	}
}
