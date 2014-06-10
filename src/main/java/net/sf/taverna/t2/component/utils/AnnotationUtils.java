package net.sf.taverna.t2.component.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.WeakHashMap;

import uk.org.taverna.scufl2.api.annotation.Annotation;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;

public class AnnotationUtils {
	private static final String TITLE_ANNOTATION = "http://purl.org/dc/terms/title";
	private static final String DESCRIPTION_ANNOTATION = "http://purl.org/dc/terms/description";
	private Scufl2Tools tools = new Scufl2Tools();
	private URITools uris = new URITools();

	private WeakHashMap<Child<?>, Model> cache = new WeakHashMap<>();
	public Statement getAnnotation(Child<WorkflowBundle> subject,
			String uriForAnnotation) throws IOException {
		WorkflowBundle bundle = subject.getParent();
		Model m = cache.get(subject);
		if (m == null) {
			m = ModelFactory.createDefaultModel();
			for (Annotation a : tools.annotationsFor(subject,
					subject.getParent())) {
				if (a.getBody().isAbsolute())
					continue;
				String rdf = bundle.getResources().getResourceAsString(
						a.getBody().getPath());
				m.read(new StringReader(rdf), a.getBody().toString());
			}
			cache.put(subject, m);
		}
		return m.getResource(uris.uriForBean(subject).toString()).getProperty(
				m.getProperty(uriForAnnotation));
	}

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
