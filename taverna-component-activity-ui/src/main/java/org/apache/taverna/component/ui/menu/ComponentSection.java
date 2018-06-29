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

package io.github.taverna_extras.component.ui.menu;

import java.net.URI;
import org.apache.taverna.ui.menu.AbstractMenuSection;

/**
 * @author alanrw
 * 
 */
public class ComponentSection extends AbstractMenuSection {
	public static final String COMPONENT_SECTION = "Components";
	public static final URI componentSection = URI
			.create("http://taverna.sf.net/2009/contextMenu/components");
	public static final URI editSection = URI
			.create("http://taverna.sf.net/2009/contextMenu/edit");

	public ComponentSection() {
		super(editSection, 100, componentSection);
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled();
	}
}
