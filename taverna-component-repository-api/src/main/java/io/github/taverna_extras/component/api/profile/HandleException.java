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


import static java.util.regex.Pattern.DOTALL;
import static java.util.regex.Pattern.compile;

import java.util.regex.Pattern;

/**
 * @author alanrw
 * 
 */
public class HandleException {
	private final Pattern pattern;
	private ExceptionReplacement replacement;
	private final boolean pruneStack;

	public HandleException(
			io.github.taverna_extras.component.api.profile.doc.HandleException proxied) {
		pruneStack = proxied.getPruneStack() != null;
		pattern = compile(proxied.getPattern(), DOTALL);
		if (proxied.getReplacement() != null)
			replacement = new ExceptionReplacement(proxied.getReplacement());
	}

	public boolean matches(String s) {
		return pattern.matcher(s).matches();
	}

	public boolean pruneStack() {
		return pruneStack;
	}

	public ExceptionReplacement getReplacement() {
		return replacement;
	}
}
