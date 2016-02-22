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

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NORTHWEST;
import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static org.apache.log4j.Logger.getLogger;
import static org.apache.taverna.component.ui.annotation.SemanticAnnotationUtils.createSemanticAnnotation;
import static org.apache.taverna.component.ui.annotation.SemanticAnnotationUtils.getDisplayName;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.profile.SemanticAnnotationProfile;

import org.apache.taverna.scufl2.api.common.AbstractNamed;
import org.apache.taverna.scufl2.api.common.Named;

import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.ui.views.contextualviews.ContextualView;

/**
 * @author alanrw
 */
public abstract class AbstractSemanticAnnotationContextualView extends
		ContextualView {
	private static final long serialVersionUID = 3567849347002793442L;
	private static final Logger logger = getLogger(SemanticAnnotationContextualView.class);

	private final FileManager fileManager;

	public AbstractSemanticAnnotationContextualView(FileManager fileManager,
			boolean allowChange) {
		super();
		this.fileManager = fileManager;
		this.allowChange = allowChange;
	}

	private final boolean allowChange;
	private JPanel panel;
	private AbstractNamed annotated;
	private List<SemanticAnnotationProfile> semanticAnnotationProfiles;
	private Model model;
	private Resource subject;

	private static Comparator<SemanticAnnotationProfile> comparator = new Comparator<SemanticAnnotationProfile>() {
		@Override
		public int compare(SemanticAnnotationProfile arg0,
				SemanticAnnotationProfile arg1) {
			String d0 = getDisplayName(arg0.getPredicate());
			String d1 = getDisplayName(arg1.getPredicate());
			return CASE_INSENSITIVE_ORDER.compare(d0, d1);
		}
	};

	@Override
	public JComponent getMainFrame() {
		return panel;
	}

	@Override
	public int getPreferredPosition() {
		return 510;
	}

	protected final void initialise() {
		populateModel();
		if (panel == null)
			panel = new JPanel(new GridBagLayout());
		else
			panel.removeAll();
		populatePanel(panel);
	}

	public void removeStatement(Statement statement) {
		model.remove(statement);
		// populatePanel(panel);
		updateSemanticAnnotation();
	}

	public void addStatement(Statement statement) {
		model.add(statement);
		// populatePanel(panel);
		updateSemanticAnnotation();
	}

	public void changeStatement(Statement origStatement, OntProperty predicate,
			RDFNode node) {
		if (predicate == null)
			return;
		model.remove(origStatement);
		model.add(subject, predicate, node);
		// populatePanel(panel);
		updateSemanticAnnotation();
	}

	public void addStatement(OntProperty predicate, RDFNode node) {
		if (predicate == null)
			return;
		model.add(subject, predicate, node);
		// populatePanel(panel);
		updateSemanticAnnotation();
	}

	@Override
	public void refreshView() {
		populatePanel(panel);
	}

	// public void addModel(Model model) {
	// this.model.add(model);
	// initialise();
	// updateSemanticAnnotation();
	// }

	public void updateSemanticAnnotation() {
		try {
			createSemanticAnnotation(fileManager.getCurrentDataflow(),
					annotated, model);
		} catch (IOException e) {
			logger.error("failed to add semantic annotation", e);
		}
	}

	public void setAnnotated(Named annotated) {
		this.annotated = (AbstractNamed) annotated;
	}

	public void setSemanticAnnotationProfiles(
			List<SemanticAnnotationProfile> profiles) {
		this.semanticAnnotationProfiles = profiles;
	}

	public Model getModel() {
		return model;
	}

	private void populateModel() {
		this.model = SemanticAnnotationUtils.populateModel(fileManager
				.getCurrentDataflow());
		this.subject = model.createResource(annotated.getURI().toASCIIString());
	}

	public Named getAnnotated() {
		return annotated;
	}

	private void populatePanel(JPanel panel) {
		panel.removeAll();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = NORTHWEST;
		gbc.fill = HORIZONTAL;
		gbc.gridx = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		panel.add(new JLabel("Reading semantic annotations"), gbc);
		revalidate();
		initView();
		new StatementsReader().execute();
	}

	private Set<Statement> listStatements(OntProperty predicate) {
		if (predicate == null)
			return Collections.emptySet();
		return model.listStatements(subject, predicate, (RDFNode) null).toSet();
	}

	private void populateViewWithPredicates(GridBagConstraints gbc,
			Map<SemanticAnnotationProfile, Set<Statement>> profileStatements,
			Set<Statement> statements,
			Set<SemanticAnnotationProfile> unresolvablePredicates) {
		for (Entry<SemanticAnnotationProfile, Set<Statement>> entry : profileStatements
				.entrySet()) {
			panel.add(
					new SemanticAnnotationPanel(this, entry.getKey(), entry
							.getValue(), allowChange), gbc);
			panel.add(new JSeparator(), gbc);
		}
		for (SemanticAnnotationProfile semanticAnnotationProfile : unresolvablePredicates) {
			panel.add(
					new UnresolveablePredicatePanel(semanticAnnotationProfile),
					gbc);
			panel.add(new JSeparator(), gbc);
		}

		if (semanticAnnotationProfiles.isEmpty())
			panel.add(new JLabel("No annotations possible"), gbc);
		for (Statement s : statements)
			panel.add(new UnrecognizedStatementPanel(s), gbc);

		gbc.weighty = 1;
		panel.add(new JPanel(), gbc);
	}

	private class StatementsReader extends SwingWorker<Void, Object> {
		private Map<SemanticAnnotationProfile, Set<Statement>> profileStatements = new TreeMap<>(
				comparator);
		private Set<Statement> statements;
		private Set<SemanticAnnotationProfile> unresolvablePredicates = new HashSet<>();

		@Override
		protected Void doInBackground() throws Exception {
			try {
				parseStatements();
			} catch (Exception e) {
				logger.error("failed to parse annotation statements", e);
				throw e;
			}
			return null;
		}

		private void parseStatements() {
			statements = listStatements(null);
			for (SemanticAnnotationProfile semanticAnnotationProfile : semanticAnnotationProfiles) {
				OntProperty predicate = semanticAnnotationProfile
						.getPredicate();
				if (predicate == null) {
					unresolvablePredicates.add(semanticAnnotationProfile);
					continue;
				}

				Set<Statement> statementsWithPredicate = listStatements(predicate);
				profileStatements.put(semanticAnnotationProfile,
						statementsWithPredicate);
				statements.removeAll(statementsWithPredicate);
			}
		}

		@Override
		protected void done() {
			panel.removeAll();
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = NORTHWEST;
			gbc.fill = HORIZONTAL;
			gbc.gridx = 0;
			gbc.weightx = 1;
			gbc.weighty = 0;
			gbc.insets = new Insets(5, 5, 5, 5);

			try {
				get();
				populateViewWithPredicates(gbc, profileStatements, statements,
						unresolvablePredicates);
			} catch (ExecutionException | InterruptedException e) {
				logger.error(e);
				panel.add(new JLabel("Unable to read semantic annotations"),
						gbc);
			}

			revalidate();
			initView();
		}
	}
}
