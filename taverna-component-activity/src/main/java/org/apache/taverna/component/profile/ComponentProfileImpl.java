/*******************************************************************************
 * Copyright (C) 2012 The University of Manchester
 *
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package org.apache.taverna.component.profile;

import static com.hp.hpl.jena.rdf.model.ModelFactory.createOntologyModel;
import static java.lang.System.identityHashCode;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static net.sf.taverna.t2.workflowmodel.health.HealthCheck.NO_PROBLEM;
import static net.sf.taverna.t2.workflowmodel.health.RemoteHealthChecker.contactEndpoint;
import static org.apache.log4j.Logger.getLogger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.Registry;
import org.apache.taverna.component.api.profile.ActivityProfile;
import org.apache.taverna.component.api.profile.ExceptionHandling;
import org.apache.taverna.component.api.profile.PortProfile;
import org.apache.taverna.component.api.profile.SemanticAnnotationProfile;

import org.apache.taverna.component.api.profile.doc.Activity;
import org.apache.taverna.component.api.profile.doc.Ontology;
import org.apache.taverna.component.api.profile.doc.Port;
import org.apache.taverna.component.api.profile.doc.Profile;
import org.apache.taverna.component.api.profile.doc.SemanticAnnotation;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;

/**
 * A ComponentProfile specifies the inputs, outputs and semantic annotations
 * that a Component must contain.
 * 
 * @author David Withers
 */
