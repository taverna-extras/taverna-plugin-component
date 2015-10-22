package org.apache.taverna.component.activity;

import org.apache.taverna.visit.VisitKind;
import org.apache.taverna.visit.Visitor;

public class ComponentHealthCheck extends VisitKind {
	public static final int NO_PROBLEM = 0;
	public static final int OUT_OF_DATE = 10;
	public static final int NON_SHAREABLE = 20;
	public static final int FAILS_PROFILE = 30;

	@Override
	public Class<? extends Visitor<?>> getVisitorClass() {
		return ComponentActivityUpgradeChecker.class;
	}

	private static class Singleton {
		private static ComponentHealthCheck instance = new ComponentHealthCheck();
	}

	public static ComponentHealthCheck getInstance() {
		return Singleton.instance;
	}
}
