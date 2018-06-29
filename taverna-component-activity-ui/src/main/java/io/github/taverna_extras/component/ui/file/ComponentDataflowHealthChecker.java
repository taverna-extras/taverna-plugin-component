/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package io.github.taverna_extras.component.ui.file;

import static org.apache.log4j.Logger.getLogger;
import static io.github.taverna_extras.component.ui.annotation.SemanticAnnotationUtils.checkComponent;
import static io.github.taverna_extras.component.ui.util.ComponentHealthCheck.FAILS_PROFILE;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import io.github.taverna_extras.component.api.ComponentException;
import io.github.taverna_extras.component.api.ComponentFactory;
import io.github.taverna_extras.component.api.Family;
import io.github.taverna_extras.component.api.Version;
import io.github.taverna_extras.component.api.profile.SemanticAnnotationProfile;
import io.github.taverna_extras.component.ui.util.ComponentHealthCheck;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.visit.VisitReport;
import static org.apache.taverna.visit.VisitReport.Status.SEVERE;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workflowmodel.Dataflow;
import org.apache.taverna.workflowmodel.health.HealthChecker;

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
//
//    @Override
//    public VisitReport visit(Dataflow o, List<Object> ancestry) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public boolean isTimeConsuming() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}
