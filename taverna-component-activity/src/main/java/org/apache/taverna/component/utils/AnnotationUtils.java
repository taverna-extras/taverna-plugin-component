package org.apache.taverna.component.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.WeakHashMap;

import org.apache.taverna.scufl2.api.annotation.Annotation;
import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.common.URITools;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.ucfpackage.UCFPackage.ResourceEntry;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Statement;

public class AnnotationUtils {
	private static final String TITLE_ANNOTATION = "http://purl.org/dc/terms/title";
	private static final String DESCRIPTION_ANNOTATION = "http://purl.org/dc/terms/description";
	private Scufl2Tools tools = new Scufl2Tools();
	private URITools uris = new URITools();

	public Model getAnnotationModel(Child<WorkflowBundle> subject) throws IOException {
		return ModelFactory.createDefaultModel().add(getModel(subject));
	}

	private WeakHashMap<Child<?>, Model> cache = new WeakHashMap<>();

	private static void readParse(Model model, WorkflowBundle bundle, String path)
			throws IOException {
		model.read(
				new StringReader(bundle.getResources()
						.getResourceAsString(path)), bundle.getGlobalBaseURI()
						.resolve(path).toString(), "TTL");
	}

	public Model getModel(Child<WorkflowBundle> subject) throws IOException {
		WorkflowBundle bundle = subject.getParent();
		Model m = cache.get(subject);
		if (m == null) {
			m = ModelFactory.createDefaultModel();
			long initialSize = m.size();
			for (Annotation a : tools.annotationsFor(subject,
					subject.getParent()))
				if (!a.getBody().isAbsolute())
					readParse(m, bundle, a.getBody().getPath());
			if (m.size() == initialSize)
				for (ResourceEntry o : bundle.getResources()
						.listResources("annotation").values())
					readParse(m, bundle, o.getPath());
			cache.put(subject, m);
		}
		return m;
	}

	public Statement getAnnotation(Child<WorkflowBundle> subject,
			String uriForAnnotation) throws IOException {
		Model m = getModel(subject);
		Property p = m.getProperty(uriForAnnotation);
		return m.getResource(uris.uriForBean(subject).toString()).getProperty(
				p);
	}

	/** Get the title of the main workflow in a workflow bundle. */
	public String getTitle(WorkflowBundle bundle, String defaultTitle) {
		try {
			Statement s = getAnnotation(bundle.getMainWorkflow(),
					TITLE_ANNOTATION);
			if (s != null && s.getObject().isLiteral())
				return s.getObject().asLiteral().getString();
		} catch (IOException e) {
			// TODO log this error?
		}
		return defaultTitle;
	}

	/** Get the description of the main workflow in a workflow bundle. */
	public String getDescription(WorkflowBundle bundle, String defaultDescription) {
		try {
			Statement s = getAnnotation(bundle.getMainWorkflow(),
					DESCRIPTION_ANNOTATION);
			if (s != null && s.getObject().isLiteral())
				return s.getObject().asLiteral().getString();
		} catch (IOException e) {
			// TODO log this error?
		}
		return defaultDescription;
	}
}
