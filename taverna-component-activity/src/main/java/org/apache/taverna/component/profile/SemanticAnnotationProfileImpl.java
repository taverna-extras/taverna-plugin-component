package org.apache.taverna.component.profile;
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

import static java.io.File.createTempFile;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.apache.log4j.Logger.getLogger;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.profile.SemanticAnnotationProfile;

import org.apache.taverna.component.api.profile.doc.SemanticAnnotation;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;

/**
 * Definition of a semantic annotation for a component element.
 * 
 * @author David Withers
 */
public class SemanticAnnotationProfileImpl implements SemanticAnnotationProfile {
	private static final Logger log = getLogger(SemanticAnnotationProfileImpl.class);
	private final ComponentProfileImpl componentProfile;
	private final SemanticAnnotation semanticAnnotation;

	public SemanticAnnotationProfileImpl(ComponentProfileImpl componentProfile,
			SemanticAnnotation semanticAnnotation) {
		this.componentProfile = componentProfile;
		this.semanticAnnotation = semanticAnnotation;
	}

	/**
	 * Returns the ontology that defines semantic annotation.
	 * 
	 * @return the ontology that defines semantic annotation
	 */
	@Override
	public OntModel getOntology() {
		String ontology = semanticAnnotation.getOntology();
		if (ontology == null)
			return null;
		return componentProfile.getOntology(ontology);
	}

	/**
	 * Returns the predicate for the semantic annotation.
	 * 
	 * @return the predicate for the semantic annotation
	 */
	@Override
	public OntProperty getPredicate() {
		OntModel ontology = getOntology();
		if (ontology == null)
			return null;
		String predicate = semanticAnnotation.getPredicate();
		if (predicate == null)
			return null;
		if (predicate.contains("foaf")) {
			StringWriter sw = new StringWriter();
			ontology.writeAll(sw, null, "RDF/XML");
			try {
				writeStringToFile(createTempFile("foaf", null), sw.toString());
			} catch (IOException e) {
				log.info("failed to write foaf ontology to temporary file", e);
			}
		}

		return ontology.getOntProperty(predicate);
	}

	@Override
	public String getPredicateString() {
		return semanticAnnotation.getPredicate();
	}

	@Override
	public String getClassString() {
		return semanticAnnotation.getClazz();
	}

	/**
	 * Returns the individual that the semantic annotation must use.
	 * 
	 * May be null if no explicit individual is required.
	 * 
	 * @return the individual that the semantic annotation must use
	 */
	@Override
	public Individual getIndividual() {
		String individual = semanticAnnotation.getValue();
		if (individual == null || individual.isEmpty())
			return null;
		return getOntology().getIndividual(individual);
	}

	/**
	 * Returns the individuals in the range of the predicate defined in the
	 * ontology.
	 * 
	 * @return the individuals in the range of the predicate defined in the
	 *         ontology
	 */
	@Override
	public List<Individual> getIndividuals() {
		OntModel ontology = getOntology();
		OntProperty prop = getPredicate();
		if (ontology == null || prop == null)
			return new ArrayList<>();
		OntResource range = prop.getRange();
		if (range == null)
			return new ArrayList<>();
		return ontology.listIndividuals(range).toList();
	}

	@Override
	public Integer getMinOccurs() {
		return semanticAnnotation.getMinOccurs().intValue();
	}

	@Override
	public Integer getMaxOccurs() {
		try {
			return Integer.valueOf(semanticAnnotation.getMaxOccurs());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return "SemanticAnnotation " + "\n Predicate : " + getPredicate()
				+ "\n Individual : " + getIndividual() + "\n Individuals : "
				+ getIndividuals();
	}

	@Override
	public OntClass getRangeClass() {
		String clazz = this.getClassString();
		if (clazz != null)
			return componentProfile.getClass(clazz);

		OntProperty prop = getPredicate();
		if (prop == null)
			return null;
		OntResource range = prop.getRange();
		if (range != null && range.isClass())
			return range.asClass();
		return null;
	}
}