public class ComponentProfileImpl implements
		org.apache.taverna.component.api.profile.Profile {
	private static final Logger logger = getLogger(ComponentProfileImpl.class);
	private static final Map<String, OntModel> ontologyModels = new HashMap<>();
	private static final JAXBContext jaxbContext;
	private BaseProfileLocator base;
	static {
		try {
			jaxbContext = JAXBContext.newInstance(Profile.class);
		} catch (JAXBException e) {
			// Should never happen! Represents a critical error
			throw new Error(
					"Failed to initialize profile deserialization engine", e);
		}
	}
	private org.apache.taverna.component.api.profile.Profile parent;
	private Profile profileDoc;
	private ExceptionHandling exceptionHandling;
	private Registry parentRegistry = null;
	private final Object lock = new Object();
	private Exception loaderException = null;
	protected volatile boolean loaded = false;

	public ComponentProfileImpl(URL profileURL, BaseProfileLocator base)
			throws ComponentException {
		this(null, profileURL, base);
	}

	public ComponentProfileImpl(String profileString, BaseProfileLocator base)
			throws ComponentException {
		this(null, profileString, base);
	}

	public ComponentProfileImpl(Registry registry, URI profileURI,
			BaseProfileLocator base) throws ComponentException,
			MalformedURLException {
		this(registry, profileURI.toURL(), base);
	}

	public ComponentProfileImpl(Registry registry, URL profileURL,
			BaseProfileLocator base) throws ComponentException {
		logger.info("Loading profile in " + identityHashCode(this) + " from "
				+ profileURL);
		this.base = base;
		try {
			URL url = profileURL;
			if (url.getProtocol().startsWith("http"))
				url = new URI(url.getProtocol(), url.getAuthority(),
						url.getPath(), url.getQuery(), url.getRef()).toURL();
			loadProfile(this, url, base);
		} catch (MalformedURLException e) {
			logger.warn("Malformed URL? " + profileURL);
		} catch (URISyntaxException e) {
			logger.warn("Malformed URL? " + profileURL);
		}
		parentRegistry = registry;
	}

	public ComponentProfileImpl(Registry registry, String profileString,
			BaseProfileLocator base) throws ComponentException {
		logger.info("Loading profile in " + identityHashCode(this)
				+ " from string");
		this.base = base;
		loadProfile(this, profileString, base);
		this.parentRegistry = registry;
	}

	private static void loadProfile(final ComponentProfileImpl profile,
			final Object source, BaseProfileLocator base) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				Date start = new Date();
				if (source instanceof URL)
					loadProfileFromURL(profile, (URL) source);
				else if (source instanceof String)
					loadProfileFromString(profile, (String) source);
				else
					throw new IllegalArgumentException(
							"Bad type of profile source: " + source.getClass());
				Date end = new Date();
				logger.info("Loaded profile in " + identityHashCode(profile)
						+ " (in " + (end.getTime() - start.getTime())
						+ " msec)");
			}
		};
		if (base.getProfile() == null)
			// Must load the base profile synchronously, to avoid deadlock
			r.run();
		else
			new Thread(r).start();
	}

	private static void loadProfileFromURL(ComponentProfileImpl profile, URL source) {
		try {
			URLConnection conn = source.openConnection();
			try {
				conn.addRequestProperty("Accept", "application/xml,*/*;q=0.1");
			} catch (Exception e) {
			}
			try (InputStream is = conn.getInputStream()) {
				profile.profileDoc = jaxbContext.createUnmarshaller()
						.unmarshal(new StreamSource(is), Profile.class)
						.getValue();
			}
		} catch (FileNotFoundException e) {
			profile.loaderException = e;
			logger.warn("URL not readable: " + source);
		} catch (Exception e) {
			profile.loaderException = e;
			logger.warn("Failed to load profile.", e);
		}
		synchronized (profile.lock) {
			profile.loaded = true;
			profile.lock.notifyAll();
		}
	}

	private static void loadProfileFromString(ComponentProfileImpl profile,
			String source) {
		try {
			profile.profileDoc = jaxbContext
					.createUnmarshaller()
					.unmarshal(new StreamSource(new StringReader(source)),
							Profile.class).getValue();
		} catch (Exception e) {
			profile.loaderException = e;
			logger.warn("Failed to load profile.", e);
		}
		synchronized (profile.lock) {
			profile.loaded = true;
			profile.lock.notifyAll();
		}
	}

	@Override
	public Registry getComponentRegistry() {
		return parentRegistry;
	}

	@Override
	public String getXML() throws ComponentException {
		try {
			StringWriter stringWriter = new StringWriter();
			jaxbContext.createMarshaller().marshal(getProfileDocument(),
					stringWriter);
			return stringWriter.toString();
		} catch (JAXBException e) {
			throw new ComponentException("Unable to serialize profile.", e);
		}
	}

	@Override
	public Profile getProfileDocument() throws ComponentException {
		try {
			synchronized (lock) {
				while (!loaded)
					lock.wait();
				if (loaderException != null) {
					if (loaderException instanceof FileNotFoundException)
						throw new ComponentException(
								"Profile not found/readable: "
										+ loaderException.getMessage(),
								loaderException);
					throw new ComponentException(
							"Problem loading profile definition: "
									+ loaderException.getMessage(),
							loaderException);
				}
				return profileDoc;
			}
		} catch (InterruptedException e) {
			logger.info("Interrupted during wait for lock.", e);
			return null;
		}
	}

	@Override
	public String getId() {
		try {
			return getProfileDocument().getId();
		} catch (ComponentException e) {
			return null;
		}
	}

	@Override
	public String getName() {
		try {
			return getProfileDocument().getName();
		} catch (ComponentException e) {
			return null;
		}
	}

	@Override
	public String getDescription() {
		try {
			return getProfileDocument().getDescription();
		} catch (ComponentException e) {
			return null;
		}
	}

	/**
	 * @return Is this the base profile?
	 */
	private boolean isBase() {
		if (base == null)
			return true;
		Object o = base.getProfile();
		return o == null || o == this;
	}

	private synchronized org.apache.taverna.component.api.profile.Profile parent()
			throws ComponentException {
		if (parent == null) {
			try {
				if (!isBase() && getProfileDocument().getExtends() != null
						&& parentRegistry != null) {
					parent = parentRegistry
							.getComponentProfile(getProfileDocument()
									.getExtends().getProfileId());
					if (parent != null)
						return parent;
				}
			} catch (ComponentException e) {
			}
			parent = new EmptyProfile();
		}
		return parent;
	}

	@Override
	public String getOntologyLocation(String ontologyId) {
		String ontologyURI = null;
		try {
			for (Ontology ontology : getProfileDocument().getOntology())
				if (ontology.getId().equals(ontologyId))
					ontologyURI = ontology.getValue();
		} catch (ComponentException e) {
		}
		if ((ontologyURI == null) && !isBase())
			ontologyURI = base.getProfile().getOntologyLocation(ontologyId);
		return ontologyURI;
	}

	private Map<String, String> internalGetPrefixMap()
			throws ComponentException {
		Map<String, String> result = new TreeMap<>();
		try {
			for (Ontology ontology : getProfileDocument().getOntology())
				result.put(ontology.getId(), ontology.getValue());
		} catch (ComponentException e) {
		}
		result.putAll(parent().getPrefixMap());
		return result;
	}

	@Override
	public Map<String, String> getPrefixMap() throws ComponentException {
		Map<String, String> result = internalGetPrefixMap();
		if (!isBase())
			result.putAll(base.getProfile().getPrefixMap());
		return result;
	}

	private OntModel readOntologyFromURI(String ontologyId, String ontologyURI) {
		logger.info("Reading ontology for " + ontologyId + " from "
				+ ontologyURI);
		OntModel model = createOntologyModel();
		try {
			URL url = new URL(ontologyURI);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// CRITICAL: must be retrieved as correct content type
			conn.addRequestProperty("Accept",
					"application/rdf+xml,application/xml;q=0.9");
			try (InputStream in = conn.getInputStream()) {
				// TODO Consider whether the encoding is handled right
				// ontologyModel.read(in, url.toString());
				model.read(new StringReader(IOUtils.toString(in, "UTF-8")),
						url.toString());
			}
		} catch (MalformedURLException e) {
			logger.error("Problem reading ontology " + ontologyId, e);
			return null;
		} catch (IOException e) {
			logger.error("Problem reading ontology " + ontologyId, e);
			return null;
		} catch (NullPointerException e) {
			// TODO Why is this different?
			logger.error("Problem reading ontology " + ontologyId, e);
			model = createOntologyModel();
		}
		return model;
	}

	private boolean isAccessible(String ontologyURI) {
		return contactEndpoint(null, ontologyURI).getResultId() == NO_PROBLEM;
	}

	@Override
	public OntModel getOntology(String ontologyId) {
		String ontologyURI = getOntologyLocation(ontologyId);
		synchronized (ontologyModels) {
			if (ontologyModels.containsKey(ontologyURI))
				return ontologyModels.get(ontologyURI);
		}

		// Drop out of critical section while we do I/O
		if (!isAccessible(ontologyURI)) {
			logger.warn("Catastrophic problem contacting ontology source.");
			// Catastrophic problem?!
			synchronized (ontologyModels) {
				ontologyModels.put(ontologyURI, null);
			}
			return null;
		}
		OntModel model = readOntologyFromURI(ontologyId, ontologyURI);

		synchronized (ontologyModels) {
			if (model != null && !ontologyModels.containsKey(ontologyURI)) {
				ontologyModels.put(ontologyURI, model);
			}
			return ontologyModels.get(ontologyURI);
		}
	}

	@Override
	public List<PortProfile> getInputPortProfiles() {
		List<PortProfile> portProfiles = new ArrayList<>();
		try {
			for (Port port : getProfileDocument().getComponent().getInputPort())
				portProfiles.add(new PortProfileImpl(this, port));
		} catch (ComponentException e) {
		}
		if (!isBase())
			portProfiles.addAll(base.getProfile().getInputPortProfiles());
		return portProfiles;
	}

	@Override
	public List<SemanticAnnotationProfile> getInputSemanticAnnotationProfiles()
			throws ComponentException {
		List<SemanticAnnotationProfile> saProfiles = new ArrayList<>();
		List<PortProfile> portProfiles = getInputPortProfiles();
		portProfiles.addAll(parent().getInputPortProfiles());
		for (PortProfile portProfile : portProfiles)
			saProfiles.addAll(portProfile.getSemanticAnnotations());
		if (!isBase())
			saProfiles.addAll(base.getProfile()
					.getInputSemanticAnnotationProfiles());
		return getUniqueSemanticAnnotationProfiles(saProfiles);
	}

	@Override
	public List<PortProfile> getOutputPortProfiles() {
		List<PortProfile> portProfiles = new ArrayList<>();
		try {
			for (Port port : getProfileDocument().getComponent()
					.getOutputPort())
				portProfiles.add(new PortProfileImpl(this, port));
		} catch (ComponentException e) {
		}
		if (!isBase())
			portProfiles.addAll(base.getProfile().getOutputPortProfiles());
		return portProfiles;
	}

	@Override
	public List<SemanticAnnotationProfile> getOutputSemanticAnnotationProfiles()
			throws ComponentException {
		List<SemanticAnnotationProfile> saProfiles = new ArrayList<>();
		List<PortProfile> portProfiles = getOutputPortProfiles();
		portProfiles.addAll(parent().getOutputPortProfiles());
		for (PortProfile portProfile : portProfiles)
			saProfiles.addAll(portProfile.getSemanticAnnotations());
		if (!isBase())
			saProfiles.addAll(base.getProfile()
					.getOutputSemanticAnnotationProfiles());
		return getUniqueSemanticAnnotationProfiles(saProfiles);
	}

	@Override
	public List<org.apache.taverna.component.api.profile.ActivityProfile> getActivityProfiles() {
		List<org.apache.taverna.component.api.profile.ActivityProfile> activityProfiles = new ArrayList<>();
		try {
			for (Activity activity : getProfileDocument().getComponent()
					.getActivity())
				activityProfiles.add(new ActivityProfileImpl(this, activity));
		} catch (ComponentException e) {
		}
		return activityProfiles;
	}

	@Override
	public List<SemanticAnnotationProfile> getActivitySemanticAnnotationProfiles()
			throws ComponentException {
		List<SemanticAnnotationProfile> saProfiles = new ArrayList<>();
		List<ActivityProfile> activityProfiles = getActivityProfiles();
		activityProfiles.addAll(parent().getActivityProfiles());
		for (ActivityProfile activityProfile : activityProfiles)
			saProfiles.addAll(activityProfile.getSemanticAnnotations());
		if (!isBase())
			saProfiles.addAll(base.getProfile()
					.getActivitySemanticAnnotationProfiles());
		return getUniqueSemanticAnnotationProfiles(saProfiles);
	}

	@Override
	public List<SemanticAnnotationProfile> getSemanticAnnotations()
			throws ComponentException {
		List<SemanticAnnotationProfile> saProfiles = getComponentProfiles();
		saProfiles.addAll(parent().getSemanticAnnotations());
		if (!isBase())
			saProfiles.addAll(base.getProfile().getSemanticAnnotations());
		return saProfiles;
	}

	private List<SemanticAnnotationProfile> getComponentProfiles() {
		List<SemanticAnnotationProfile> saProfiles = new ArrayList<>();
		try {
			for (SemanticAnnotation semanticAnnotation : getProfileDocument()
					.getComponent().getSemanticAnnotation())
				saProfiles.add(new SemanticAnnotationProfileImpl(this,
						semanticAnnotation));
		} catch (ComponentException e) {
		}
		return saProfiles;
	}

	private List<SemanticAnnotationProfile> getUniqueSemanticAnnotationProfiles(
			List<SemanticAnnotationProfile> semanticAnnotationProfiles) {
		List<SemanticAnnotationProfile> uniqueSemanticAnnotations = new ArrayList<>();
		Set<OntProperty> predicates = new HashSet<>();
		for (SemanticAnnotationProfile semanticAnnotationProfile : semanticAnnotationProfiles) {
			OntProperty prop = semanticAnnotationProfile.getPredicate();
			if (prop != null && !predicates.contains(prop)) {
				predicates.add(prop);
				uniqueSemanticAnnotations.add(semanticAnnotationProfile);
			}
		}
		return uniqueSemanticAnnotations;
	}

	@Override
	public ExceptionHandling getExceptionHandling() {
		try {
			if (exceptionHandling == null
					&& getProfileDocument().getComponent()
							.getExceptionHandling() != null)
				exceptionHandling = new ExceptionHandling(getProfileDocument()
						.getComponent().getExceptionHandling());
		} catch (ComponentException e) {
		}
		return exceptionHandling;
	}

	@Override
	public String toString() {
		return "ComponentProfile" + "\n  Name : " + getName()
				+ "\n  Description : " + getDescription()
				+ "\n  InputPortProfiles : " + getInputPortProfiles()
				+ "\n  OutputPortProfiles : " + getOutputPortProfiles();
	}

	@Override
	public int hashCode() {
		return 31 + ((getId() == null) ? 0 : getId().hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ComponentProfileImpl other = (ComponentProfileImpl) obj;
		if (!loaded || !other.loaded)
			return false;
		if (getId() == null)
			return other.getId() == null;
		return getId().equals(other.getId());
	}

	public OntClass getClass(String className) {
		try {
			for (Ontology ontology : getProfileDocument().getOntology()) {
				OntModel ontModel = getOntology(ontology.getId());
				if (ontModel != null) {
					OntClass result = ontModel.getOntClass(className);
					if (result != null)
						return result;
				}
			}
		} catch (ComponentException e) {
		}
		return null;
	}

	@Override
	public void delete() throws ComponentException {
		throw new ComponentException("Deletion not supported.");
	}
}

