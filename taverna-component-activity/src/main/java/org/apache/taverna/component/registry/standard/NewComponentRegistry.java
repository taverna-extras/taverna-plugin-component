package org.apache.taverna.component.registry.standard;

import static org.apache.log4j.Logger.getLogger;
import static org.apache.taverna.component.registry.standard.Policy.PRIVATE;
import static org.apache.taverna.component.utils.SystemUtils.getElementString;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.Component;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.Family;
import org.apache.taverna.component.api.License;
import org.apache.taverna.component.api.SharingPolicy;
import org.apache.taverna.component.api.Version;
import org.apache.taverna.component.api.Version.ID;
import org.apache.taverna.component.api.profile.Profile;
import org.apache.taverna.component.registry.ComponentRegistry;
import org.apache.taverna.component.registry.ComponentUtil;
import org.apache.taverna.component.registry.ComponentVersionIdentification;
import org.apache.taverna.component.registry.api.ComponentDescriptionList;
import org.apache.taverna.component.registry.api.ComponentFamilyList;
import org.apache.taverna.component.registry.api.ComponentFamilyType;
import org.apache.taverna.component.registry.api.ComponentProfileList;
import org.apache.taverna.component.registry.api.ComponentProfileType;
import org.apache.taverna.component.registry.api.ComponentType;
import org.apache.taverna.component.registry.api.Content;
import org.apache.taverna.component.registry.api.Description;
import org.apache.taverna.component.registry.api.LicenseList;
import org.apache.taverna.component.registry.api.LicenseType;
import org.apache.taverna.component.registry.api.ObjectFactory;
import org.apache.taverna.component.registry.api.Permissions;
import org.apache.taverna.component.registry.api.PolicyList;
import org.apache.taverna.component.utils.AnnotationUtils;
import org.apache.taverna.component.utils.SystemUtils;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.security.credentialmanager.CredentialManager;

class NewComponentRegistry extends ComponentRegistry {
	private static final String PROFILE_MIME_TYPE = "application/vnd.taverna.component-profile+xml";
	private static final String T2FLOW_MIME_TYPE = "application/vnd.taverna.t2flow+xml";
	static final Logger logger = getLogger(NewComponentRegistry.class);
	static final JAXBContext jaxbContext;
	static final Charset utf8;
	private static final ObjectFactory objectFactory = new ObjectFactory();

	// service URIs
	private static final String COMPONENT_SERVICE = "/component.xml";
	private static final String COMPONENT_FAMILY_SERVICE = "/component-family.xml";
	private static final String COMPONENT_PROFILE_SERVICE = "/component-profile.xml";
	private static final String COMPONENT_LIST = "/components.xml";
	private static final String COMPONENT_FAMILY_LIST = "/component-families.xml";
	private static final String COMPONENT_PROFILE_LIST = "/component-profiles.xml";
	private static final String WORKFLOW_SERVICE = "/workflow.xml";
	private static final String PACK_SERVICE = "/pack.xml";
	private static final String FILE_SERVICE = "/file.xml";
	private static final String LICENSE_LIST = "/licenses.xml";
	private static final String POLICY_LIST = "/policies.xml";

	static {
		JAXBContext c = null;
		Charset cs = null;
		try {
			c = JAXBContext.newInstance(ComponentDescriptionList.class,
					ComponentFamilyList.class, ComponentProfileList.class,
					ComponentType.class, ComponentFamilyType.class,
					ComponentProfileType.class, PolicyList.class,
					LicenseList.class);
			cs = Charset.forName("UTF-8");
		} catch (JAXBException e) {
			throw new Error("failed to build context", e);
		} catch (UnsupportedCharsetException e) {
			throw new Error("failed to find charset", e);
		} finally {
			jaxbContext = c;
			utf8 = cs;
		}
	}

	Client client;
	private final CredentialManager cm;
	private final ComponentUtil util;
	private final SystemUtils system;
	final AnnotationUtils annUtils;

	protected NewComponentRegistry(CredentialManager cm, URL registryBase,
			ComponentUtil util, SystemUtils system, AnnotationUtils annUtils) throws ComponentException {
		super(registryBase);
		this.cm = cm;
		this.util = util;
		this.system = system;
		this.annUtils = annUtils;
	}

