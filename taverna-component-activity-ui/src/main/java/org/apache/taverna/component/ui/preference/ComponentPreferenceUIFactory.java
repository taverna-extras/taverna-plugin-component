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

package io.github.taverna_extras.component.ui.preference;

import javax.swing.JPanel;
import org.apache.taverna.configuration.Configurable;
import org.apache.taverna.configuration.ConfigurationUIFactory;

/**
 * @author alanrw
 */
public class ComponentPreferenceUIFactory implements ConfigurationUIFactory {
	public static final String DISPLAY_NAME = "Components";

	private JPanel configPanel;//FIXME beaninject
	private ComponentPreference prefs;// FIXME beaninject

	public ComponentPreferenceUIFactory() {
		super();
	}

	public void setConfigPanel(JPanel configPanel) {
		this.configPanel = configPanel;
	}

	public void setPreferences(ComponentPreference pref) {
		this.prefs = pref;
	}

	@Override
	public boolean canHandle(String uuid) {
		return uuid.equals(prefs.getUUID());
	}

	@Override
	public Configurable getConfigurable() {
		return prefs;
	}

	@Override
	public JPanel getConfigurationPanel() {
		return configPanel;
	}
}
