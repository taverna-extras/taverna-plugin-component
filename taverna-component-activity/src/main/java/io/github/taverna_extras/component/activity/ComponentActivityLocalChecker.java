package io.github.taverna_extras.component.activity;
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

import static io.github.taverna_extras.component.activity.ComponentHealthCheck.NON_SHAREABLE;

import java.util.List;
import org.apache.taverna.visit.VisitKind;
import org.apache.taverna.visit.VisitReport;
import static org.apache.taverna.visit.VisitReport.Status.WARNING;
import org.apache.taverna.workflowmodel.health.HealthChecker;

/**
 * Component health checker
 * 
 */
public class ComponentActivityLocalChecker implements
		HealthChecker<ComponentActivity> {
	private static final VisitKind visitKind = ComponentHealthCheck
			.getInstance();

	@Override
	public boolean canVisit(Object o) {
		/*
		 * Return True if we can visit the object. We could do deeper (but not
		 * time consuming) checks here, for instance if the health checker only
		 * deals with ComponentActivity where a certain configuration option is
		 * enabled.
		 */
		return o instanceof ComponentActivity;
	}

	@Override
	public boolean isTimeConsuming() {
		/*
		 * Return true if the health checker does a network lookup or similar
		 * time consuming checks, in which case it would only be performed when
		 * using File->Validate workflow or File->Run.
		 */
		return false;
	}

	@Override
	public VisitReport visit(ComponentActivity activity, List<Object> ancestry) {
		if (!activity.getConfigBean().getRegistryBase().getProtocol()
				.startsWith("http"))
			return new VisitReport(visitKind, activity,
					"Local component makes workflow non-shareable",
					NON_SHAREABLE, WARNING);
		return null;
	}

}
