package io.github.taverna_extras.component.api.profile;
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author alanrw
 * 
 */
public class ExceptionHandling {
	private final boolean failLists;
	private final List<HandleException> remapped = new ArrayList<HandleException>();

	public ExceptionHandling(
			io.github.taverna_extras.component.api.profile.doc.ExceptionHandling proxied) {
		for (io.github.taverna_extras.component.api.profile.doc.HandleException he : proxied
				.getHandleException())
			remapped.add(new HandleException(he));
		this.failLists = proxied.getFailLists() != null;
	}

	public boolean failLists() {
		return failLists;
	}

	public List<HandleException> getHandleExceptions() {
		return remapped;
	}
}
