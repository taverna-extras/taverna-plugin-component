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

package io.github.taverna_extras.component.ui.panel;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import io.github.taverna_extras.component.api.Component;
import io.github.taverna_extras.component.api.Family;
import io.github.taverna_extras.component.api.Registry;
import io.github.taverna_extras.component.api.Version;

/**
 * @author alanrw
 */
public class ComponentListCellRenderer<T> implements ListCellRenderer<T> {
	private static DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

	@Override
	public java.awt.Component getListCellRendererComponent(
			JList<? extends T> list, T value, int index, boolean isSelected,
			boolean cellHasFocus) {
		return defaultRenderer.getListCellRendererComponent(list,
				convertValueToString(value), index, isSelected, cellHasFocus);
	}

	private static String convertValueToString(Object value) {
		if (value instanceof Registry)
			return ((Registry) value).getRegistryBase().toString();
		if (value instanceof Family)
			return ((Family) value).getName();
		if (value instanceof Component)
			return ((Component) value).getName();
		if (value instanceof Version)
			return ((Version) value).getVersionNumber().toString();
		if (value instanceof Integer)
			return ((Integer) value).toString();
		if (value instanceof String)
			return (String) value;
		if (value == null)
			return "null";
		return "Spaceholder for " + value.getClass().getName();
	}
}
