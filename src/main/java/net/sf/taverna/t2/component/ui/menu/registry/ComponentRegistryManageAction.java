/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.registry;

import static net.sf.taverna.t2.component.ui.preference.ComponentPreferenceUIFactory.DISPLAY_NAME;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.sf.taverna.t2.component.ui.serviceprovider.ComponentServiceIcon;
import net.sf.taverna.t2.workbench.configuration.workbench.ui.T2ConfigurationFrame;

/**
 * @author alanrw
 */
public class ComponentRegistryManageAction extends AbstractAction {
	private static final long serialVersionUID = 8993945811345164194L;
	private static final String MANAGE_REGISTRY = "Manage registries...";

	private final T2ConfigurationFrame configFrame;

	public ComponentRegistryManageAction(T2ConfigurationFrame configFrame,
			ComponentServiceIcon icon) {
		super(MANAGE_REGISTRY, icon.getIcon());
		this.configFrame = configFrame;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		configFrame.showConfiguration(DISPLAY_NAME);
	}
}
