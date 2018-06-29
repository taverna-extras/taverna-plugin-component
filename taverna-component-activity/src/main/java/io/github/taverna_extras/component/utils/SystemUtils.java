package io.github.taverna_extras.component.utils;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import io.github.taverna_extras.component.api.ComponentException;
import io.github.taverna_extras.component.registry.api.Description;
import org.apache.taverna.configuration.app.ApplicationConfiguration;
import org.apache.taverna.platform.execution.api.InvalidWorkflowException;
import org.apache.taverna.platform.execution.api.WorkflowCompiler;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.workflowmodel.Dataflow;

public class SystemUtils {
	private static final String T2FLOW_TYPE = "application/vnd.taverna.t2flow+xml";
	private static final String SCUFL2_TYPE = "application/vnd.taverna.scufl2.workflow-bundle";
	private ApplicationConfiguration appConfig;
	private WorkflowBundleIO workflowBundleIO;
	private List<WorkflowCompiler> compilers;

	public byte[] serializeBundle(WorkflowBundle bundle) throws ComponentException {
		try {
			ByteArrayOutputStream dataflowStream = new ByteArrayOutputStream();
			workflowBundleIO.writeBundle(bundle, dataflowStream, SCUFL2_TYPE);
			return dataflowStream.toByteArray();
		} catch (Exception e) {
			throw new ComponentException(
					"failed to serialize component implementation", e);
		}
	}

	private String determineMediaTypeForFilename(File file) {
		String[] pieces = file.getName().split("\\.");
		switch (pieces[pieces.length - 1]) {
		case "t2flow":
			return T2FLOW_TYPE;
		default:
			return SCUFL2_TYPE;
		}
	}

	public void saveBundle(WorkflowBundle bundle, File file) throws Exception {
		workflowBundleIO.writeBundle(bundle, file,
				determineMediaTypeForFilename(file));
	}

	public WorkflowBundle getBundleFromUri(String uri) throws Exception {
		return workflowBundleIO.readBundle(new URL(uri), null);
	}

	public WorkflowBundle getBundle(File file) throws Exception {
		return workflowBundleIO.readBundle(file, null);
	}

	public static JAXBElement<?> getElement(Description d, String name)
			throws ComponentException {
		for (Object o : d.getContent())
			if (o instanceof JAXBElement) {
				JAXBElement<?> el = (JAXBElement<?>) o;
				if (el.getName().getLocalPart().equals(name))
					return el;
			}
		throw new ComponentException("no " + name + " element");
	}

	public static String getElementString(Description d, String name)
			throws ComponentException {
		return getElement(d, name).getValue().toString().trim();
	}

	public static String getValue(Description d) {
		StringBuilder sb = new StringBuilder();
		for (Object o : d.getContent())
			if (!(o instanceof JAXBElement))
				sb.append(o);
		return sb.toString();
	}

	public File getApplicationHomeDir() {
		return appConfig.getApplicationHomeDir().toFile();
	}

	public void setAppConfig(ApplicationConfiguration appConfig) {
		this.appConfig = appConfig;
	}

	public void setWorkflowBundler(WorkflowBundleIO workflowBundler) {
		this.workflowBundleIO = workflowBundler;
	}

	public void setCompilers(List<WorkflowCompiler> compilers) {
		this.compilers = compilers;
	}

	public Dataflow compile(WorkflowBundle implementation)
			throws InvalidWorkflowException {
		InvalidWorkflowException exn = null;
		if (compilers != null)
			for (WorkflowCompiler c : new ArrayList<>(compilers))
				try {
					return c.getDataflow(implementation);
				} catch (InvalidWorkflowException e) {
					if (exn == null)
						exn = e;
					continue;
				}
		if (exn != null)
			throw exn;
		throw new InvalidWorkflowException("no compiler available");
	}
}
