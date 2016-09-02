package org.apache.taverna.component.registry;
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

import static java.lang.System.currentTimeMillis;
import static org.apache.log4j.Logger.getLogger;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.Version;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;

public class ComponentImplementationCache {
	private class Entry {
		WorkflowBundle implementation;
		long timestamp;
	}
	private final long VALIDITY = 15 * 60 * 1000;
	private final Logger logger = getLogger(ComponentImplementationCache.class);
	private final Map<Version.ID, Entry> cache = new WeakHashMap<>();
	private ComponentUtil utils;

	public void setComponentUtil(ComponentUtil utils) {
		this.utils = utils;
	}

	public WorkflowBundle getImplementation(Version.ID id) throws ComponentException {
		long now = currentTimeMillis();
		synchronized (id) {
			Entry entry = cache.get(id);
			if (entry != null && entry.timestamp >= now)
				return entry.implementation;
			logger.info("before calculate component version for " + id);
			Version componentVersion;
			try {
				componentVersion = utils.getVersion(id);
			} catch (RuntimeException e) {
				if (entry != null)
					return entry.implementation;
				throw new ComponentException(e.getMessage(), e);
			}
			logger.info("calculated component version for " + id + " as "
					+ componentVersion.getVersionNumber() + "; retrieving dataflow");
			WorkflowBundle implementation = componentVersion.getImplementation();
			//DataflowValidationReport report = implementation.checkValidity();
			//logger.info("component version " + id + " incomplete:"
			//		+ report.isWorkflowIncomplete() + " valid:"
			//		+ report.isValid());
			entry = new Entry();
			entry.implementation = implementation;
			entry.timestamp = now + VALIDITY;
			return cache.put(id, entry).implementation;
		}
	}
}
