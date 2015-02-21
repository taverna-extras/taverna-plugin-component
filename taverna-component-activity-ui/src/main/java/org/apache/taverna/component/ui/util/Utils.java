/**
 * 
 */
package org.apache.taverna.component.ui.util;

import static org.apache.taverna.component.ui.ComponentConstants.ACTIVITY_URI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.taverna.component.api.ComponentFactory;
import org.apache.taverna.component.api.Version;
import org.apache.taverna.component.ui.preference.ComponentPreference;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceProvider;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Named;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import net.sf.taverna.t2.workbench.file.FileManager;

/**
 * @author alanrw
 */
public class Utils {
	// From http://stackoverflow.com/questions/163360/regular-expresion-to-match-urls-in-java
	public static String URL_PATTERN = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	public static final String LONG_STRING = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
	public static final String SHORT_STRING = "XXX";

	private FileManager fileManager;
	private ServiceDescriptionRegistry registry;
	private ComponentFactory factory;
	private ComponentPreference prefs;
	private ComponentServiceIcon icon;

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
	}
	
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}
	
	public void setPrefs(ComponentPreference prefs) {
		this.prefs = prefs;
	}
	
	public void setRegistry(ServiceDescriptionRegistry registry) {
		this.registry = registry;
	}

	public void refreshComponentServiceProvider(Configuration config) {
		ComponentServiceProvider provider = new ComponentServiceProvider(
				factory, prefs, icon, this);
		provider.configure(config);
		registry.removeServiceDescriptionProvider(provider);
		registry.addServiceDescriptionProvider(provider);
	}

	public void removeComponentServiceProvider(Configuration config) {
		ComponentServiceProvider provider = new ComponentServiceProvider(
				factory, prefs, icon, this);
		provider.configure(config);
		registry.removeServiceDescriptionProvider(provider);
	}

	public boolean dataflowIsComponent(WorkflowBundle d) {
		if (d == null)
			return false;
		Object dataflowSource = fileManager.getDataflowSource(d);
		return dataflowSource instanceof Version.ID;// FIXME Really?
	}

	public boolean currentDataflowIsComponent() {
		return dataflowIsComponent(fileManager.getCurrentDataflow());
	}

	public static boolean isComponentActivity(Object obj) {
		if (obj == null || !(obj instanceof Activity))
			return false;
		Configuration cfg = ((Activity) obj).getConfiguration();
		return cfg != null && ACTIVITY_URI.equals(cfg.getType());
	}

	private static final Pattern SANITIZER_RE = Pattern
			.compile("[^a-zA-Z0-9]+");
	private static final Pattern SUFFIXED_RE = Pattern
			.compile("^(.+)_([0-9]+)$");

	/**
	 * Pick a name that is unique within the context set. This is done by
	 * appending "<tt>_<i>n</i></tt>" as necessary, where <tt><i>n</i></tt> is a
	 * number.
	 * 
	 * @param name
	 *            The suggested name; this is always checked first.
	 * @param context
	 *            The set of things that the name will have to be unique within.
	 * @return A name that is definitely not the name of anything in the given
	 *         set.
	 */
	public static String uniqueName(String name, NamedSet<? extends Named> context) {
		String candidate = SANITIZER_RE.matcher(name).replaceAll("_");
		if (context.getByName(candidate) == null)
			return candidate;
		int counter = 0;
		String prefix = candidate;
		Matcher m = SUFFIXED_RE.matcher(candidate);
		if (m.matches()) {
			prefix = m.group(1);
			counter = Integer.parseInt(m.group(2));
		}
		do {
			candidate = prefix + "_" + (++counter);
		} while (context.getByName(candidate) != null);
		return candidate;
	}
}
