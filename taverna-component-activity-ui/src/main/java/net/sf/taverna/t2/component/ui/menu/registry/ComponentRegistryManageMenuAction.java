/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.registry;

import static net.sf.taverna.t2.component.ui.menu.registry.ComponentRegistryMenuSection.COMPONENT_REGISTRY_SECTION;

import java.net.URI;

import javax.swing.Action;

import net.sf.taverna.t2.component.ui.serviceprovider.ComponentServiceIcon;
import net.sf.taverna.t2.ui.menu.AbstractMenuAction;
import net.sf.taverna.t2.workbench.configuration.workbench.ui.T2ConfigurationFrame;

/**
 * @author alanrw
 */
public class ComponentRegistryManageMenuAction extends AbstractMenuAction {
	private static final URI COMPONENT_REGISTRY_MANAGE_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentRegistryManage");

	private T2ConfigurationFrame configFrame;
	private ComponentServiceIcon icon;

	public ComponentRegistryManageMenuAction() {
		super(COMPONENT_REGISTRY_SECTION, 100, COMPONENT_REGISTRY_MANAGE_URI);
	}

	public void setConfigurationFrame(T2ConfigurationFrame configFrame) {
		this.configFrame = configFrame;
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	protected Action createAction() {
		return new ComponentRegistryManageAction(configFrame, icon);
	}
}
