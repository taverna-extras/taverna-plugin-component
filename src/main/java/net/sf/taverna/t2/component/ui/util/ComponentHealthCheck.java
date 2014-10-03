package net.sf.taverna.t2.component.ui.util;

import static org.apache.log4j.Logger.getLogger;

import java.util.List;

import org.apache.log4j.Logger;

import uk.org.taverna.scufl2.api.activity.Activity;
import net.sf.taverna.t2.component.api.ComponentException;
import net.sf.taverna.t2.component.api.ComponentFactory;
import net.sf.taverna.t2.visit.VisitKind;
import net.sf.taverna.t2.visit.VisitReport;
import net.sf.taverna.t2.visit.Visitor;
import net.sf.taverna.t2.workflowmodel.health.HealthChecker;

public class ComponentHealthCheck extends VisitKind {
	public static final int NO_PROBLEM = 0;
	public static final int OUT_OF_DATE = 10;
	public static final int NON_SHAREABLE = 20;
	public static final int FAILS_PROFILE = 30;
	private static Logger logger = getLogger(ComponentHealthCheck.class);
	private static final String OUTDATED_MSG = "Component out of date";
	private static final VisitKind visitKind = ComponentHealthCheck
			.getInstance();

	@Override
	public Class<? extends Visitor<?>> getVisitorClass() {
		return UpgradeChecker.class;
	}

	private static class Singleton {
		private static ComponentHealthCheck instance = new ComponentHealthCheck();
	}

	public static ComponentHealthCheck getInstance() {
		return Singleton.instance;
	}

	static class UpgradeChecker implements HealthChecker<Activity> {
		ComponentFactory factory; //FIXME beaninject

		@Override
		public boolean canVisit(Object o) {
			return o instanceof ComponentActivity;
		}

		@Override
		public boolean isTimeConsuming() {
			return false;
		}

		@Override
		public VisitReport visit(Activity activity,
				List<Object> ancestry) {
			ComponentActivityConfigurationBean config = activity
					.getConfiguration();
			int versionNumber = config.getComponentVersion();
			int latestVersion = 0;

			try {
				latestVersion = factory
						.getComponent(config.getRegistryBase(),
								config.getFamilyName(),
								config.getComponentName())
						.getComponentVersionMap().lastKey();
			} catch (ComponentException e) {
				logger.error("failed to get component description", e);
			}

			if (latestVersion > versionNumber)
				return new VisitReport(visitKind, activity, OUTDATED_MSG,
						OUT_OF_DATE, VisitReport.Status.WARNING);
			return null;
		}
	}
}
