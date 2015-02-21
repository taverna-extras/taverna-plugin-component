/**
 * 
 */
package org.apache.taverna.component.ui.file;

import static net.sf.taverna.t2.visit.VisitReport.Status.SEVERE;
import static org.apache.log4j.Logger.getLogger;
import static org.apache.taverna.component.ui.annotation.SemanticAnnotationUtils.checkComponent;
import static org.apache.taverna.component.ui.util.ComponentHealthCheck.FAILS_PROFILE;

import java.util.List;
import java.util.Set;

import net.sf.taverna.t2.visit.VisitReport;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workflowmodel.health.HealthChecker;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.ComponentFactory;
import org.apache.taverna.component.api.Family;
import org.apache.taverna.component.api.Version;
import org.apache.taverna.component.api.profile.SemanticAnnotationProfile;
import org.apache.taverna.component.ui.util.ComponentHealthCheck;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;

/**
 * @author alanrw
 */
public class ComponentDataflowHealthChecker implements HealthChecker<Dataflow> {
	private static final String PROFILE_UNSATISFIED_MSG = "Workflow does not satisfy component profile";
	private static Logger logger = getLogger(ComponentDataflowHealthChecker.class);

	private FileManager fm;
	private ComponentHealthCheck visitType = ComponentHealthCheck.getInstance(); //FIXME beaninject?
	private ComponentFactory factory;

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
	}

	public void setFileManager(FileManager fm) {
		this.fm = fm;
	}

	private Version.ID getSource(Object o) {
		return (Version.ID) fm.getDataflowSource((WorkflowBundle) o);
	}

	public void checkProfileSatisfied(WorkflowBundle bundle) {
		//FIXME
	}
	@Override
	public boolean canVisit(Object o) {
		try {
			return getSource(o) != null;
		} catch (IllegalArgumentException e) {
			// Not open?
		} catch (ClassCastException e) {
			// Not dataflow? Not component?
		}
		return false;
	}

	@Override
	public VisitReport visit(WorkflowBundle dataflow, List<Object> ancestry) {
		try {
			Version.ID ident = getSource(dataflow);
			Family family = factory.getFamily(ident.getRegistryBase(),
					ident.getFamilyName());

			Set<SemanticAnnotationProfile> problemProfiles = checkComponent(
					dataflow, family.getComponentProfile());
			if (problemProfiles.isEmpty())
				return null;

			VisitReport visitReport = new VisitReport(visitType, dataflow,
					PROFILE_UNSATISFIED_MSG, FAILS_PROFILE, SEVERE);
			visitReport.setProperty("problemProfiles", problemProfiles);
			return visitReport;
		} catch (ComponentException e) {
			logger.error(
					"failed to comprehend profile while checking for match", e);
			return null;
		}
	}
}
