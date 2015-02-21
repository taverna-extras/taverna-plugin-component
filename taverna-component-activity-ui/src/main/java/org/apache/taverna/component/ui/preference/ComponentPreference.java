/**
 * 
 */
package org.apache.taverna.component.ui.preference;

import static org.apache.commons.lang.StringUtils.join;
import static org.apache.log4j.Logger.getLogger;
import static org.apache.taverna.component.ui.preference.ComponentDefaults.REGISTRY_LIST;
import static org.apache.taverna.component.ui.preference.ComponentDefaults.getDefaultProperties;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.ComponentFactory;
import org.apache.taverna.component.api.Registry;

import uk.org.taverna.configuration.AbstractConfigurable;
import uk.org.taverna.configuration.ConfigurationManager;

/**
 * @author alanrw
 */
public class ComponentPreference extends AbstractConfigurable {
	public static final String DISPLAY_NAME = "Components";
	private final Logger logger = getLogger(ComponentPreference.class);

	private SortedMap<String, Registry> registryMap = new TreeMap<>();
	private ComponentFactory factory;

	public ComponentPreference(ConfigurationManager cm, ComponentFactory factory) {
		super(cm);
		this.factory = factory;
		updateRegistryMap();
	}

	private void updateRegistryMap() {
		registryMap.clear();

		for (String key : getRegistryKeys()) {
			String value = super.getProperty(key);
			try {
				registryMap.put(key, factory.getRegistry(new URL(
						value)));
			} catch (MalformedURLException e) {
				logger.error("bogus url (" + value
						+ ") in configuration file", e);
			} catch (ComponentException e) {
				logger.error("failed to construct registry handle for "
						+ value, e);
			}
		}
	}
	
	private String[] getRegistryKeys() {
		String registryNamesConcatenated = super.getProperty(REGISTRY_LIST);
		if (registryNamesConcatenated == null)
			return getDefaultPropertyMap().keySet().toArray(new String[]{});
		return registryNamesConcatenated.split(",");
	}

	@Override
	public String getFilePrefix() {
		return "Component";
	}

	@Override
	public String getUUID() {
		return "2317A297-2AE0-42B5-86DC-99C9B7C0524A";
	}

	/**
	 * @return the registryMap
	 */
	public SortedMap<String, Registry> getRegistryMap() {
		return registryMap;
	}

	public String getRegistryName(URL registryBase) {
		// Trim trailing '/' characters to ensure match.
		String base = registryBase.toString();
		while (base.endsWith("/"))
			base = base.substring(0, base.length() - 1);

		for (Entry<String, Registry> entry : registryMap.entrySet())
			if (entry.getValue().getRegistryBaseString().equals(base))
				return entry.getKey();
		return base;
	}

	public void setRegistryMap(SortedMap<String, Registry> registries) {
		registryMap.clear();
		registryMap.putAll(registries);
		super.clear();
		List<String> keyList = new ArrayList<>();
		for (Entry<String, Registry> entry : registryMap.entrySet()) {
			String key = entry.getKey();
			keyList.add(key);
			super.setProperty(key, entry.getValue().getRegistryBaseString());
		}
		Collections.sort(keyList);
		String registryNamesConcatenated = join(keyList, ",");
		super.setProperty(REGISTRY_LIST, registryNamesConcatenated);
	}

	@Override
	public Map<String, String> getDefaultPropertyMap() {
		return getDefaultProperties();
	}

	@Override
	public String getDisplayName() {
		return DISPLAY_NAME;
	}

	@Override
	public String getCategory() {
		return "general";
	}
}
