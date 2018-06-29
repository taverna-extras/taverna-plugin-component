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

import static java.lang.String.format;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.table.DefaultTableModel;

import io.github.taverna_extras.component.api.Registry;

/**
 * @author alanrw
 * 
 */
public class RegistryTableModel extends DefaultTableModel {
	private static final long serialVersionUID = -7789666945764974370L;
	private SortedMap<String, Registry> registryMap = new TreeMap<String, Registry>();

	public RegistryTableModel() {
		super(new String[] { "Registry name", "Registry location" }, 0);
	}

	public void setRegistryMap(SortedMap<String, Registry> registries) {
		registryMap.clear();
		registryMap.putAll(registries);
		updateRows();
	}

	public void updateRows() {
		super.setRowCount(0);
		for (Entry<String, Registry> entry : registryMap.entrySet())
			super.addRow(new Object[] { entry.getKey(),
					entry.getValue().getRegistryBaseString() });
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
    public String getRowTooltipText(int row) {
        Registry registry = registryMap.get(getValueAt(row, 0));
        return format("This is a %s registry.", registry.getRegistryTypeName());
    }

	@Override
	public void removeRow(int row) {
		String key = (String) getValueAt(row, 0);
		registryMap.remove(key);
		super.removeRow(row);
	}

	public void insertRegistry(String name, Registry newRegistry) {
		registryMap.put(name, newRegistry);
		updateRows();
	}

	/**
	 * @return the registryMap
	 */
	public SortedMap<String, Registry> getRegistryMap() {
		return registryMap;
	}

}
