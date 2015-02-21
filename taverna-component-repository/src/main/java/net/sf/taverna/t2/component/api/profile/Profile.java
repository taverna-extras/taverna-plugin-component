package net.sf.taverna.t2.component.api.profile;

import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.component.api.NamedItem;
import net.sf.taverna.t2.component.api.Registry;
import net.sf.taverna.t2.component.api.ComponentException;

import com.hp.hpl.jena.ontology.OntModel;

public interface Profile extends NamedItem, AnnotatedElement {

	Registry getComponentRegistry();

	String getXML() throws ComponentException;

	net.sf.taverna.t2.component.api.profile.doc.Profile getProfileDocument()
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
