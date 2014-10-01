package net.sf.taverna.t2.component.preference;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import uk.org.taverna.configuration.app.ApplicationConfiguration;

/**
 * Factored out defaults location system.
 * 
 * @author Donal Fellows
 */
public class ComponentDefaults {
    public static final String REGISTRY_LIST = "REGISTRY_NAMES";
    private static final String LOCAL_NAME = "local registry";
    private static final String MYEXPERIMENT_NAME = "myExperiment";
    private static final String MYEXPERIMENT_SITE = "http://www.myexperiment.org";
    public static final String DEFAULT_REGISTRY_LIST = LOCAL_NAME + "," + MYEXPERIMENT_NAME;

    public static Map<String, String> getDefaultProperties() {
    	// Capacity = 3; we know that this is going to have 3 entries
    	Map<String, String> defaults = new LinkedHashMap<>(3);
    	defaults.put(LOCAL_NAME, calculateComponentsDirectoryPath());
    	defaults.put(MYEXPERIMENT_NAME, MYEXPERIMENT_SITE);
    	defaults.put(REGISTRY_LIST, DEFAULT_REGISTRY_LIST);
    	return defaults;
    }

    static ApplicationConfiguration config;//FIXME beaninject (and beanify!)

	public static String calculateComponentsDirectoryPath() {
		return new File(config.getApplicationHomeDir(), "components").toURI()
				.toASCIIString();
	}

	private ComponentDefaults() {
	}
}
