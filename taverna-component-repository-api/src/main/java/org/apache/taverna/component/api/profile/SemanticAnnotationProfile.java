package org.apache.taverna.component.api.profile;

import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;

public interface SemanticAnnotationProfile {

	/**
	 * Returns the ontology that defines semantic annotation.
	 * 
	 * @return the ontology that defines semantic annotation
	 */
	OntModel getOntology();

	/**
	 * Returns the predicate for the semantic annotation.
	 * 
	 * @return the predicate for the semantic annotation
	 */
	OntProperty getPredicate();

	String getPredicateString();

	String getClassString();

	/**
	 * Returns the individual that the semantic annotation must use.
	 * 
	 * May be null if no explicit individual is required.
	 * 
	 * @return the individual that the semantic annotation must use
	 */
	Individual getIndividual();

	/**
	 * Returns the individuals in the range of the predicate defined in the
	 * ontology.
	 * 
	 * @return the individuals in the range of the predicate defined in the
	 *         ontology
	 */
	List<Individual> getIndividuals();

	Integer getMinOccurs();

	Integer getMaxOccurs();

	OntClass getRangeClass();

}