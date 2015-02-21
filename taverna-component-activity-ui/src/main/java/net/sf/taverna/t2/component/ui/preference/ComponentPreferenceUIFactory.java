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
