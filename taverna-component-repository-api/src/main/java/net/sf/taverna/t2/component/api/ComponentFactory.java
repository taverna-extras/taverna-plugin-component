package net.sf.taverna.t2.component.api;

import java.net.URL;

import net.sf.taverna.t2.component.api.profile.Profile;

public interface ComponentFactory {
	public Registry getRegistry(URL registryBase) throws ComponentException;

	public Family getFamily(URL registryBase, String familyName)
			throws ComponentException;

	public Component getComponent(URL registryBase, String familyName,
			String componentName) throws ComponentException;

	public Version getVersion(URL registryBase, String familyName,
			String componentName, Integer componentVersion)
			throws ComponentException;

	public Version getVersion(Version.ID ident) throws ComponentException;

	public Component getComponent(Version.ID ident) throws ComponentException;

	public Profile getProfile(URL url) throws ComponentException;

	public Profile getBaseProfile() throws ComponentException;
}
