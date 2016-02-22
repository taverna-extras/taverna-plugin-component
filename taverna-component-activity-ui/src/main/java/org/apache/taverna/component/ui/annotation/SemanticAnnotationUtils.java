/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.component.ui.annotation;

import static org.apache.jena.rdf.model.ModelFactory.createDefaultModel;
import static org.apache.log4j.Logger.getLogger;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.profile.Profile;
import org.apache.taverna.component.api.profile.SemanticAnnotationProfile;

import org.apache.taverna.scufl2.api.annotation.Annotation;
import org.apache.taverna.scufl2.api.common.AbstractNamed;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;

import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

/**
 * @author David Withers
 */
public class SemanticAnnotationUtils {
	protected static final String ENCODING = "TURTLE";
	/* Pretend-base for making relative URIs */
	private static String BASE = "widget://4aa8c93c-3212-487c-a505-3e337adf54a3/";
	private static Logger logger = getLogger(SemanticAnnotationUtils.class);

	public static String getObjectName(Statement statement) {
		return getDisplayName(statement.getObject());
	}

	public static String getDisplayName(RDFNode node) {
		if (node == null)
			return "unknown";
		else if (node.isAnon())
			return "anon";
		else if (node.isLiteral())
			return node.asLiteral().getLexicalForm();
		else if (node.isResource()) {
			Resource resource = node.asResource();
			if (resource instanceof OntResource) {
				String label = ((OntResource) resource).getLabel(null);
				if (label != null)
					return label;
			}
			String localName = resource.getLocalName();
			if ((localName != null) && !localName.isEmpty())
				return localName;
			return resource.toString();
		} else
			return "unknown";
	}

	public static Annotation findSemanticAnnotation(AbstractNamed annotated) {
		for (Annotation annotation : annotated.getAnnotations())
			return annotation;
		return null;
	}

	public static String getStrippedAnnotationContent(Annotation annotation)
			throws IOException {
		AbstractNamed target = (AbstractNamed) annotation.getTarget();
		return annotation.getRDFContent().replace(
				target.getRelativeURI(annotation).toASCIIString(), BASE);
	}

	public static Annotation createSemanticAnnotation(WorkflowBundle bundle,
			AbstractNamed target, Model model) throws IOException {
		Calendar now = new GregorianCalendar();
		Annotation annotation = new Annotation();
		annotation.setParent(bundle);
		String path = annotation.getResourcePath();
		annotation.setTarget(target);
		// annotation.setAnnotatedBy(annotatedBy);
		annotation.setAnnotatedAt(now);
		// annotation.setSerializedBy(serializedBy);
		annotation.setSerializedAt(now);
		bundle.getResources().addResource(
				"@base<" + target.getRelativeURI(annotation).toASCIIString()
						+ "> .\n" + createTurtle(model), path, "text/rdf+n3");
		return annotation;
	}

	/**
	 * @param model
	 * @return
	 */
	public static String createTurtle(Model model) {
		StringWriter stringWriter = new StringWriter();
		model.write(stringWriter, ENCODING, BASE);
		// Workaround for https://issues.apache.org/jira/browse/JENA-132
		return stringWriter.toString().replace(BASE, "");
	}

	public static Model populateModel(WorkflowBundle annotated) {
		Model result = createDefaultModel();
		try {
			for (Annotation a : annotated.getAnnotations())
				populateModelFromString(result, a.getRDFContent());
		} catch (Exception e) {
			logger.error("failed to construct semantic annotation model", e);
		}
		return result;
	}

	public static void populateModel(Model result, Annotation annotation)
			throws IOException {
		AbstractNamed target = (AbstractNamed) annotation.getTarget();
		String content = annotation.getRDFContent().replace(
				target.getRelativeURI(annotation).toASCIIString(), BASE);
		populateModelFromString(result, content);
	}

	public static void populateModelFromString(Model result, String content) {
		result.read(new StringReader(content), BASE, ENCODING);
	}

	public static Resource createBaseResource(Model model) {
		return model.createResource(BASE);
	}

	/**
	 * Check if a profile is satisfied by a component.
	 * 
	 * @param bundle
	 *            The component definition.
	 * @param componentProfile
	 *            The profile definition.
	 * @return The set of failed constraints. If empty, the profile is satisfied
	 *         by the component.
	 */
	public static Set<SemanticAnnotationProfile> checkComponent(
			WorkflowBundle bundle, Profile componentProfile) {
		// TODO Check port presence by name
		Set<SemanticAnnotationProfile> problemProfiles = new HashSet<>();
		Model model = populateModel(bundle);
		Set<Statement> statements = model.listStatements().toSet();
		try {
			for (SemanticAnnotationProfile saProfile : componentProfile
					.getSemanticAnnotations()) {
				OntProperty predicate = saProfile.getPredicate();
				if (predicate == null)
					continue;
				int count = 0;
				for (Statement statement : statements)
					if (statement.getPredicate().equals(predicate))
						count++;
				if (count < saProfile.getMinOccurs())
					problemProfiles.add(saProfile);
				if (saProfile.getMaxOccurs() != null
						&& count > saProfile.getMaxOccurs())
					// The UI should prevent this, but check anyway
					problemProfiles.add(saProfile);
			}
		} catch (ComponentException e) {
			logger.error("failed to look up profiles for semantic annotations", e);
		}
		return problemProfiles;
	}
}
