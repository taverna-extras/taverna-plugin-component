/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.registry;

import static net.sf.taverna.t2.component.ui.preference.ComponentPreferenceUIFactory.DISPLAY_NAME;
import static net.sf.taverna.t2.component.ui.serviceprovider.ComponentServiceIcon.getIcon;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.sf.taverna.t2.workbench.configuration.workbench.ui.T2ConfigurationFrame;

/**
 * @author alanrw
 */
public class ComponentRegistryManageAction extends AbstractAction {
	private static final long serialVersionUID = 8993945811345164194L;
	private static final String MANAGE_REGISTRY = "Manage registries...";

	private T2ConfigurationFrame configFrame;//FIXME beaninject

	public ComponentRegistryManageAction() {
		super(MANAGE_REGISTRY, getIcon());
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		configFrame.showConfiguration(DISPLAY_NAME);
	}
}
