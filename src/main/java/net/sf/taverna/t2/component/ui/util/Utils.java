/**
 * 
 */
package net.sf.taverna.t2.component.ui.util;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import net.sf.taverna.t2.component.api.ComponentFactory;
import net.sf.taverna.t2.component.api.Version;
import net.sf.taverna.t2.component.preference.ComponentPreference;
import net.sf.taverna.t2.component.ui.serviceprovider.ComponentServiceProvider;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import net.sf.taverna.t2.workbench.file.FileManager;

/**
 * @author alanrw
 *
 *FIXME this class is very very broken!
 */
public class Utils {
	// From http://stackoverflow.com/questions/163360/regular-expresion-to-match-urls-in-java
	public static String URL_PATTERN = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	public static final String LONG_STRING = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
	public static final String SHORT_STRING = "XXX";

	private static FileManager fileManager;//FIXME beaninject
	private static ServiceDescriptionRegistry registry;//FIXME beaninject ServiceDescriptionRegistryImpl
	private static ComponentFactory factory;//FIXME beaninject
	private static ComponentPreference prefs;//FIXME beaninject

	public static void refreshComponentServiceProvider(Configuration config) {
		ComponentServiceProvider provider = new ComponentServiceProvider(
				factory, prefs);
		provider.configure(config);
		registry.removeServiceDescriptionProvider(provider);
		registry.addServiceDescriptionProvider(provider);
	}

	public static void removeComponentServiceProvider(Configuration config) {
		ComponentServiceProvider provider = new ComponentServiceProvider(
				factory, prefs);
		provider.configure(config);
		registry.removeServiceDescriptionProvider(provider);
	}

	public static boolean dataflowIsComponent(WorkflowBundle d) {
		if (d == null)
			return false;
		Object dataflowSource = fileManager.getDataflowSource(d);
		return dataflowSource instanceof Version.ID;// Really?
	}

	public static boolean currentDataflowIsComponent() {
		return dataflowIsComponent(fileManager.getCurrentDataflow());
	}
}
