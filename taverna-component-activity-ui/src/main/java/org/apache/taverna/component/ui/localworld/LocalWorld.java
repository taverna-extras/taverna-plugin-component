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

package org.apache.taverna.component.ui.localworld;

import static org.apache.jena.rdf.model.ModelFactory.createOntologyModel;
import static org.apache.log4j.Logger.getLogger;
import static org.apache.taverna.component.ui.annotation.SemanticAnnotationUtils.createTurtle;
import static org.apache.taverna.component.ui.annotation.SemanticAnnotationUtils.populateModelFromString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.List;

import org.apache.log4j.Logger;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Resource;
import org.apache.taverna.configuration.app.ApplicationConfiguration;

/**
 * @author alanrw
 */
public class LocalWorld {
	private static final String FILENAME = "localWorld.ttl";
	private static final Logger logger = getLogger(LocalWorld.class);
	protected static final String ENCODING = "TURTLE";
	private static LocalWorld instance = null;

	private OntModel model;

	public synchronized static LocalWorld getInstance() {
		if (instance == null)
			instance = new LocalWorld();
		return instance;
	}

	private LocalWorld() {
		File modelFile = new File(calculateComponentsDirectory(), FILENAME);
		model = createOntologyModel();
		if (modelFile.exists())
			try (Reader in = new InputStreamReader(new FileInputStream(
					modelFile), "UTF-8")) {
				model.read(in, null, ENCODING);
			} catch (IOException e) {
				logger.error("failed to construct local annotation world", e);
			}
	}

	ApplicationConfiguration config;//FIXME beaninject

	public File calculateComponentsDirectory() {
		return new File(config.getApplicationHomeDir(), "components");
	}

	public Individual createIndividual(String urlString, OntClass rangeClass) {
		try {
			return model.createIndividual(urlString, rangeClass);
		} finally {
			saveModel();
		}
	}

	private void saveModel() {
		File modelFile = new File(calculateComponentsDirectory(), FILENAME);
		try (OutputStream out = new FileOutputStream(modelFile)) {
			out.write(createTurtle(model).getBytes("UTF-8"));
		} catch (IOException e) {
			logger.error("failed to save local annotation world", e);
		}
	}

	public List<Individual> getIndividualsOfClass(Resource clazz) {
		return model.listIndividuals(clazz).toList();
	}

	public void addModelFromString(String addedModel) {
		try {
			populateModelFromString(model, addedModel);
		} finally {
			saveModel();
		}
	}
}
