/**
 * 
 */
package net.sf.taverna.t2.component.ui.file;

import static java.util.Collections.sort;
import static net.sf.taverna.t2.component.ui.util.ComponentHealthCheck.FAILS_PROFILE;
import static net.sf.taverna.t2.component.annotation.SemanticAnnotationUtils.getDisplayName;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JTextArea;

import org.apache.taverna.component.api.profile.SemanticAnnotationProfile;

import net.sf.taverna.t2.component.ui.util.ComponentHealthCheck;
import net.sf.taverna.t2.visit.VisitKind;
import net.sf.taverna.t2.visit.VisitReport;
import net.sf.taverna.t2.workbench.report.explainer.VisitExplainer;

/**
 * @author alanrw
 */
public class ComponentDataflowHealthCheckExplainer implements VisitExplainer {
	private static final Comparator<SemanticAnnotationProfile> comparator = new Comparator<SemanticAnnotationProfile>() {
		@Override
		public int compare(SemanticAnnotationProfile a,
				SemanticAnnotationProfile b) {
			return getDisplayName(a.getPredicate()).compareTo(
					getDisplayName(b.getPredicate()));
		}
	};

	@Override
	public boolean canExplain(VisitKind vk, int resultId) {
		return vk instanceof ComponentHealthCheck
				&& resultId == FAILS_PROFILE;
	}

	@Override
	public JComponent getExplanation(VisitReport vr) {
		@SuppressWarnings("unchecked")
		Set<SemanticAnnotationProfile> problemProfiles = (Set<SemanticAnnotationProfile>) vr
				.getProperty("problemProfiles");
		List<SemanticAnnotationProfile> sortedList = new ArrayList<>(
				problemProfiles);
		sort(sortedList, comparator);
		StringBuilder text = new StringBuilder();
		for (SemanticAnnotationProfile profile : sortedList)
			text.append(getSemanticProfileExplanation(profile)).append("\n");
		return new JTextArea(text.toString());
	}

	@Override
	public JComponent getSolution(VisitReport vr) {
		return new JTextArea("Correct the semantic annotation");
	}

	private static String getSemanticProfileExplanation(
			SemanticAnnotationProfile p) {
		Integer minOccurs = p.getMinOccurs();
		Integer maxOccurs = p.getMaxOccurs();
		String displayName = getDisplayName(p.getPredicate());
		if (maxOccurs == null)
			return displayName + " must have at least " + minOccurs + " value";
		if (minOccurs.equals(maxOccurs))
			return displayName + " must have " + minOccurs + " value(s)";
		return displayName + " must have between " + minOccurs + " and "
				+ maxOccurs + " value(s)";
	}
}
