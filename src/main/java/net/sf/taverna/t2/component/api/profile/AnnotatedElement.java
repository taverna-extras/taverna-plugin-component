package net.sf.taverna.t2.component.api.profile;

import java.util.List;

import net.sf.taverna.t2.component.api.ComponentException;

public interface AnnotatedElement {
	List<SemanticAnnotationProfile> getSemanticAnnotations()
			throws ComponentException;
}
