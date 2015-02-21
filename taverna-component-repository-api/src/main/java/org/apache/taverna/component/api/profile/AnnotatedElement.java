package org.apache.taverna.component.api.profile;

import java.util.List;

import org.apache.taverna.component.api.ComponentException;

public interface AnnotatedElement {
	List<SemanticAnnotationProfile> getSemanticAnnotations()
			throws ComponentException;
}
