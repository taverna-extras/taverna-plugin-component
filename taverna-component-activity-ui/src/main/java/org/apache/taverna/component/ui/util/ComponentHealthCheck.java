package org.apache.taverna.component.ui.util;

import static org.apache.log4j.Logger.getLogger;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.ComponentFactory;
import org.apache.taverna.component.api.Version;
import org.apache.taverna.component.ui.ComponentActivityConfigurationBean;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.validation.correctness.DefaultDispatchingVisitor;

public class ComponentHealthCheck extends VisitKind {
	public static final int NO_PROBLEM = 0;
	public static final int OUT_OF_DATE = 10;
	public static final int NON_SHAREABLE = 20;
	public static final int FAILS_PROFILE = 30;
	private static Logger logger = getLogger(ComponentHealthCheck.class);
	private static final String OUTDATED_MSG = "Component out of date";

	private ComponentFactory factory;

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
	}

	public List<Object> checkForOutdatedComponents(WorkflowBundle bundle) {
		UpgradeChecker uc = new UpgradeChecker();
		bundle.accept(uc);
		return uc.warnings;
	}

	private class UpgradeChecker extends DefaultDispatchingVisitor {
		ComponentFactory factory;
		List<Object> warnings = new ArrayList<>();

		@Override
		public void visitActivity(Activity activity) {
			ComponentActivityConfigurationBean config = new ComponentActivityConfigurationBean(
					activity.getConfiguration().getJson(), factory);
			Version v;
			try {
				v = config.getVersion();
			} catch (ComponentException e) {
				logger.error("failed to get component description", e);
				warnings.add(e);//FIXME Just putting the exception in here isn't good
				return;
			}
			visitComponent(activity, v);
		}
		protected void visitComponent(Activity activity, Version version) {
			int latest = version.getComponent().getComponentVersionMap().lastKey();
			if (latest > version.getVersionNumber())
				warnings.add(new VisitReport(ComponentHealthCheck.this,
						activity, OUTDATED_MSG, OUT_OF_DATE,
						VisitReport.Status.WARNING));
		}
	}
}