	private void checkClientCreated() throws ComponentException {
		try {
			if (client == null)
				client = new Client(jaxbContext, super.getRegistryBase(), cm);
		} catch (Exception e) {
			throw new ComponentException("Unable to access registry", e);
		}
	}

	private List<Description> listComponentFamilies(String profileUri)
			throws ComponentException {
		checkClientCreated();
		return client.get(ComponentFamilyList.class, COMPONENT_FAMILY_LIST,
				"component-profile=" + profileUri,
				"elements=" + NewComponentFamily.ELEMENTS).getPack();
	}

	ComponentType getComponentById(String id, Integer version, String elements)
			throws ComponentException {
		checkClientCreated();

		if (version != null) {
			return client.get(ComponentType.class, WORKFLOW_SERVICE,
					"id=" + id, "version=" + version, "elements=" + elements);
		}
		return client.get(ComponentType.class, WORKFLOW_SERVICE, "id=" + id,
				"elements=" + elements);
	}

	@SuppressWarnings("unused")
	private ComponentFamilyType getComponentFamilyById(String id,
			String elements) throws ComponentException {
		checkClientCreated();

		return client.get(ComponentFamilyType.class, PACK_SERVICE, "id=" + id,
				"elements=" + elements);
	}

	private ComponentProfileType getComponentProfileById(String id,
			String elements) throws ComponentException {
		checkClientCreated();

		return client.get(ComponentProfileType.class, FILE_SERVICE, "id=" + id,
				"elements=" + elements);
	}

	@Override
	protected void populateFamilyCache() throws ComponentException {
		for (Profile pr : getComponentProfiles()) {
			NewComponentProfile p = (NewComponentProfile) pr;
			for (Description cfd : listComponentFamilies(p
					.getResourceLocation()))
				familyCache.put(getElementString(cfd, "title"),
						new NewComponentFamily(this, p, cfd, util));
		}
	}

	@Override
	protected Family internalCreateComponentFamily(String familyName,
			Profile componentProfile, String description, License license,
			SharingPolicy sharingPolicy) throws ComponentException {
		NewComponentProfile profile = (NewComponentProfile) componentProfile;

		checkClientCreated();

		return new NewComponentFamily(this, profile, client.post(
				ComponentFamilyType.class,
				objectFactory.createPack(makeComponentFamilyCreateRequest(
						profile, familyName, description, license,
						sharingPolicy)), COMPONENT_FAMILY_SERVICE, "elements="
						+ NewComponentFamily.ELEMENTS), util);
	}

	@Override
	protected void internalRemoveComponentFamily(Family componentFamily)
			throws ComponentException {
		NewComponentFamily ncf = (NewComponentFamily) componentFamily;
		checkClientCreated();

		client.delete(WORKFLOW_SERVICE, "id=" + ncf.getId());
	}

	@Override
	protected void populateProfileCache() throws ComponentException {
		checkClientCreated();

		for (Description cpd : client.get(ComponentProfileList.class,
				COMPONENT_PROFILE_LIST,
				"elements=" + NewComponentProfile.ELEMENTS).getFile())
			if (cpd.getUri() != null && !cpd.getUri().isEmpty())
				profileCache.add(new NewComponentProfile(this, cpd, util
						.getBaseProfileLocator()));
	}

	@Override
	protected Profile internalAddComponentProfile(Profile componentProfile,
			License license, SharingPolicy sharingPolicy)
			throws ComponentException {
		if (componentProfile == null)
			throw new ComponentException("component profile must not be null");
		try {
			if (componentProfile instanceof NewComponentProfile) {
				NewComponentProfile profile = (NewComponentProfile) componentProfile;
				if (profile.getComponentRegistry().equals(this))
					return new NewComponentProfile(this,
							getComponentProfileById(profile.getId(),
									NewComponentProfile.ELEMENTS),
							util.getBaseProfileLocator());
			}
		} catch (ComponentException e) {
			// Do nothing but fall through
		}
		checkClientCreated();

		return new NewComponentProfile(this, client.post(
				ComponentProfileType.class, objectFactory
						.createFile(makeComponentProfileCreateRequest(
								componentProfile.getName(),
								componentProfile.getDescription(),
								componentProfile.getXML(), license,
								sharingPolicy)), COMPONENT_PROFILE_SERVICE,
				"elements=" + NewComponentProfile.ELEMENTS),
				util.getBaseProfileLocator());
	}

