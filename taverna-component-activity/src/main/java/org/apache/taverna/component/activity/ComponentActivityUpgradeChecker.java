package org.apache.taverna.component.activity;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import static org.apache.log4j.Logger.getLogger;
import static org.apache.taverna.component.activity.ComponentHealthCheck.OUT_OF_DATE;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.registry.ComponentUtil;
import org.apache.taverna.visit.VisitKind;
import org.apache.taverna.visit.VisitReport;
import static org.apache.taverna.visit.VisitReport.Status.WARNING;
import org.apache.taverna.workflowmodel.health.HealthChecker;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author alanrw
 * 
 */
public class ComponentActivityUpgradeChecker implements
		HealthChecker<ComponentActivity> {
	private static final String OUTDATED_MSG = "Component out of date";
	private static final VisitKind visitKind = ComponentHealthCheck
			.getInstance();
	private static Logger logger = getLogger(ComponentActivityUpgradeChecker.class);
	private ComponentUtil utils;

	@Required
	public void setComponentUtil(ComponentUtil util) {
		this.utils = util;
	}

	@Override
	public boolean canVisit(Object o) {
		return o instanceof ComponentActivity;
	}

	@Override
	public boolean isTimeConsuming() {
		return false;
	}

	@Override
	public VisitReport visit(ComponentActivity activity, List<Object> ancestry) {
		ComponentActivityConfigurationBean config = activity.getConfigBean();
		int versionNumber = config.getComponentVersion();
		int latestVersion = 0;

		try {
			latestVersion = utils
					.getComponent(config.getRegistryBase(),
							config.getFamilyName(), config.getComponentName())
					.getComponentVersionMap().lastKey();
		} catch (ComponentException e) {
			logger.error("failed to get component description", e);
		}

		if (latestVersion > versionNumber)
			return new VisitReport(visitKind, activity, OUTDATED_MSG,
					OUT_OF_DATE, WARNING);
		return null;
	}

}
