package org.apache.taverna.component.api.profile;
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

import java.util.List;
import java.util.Map;

import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.NamedItem;
import org.apache.taverna.component.api.Registry;

import org.apache.jena.ontology.OntModel;

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
