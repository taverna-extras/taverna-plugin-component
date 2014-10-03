/**
 * 
 */
package net.sf.taverna.t2.component.ui.preference;

import javax.swing.JPanel;

import uk.org.taverna.configuration.Configurable;
import uk.org.taverna.configuration.ConfigurationUIFactory;
import net.sf.taverna.t2.component.preference.ComponentPreference;

/**
 * @author alanrw
 */
public class ComponentPreferenceUIFactory implements ConfigurationUIFactory {
	public static final String DISPLAY_NAME = "Components";
	private final JPanel configPanel;
	private static ComponentPreference pref;// FIXME beaninject

	public ComponentPreferenceUIFactory() {
		super();
		configPanel = new ComponentPreferencePanel();
	}

	@Override
	public boolean canHandle(String uuid) {
		return uuid.equals(pref.getUUID());
	}

	@Override
	public Configurable getConfigurable() {
		return pref;
	}

	@Override
	public JPanel getConfigurationPanel() {
		return configPanel;
	}
}
