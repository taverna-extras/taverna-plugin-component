package org.apache.taverna.component.api.profile;

import java.util.List;
import java.util.Map;

import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.NamedItem;
import org.apache.taverna.component.api.Registry;

import com.hp.hpl.jena.ontology.OntModel;

public interface Profile extends NamedItem, AnnotatedElement {

	Registry getComponentRegistry();

	String getXML() throws ComponentException;

	org.apache.taverna.component.api.profile.doc.Profile getProfileDocument()
			throws ComponentException;

	String getId();

	String getOntologyLocation(String ontologyId);

	Map<String, String> getPrefixMap() throws ComponentException;

	OntModel getOntology(String ontologyId);

	List<PortProfile> getInputPortProfiles();

	List<SemanticAnnotationProfile> getInputSemanticAnnotationProfiles()
			throws ComponentException;

	List<PortProfile> getOutputPortProfiles();

	List<SemanticAnnotationProfile> getOutputSemanticAnnotationProfiles()
			throws ComponentException;

	List<ActivityProfile> getActivityProfiles();

	List<SemanticAnnotationProfile> getActivitySemanticAnnotationProfiles()
			throws ComponentException;

	ExceptionHandling getExceptionHandling();

	void delete() throws ComponentException;
}
