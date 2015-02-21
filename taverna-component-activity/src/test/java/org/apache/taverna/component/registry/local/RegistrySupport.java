package org.apache.taverna.component.registry.local;

import static java.lang.System.getProperty;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.taverna.component.registry.Harness.componentRegistry;
import static org.apache.taverna.component.registry.Harness.componentRegistryUrl;

import java.io.File;

import org.apache.taverna.component.registry.local.LocalComponentRegistryFactory;

class RegistrySupport {
	private static File testRegistry;
	final static LocalComponentRegistryFactory factory = new LocalComponentRegistryFactory();

	public static void pre() throws Exception {
		testRegistry = new File(getProperty("java.io.tmpdir"), "TestRegistry");
		testRegistry.mkdir();
		componentRegistryUrl = testRegistry.toURI().toURL();
		componentRegistry = factory.getComponentRegistry(componentRegistryUrl);
	}

	public static void post() throws Exception {
		deleteDirectory(testRegistry);
	}
}