	public Permissions getPermissions(SharingPolicy userSharingPolicy) {
		if (userSharingPolicy == null)
			userSharingPolicy = getDefaultSharingPolicy();
		return ((Policy) userSharingPolicy).getPermissionsElement();
	}

	private ComponentProfileType makeComponentProfileCreateRequest(
			String title, String description, String content, License license,
			SharingPolicy sharingPolicy) throws ComponentException {
		ComponentProfileType profile = new ComponentProfileType();

		profile.setFilename(title + ".xml");
		profile.setTitle(title);
		profile.setTitle(description);
		profile.setContentType(PROFILE_MIME_TYPE);
		profile.setContent(new Content());
		profile.getContent().setEncoding("base64");
		profile.getContent().setType("binary");
		profile.getContent().setValue(content.getBytes(utf8));
		if (license == null)
			license = getPreferredLicense();
		profile.setLicenseType(new Description());
		profile.getLicenseType().getContent().add(license.getAbbreviation());
		profile.setPermissions(getPermissions(sharingPolicy));

		return profile;
	}

	private ComponentFamilyType makeComponentFamilyCreateRequest(
			NewComponentProfile profile, String familyName, String description,
			License license, SharingPolicy sharingPolicy)
			throws ComponentException {
		ComponentFamilyType familyDoc = new ComponentFamilyType();

		familyDoc.setComponentProfile(profile.getResourceLocation());
		familyDoc.setDescription(description);
		familyDoc.setTitle(familyName);
		if (license == null)
			license = getPreferredLicense();
		familyDoc.setLicenseType(new Description());
		familyDoc.getLicenseType().getContent().add(license.getAbbreviation());
		familyDoc.setPermissions(getPermissions(sharingPolicy));

		return familyDoc;
	}

	private ComponentType makeComponentVersionCreateRequest(String title,
			String description, WorkflowBundle content, NewComponentFamily family,
			License license, SharingPolicy sharingPolicy)
			throws ComponentException {
		ComponentType comp = new ComponentType();

		comp.setTitle(title);
		comp.setDescription(description);
		if (family != null)
			comp.setComponentFamily(family.getResourceLocation());
		comp.setContentType(T2FLOW_MIME_TYPE);
		comp.setContent(new Content());
		comp.getContent().setEncoding("base64");
		comp.getContent().setType("binary");
		comp.getContent().setValue(system.serializeBundle(content));
		if (license == null)
			license = getPreferredLicense();
		if (license != null) {
			comp.setLicenseType(new Description());
			comp.getLicenseType().getContent().add(license.getAbbreviation());
		}
		comp.setPermissions(getPermissions(sharingPolicy));

		return comp;
	}

	private static final boolean DO_LIST_POLICIES = false;

	private List<Description> listPolicies() throws ComponentException {
		checkClientCreated();
		return client.get(PolicyList.class, POLICY_LIST, "type=group")
				.getPolicy();
	}

	@Override
	protected void populatePermissionCache() {
		permissionCache.add(Policy.PUBLIC);
		permissionCache.add(Policy.PRIVATE);
		try {
			if (DO_LIST_POLICIES)
				for (Description d : listPolicies())
					permissionCache.add(new Policy.Group(d.getId()));
		} catch (ComponentException e) {
			logger.warn("failed to fetch sharing policies", e);
		}
	}

	private List<LicenseType> listLicenses() throws ComponentException {
		checkClientCreated();

		return client.get(LicenseList.class, LICENSE_LIST,
				"elements=" + NewComponentLicense.ELEMENTS).getLicense();
	}

	@Override
	protected void populateLicenseCache() {
		try {
			for (LicenseType lt : listLicenses())
				licenseCache.add(new NewComponentLicense(this, lt));
		} catch (ComponentException e) {
			logger.warn("failed to fetch licenses", e);
		}
	}