/**
 * A simple do-nothing implementation of a profile. Used when there's no other
 * option for what a <i>real</i> profile extends.
 * 
 * @author Donal Fellows
 */
final class EmptyProfile implements
		org.apache.taverna.component.api.profile.Profile {
	@Override
	public String getName() {
		return "";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public Registry getComponentRegistry() {
		return null;
	}

	@Override
	public String getXML() throws ComponentException {
		throw new ComponentException("No document.");
	}

	@Override
	public Profile getProfileDocument() {
		return new Profile();
	}

	@Override
	public String getId() {
		return "";
	}

	@Override
	public String getOntologyLocation(String ontologyId) {
		return "";
	}

	@Override
	public Map<String, String> getPrefixMap() {
		return emptyMap();
	}

	@Override
	public OntModel getOntology(String ontologyId) {
		return null;
	}

	@Override
	public List<PortProfile> getInputPortProfiles() {
		return emptyList();
	}

	@Override
	public List<SemanticAnnotationProfile> getInputSemanticAnnotationProfiles() {
		return emptyList();
	}

	@Override
	public List<PortProfile> getOutputPortProfiles() {
		return emptyList();
	}

	@Override
	public List<SemanticAnnotationProfile> getOutputSemanticAnnotationProfiles() {
		return emptyList();
	}

	@Override
	public List<org.apache.taverna.component.api.profile.ActivityProfile> getActivityProfiles() {
		return emptyList();
	}

	@Override
	public List<SemanticAnnotationProfile> getActivitySemanticAnnotationProfiles() {
		return emptyList();
	}

	@Override
	public List<SemanticAnnotationProfile> getSemanticAnnotations() {
		return emptyList();
	}

	@Override
	public ExceptionHandling getExceptionHandling() {
		return null;
	}

	@Override
	public void delete() throws ComponentException {
		throw new ComponentException("Deletion forbidden.");
	}
}