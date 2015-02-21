/**
 * 
 */
package org.apache.taverna.component.ui.annotation;

import static java.lang.String.format;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.taverna.component.api.profile.SemanticAnnotationProfile;

/**
 * @author alanrw
 */
@SuppressWarnings("serial")
public class UnresolveablePredicatePanel extends JPanel {
	public UnresolveablePredicatePanel(
			SemanticAnnotationProfile semanticAnnotationProfile) {
		setLayout(new BorderLayout());
		setBorder(new GreyBorder());
		add(new JLabel(format("Unable to resolve %s in the ontology",
				semanticAnnotationProfile.getPredicateString())));
	}
}
