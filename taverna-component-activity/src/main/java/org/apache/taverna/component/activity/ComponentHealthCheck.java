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
