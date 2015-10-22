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

package org.apache.taverna.component.ui.serviceprovider;

import static org.apache.taverna.component.ui.serviceprovider.Service.COMPONENT_ACTIVITY_URI;

import java.net.URI;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.apache.taverna.workbench.activityicons.ActivityIconSPI;

public class ComponentServiceIcon implements ActivityIconSPI {
	private static class Init {
		private static Icon icon = new ImageIcon(
				ComponentServiceIcon.class.getResource("/brick.png"));
	}

	@Override
	public int canProvideIconScore(URI activityType) {
		if (activityType.equals(COMPONENT_ACTIVITY_URI))
			return DEFAULT_ICON + 1;
		return NO_ICON;
	}

	@Override
	public Icon getIcon(URI activityType) {
		return Init.icon;
	}

	public Icon getIcon() {
		return Init.icon;
	}
}
