/**
 * 
 */
package org.apache.taverna.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

import org.apache.taverna.component.api.ComponentFactory;
import org.apache.taverna.component.ui.preference.ComponentPreference;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;

import uk.org.taverna.commons.services.ServiceRegistry;
import net.sf.taverna.t2.ui.menu.MenuManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.selection.SelectionManager;

/**
 * @author alanrw
 */
public class ComponentSearchMenuAction extends AbstractComponentMenuAction {
	private static final URI SEARCH_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentSearch");
	private ComponentPreference prefs;
	private ComponentFactory factory;
	private EditManager em;
	private MenuManager mm;
	private SelectionManager sm;
	private ServiceRegistry serviceRegistry;
	private ComponentServiceIcon icon;

	public ComponentSearchMenuAction() {
		super(1500, SEARCH_COMPONENT_URI);
	}

	public void setPreferences(ComponentPreference prefs) {
		this.prefs = prefs;
	}

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
	}

	public void setEditManager(EditManager em) {
		this.em = em;
	}

	public void setMenuManager(MenuManager mm) {
		this.mm = mm;
	}

	public void setSelectionManager(SelectionManager sm) {
		this.sm = sm;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}

	@Override
	protected Action createAction() {
		return new ComponentSearchAction(prefs, factory, em, mm, sm,
				serviceRegistry, icon);
	}
}