	@Override
	public License getPreferredLicense() throws ComponentException {
		return getLicenseByAbbreviation(getNameOfPreferredLicense());
	}

	public String getNameOfPreferredLicense() {
		return "by-nd";
	}

	public SharingPolicy getDefaultSharingPolicy() {
		return PRIVATE;
	}

	private List<Description> listComponents(String query, String prefixes)
			throws ComponentException {
		checkClientCreated();

		return client.get(ComponentDescriptionList.class, COMPONENT_LIST,
				"query=" + query, "prefixes=" + prefixes,
				"elements=" + NewComponent.ELEMENTS).getWorkflow();
	}

	@Override
	public Set<ID> searchForComponents(String prefixes, String text)
			throws ComponentException {
		HashSet<ID> versions = new HashSet<>();
		for (Description cd : listComponents(text, prefixes)) {
			NewComponent nc = null;
			for (Family f : getComponentFamilies()) {
				nc = (NewComponent) ((NewComponentFamily) f)
						.getComponent(getElementString(cd, "title"));
				if (nc != null)
					break;
			}
			if (nc != null)
				versions.add(new ComponentVersionIdentification(
						getRegistryBase(), nc.getFamily().getName(), nc
								.getName(), cd.getVersion()));
			else
				logger.warn("could not construct component for " + cd.getUri());
		}
		return versions;
	}

	private List<Description> listComponents(String familyUri)
			throws ComponentException {
		checkClientCreated();

		return client.get(ComponentDescriptionList.class, COMPONENT_LIST,
				"component-family=" + familyUri,
				"elements=" + NewComponent.ELEMENTS).getWorkflow();
	}

	protected List<Component> listComponents(NewComponentFamily family)
			throws ComponentException {
		List<Component> result = new ArrayList<>();
		for (Description cd : listComponents(family.getResourceLocation()))
			result.add(new NewComponent(this, family, cd, system));
		return result;
	}

	protected void deleteComponent(NewComponent component)
			throws ComponentException {
		checkClientCreated();

		client.delete(WORKFLOW_SERVICE, "id=" + component.getId());
	}

	protected Version createComponentFrom(NewComponentFamily family,
			String componentName, String description,
			WorkflowBundle implementation, License license,
			SharingPolicy sharingPolicy) throws ComponentException {
		checkClientCreated();

		ComponentType ct = client.post(ComponentType.class, objectFactory
				.createWorkflow(makeComponentVersionCreateRequest(
						componentName, description, implementation, family,
						license, sharingPolicy)), COMPONENT_SERVICE,
				"elements=" + NewComponent.ELEMENTS);
		NewComponent nc = new NewComponent(this, family, ct, system);
		return nc.new Version(ct.getVersion(), description, implementation);
	}

	protected Version createComponentVersionFrom(NewComponent component,
			String componentName, String description,
			WorkflowBundle implementation, License license,
			SharingPolicy sharingPolicy) throws ComponentException {
		checkClientCreated();

		ComponentType ct = client.post(ComponentType.class, objectFactory
				.createWorkflow(makeComponentVersionCreateRequest(
						componentName, description, implementation,
						component.family, license, sharingPolicy)),
				COMPONENT_SERVICE, "id=" + component.getId(), "elements="
						+ NewComponent.ELEMENTS);
		return component.new Version(ct.getVersion(), description,
				implementation);
	}

	public License getLicense(String name) throws ComponentException {
		for (License l : getLicenses())
			if (l.getAbbreviation().equals(name))
				return l;
		return null;
	}

	@Override
	public boolean equals(Object o) {
		// Careful! Java's URL equality IS BROKEN!
		if (o != null && o instanceof NewComponentRegistry) {
			NewComponentRegistry other = (NewComponentRegistry) o;
			return getRegistryBaseString()
					.equals(other.getRegistryBaseString());
		}
		return false;
	}

	private static final int BASEHASH = NewComponentRegistry.class.hashCode();

	@Override
	public int hashCode() {
		return BASEHASH ^ getRegistryBaseString().hashCode();
	}

	@Override
	public String getRegistryTypeName() {
		return "Component API";
	}
}
