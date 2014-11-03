/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.profile;

import static net.sf.taverna.t2.component.ui.menu.profile.ComponentProfileMenuSection.COMPONENT_PROFILE_SECTION;

import java.net.URI;

import javax.swing.Action;

import net.sf.taverna.t2.component.preference.ComponentPreference;
import net.sf.taverna.t2.component.ui.serviceprovider.ComponentServiceIcon;
import net.sf.taverna.t2.ui.menu.AbstractMenuAction;

/**
 * @author alanrw
 */
public class ComponentProfileDeleteMenuAction extends AbstractMenuAction {
	private static final URI COMPONENT_PROFILE_DELETE_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentProfileDelete");

	private ComponentPreference prefs;
	private ComponentServiceIcon icon;

	public ComponentProfileDeleteMenuAction() {
		super(COMPONENT_PROFILE_SECTION, 300, COMPONENT_PROFILE_DELETE_URI);
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}

	public void setPreferences(ComponentPreference prefs) {
		this.prefs = prefs;
	}

	@Override
	protected Action createAction() {
		return new ComponentProfileDeleteAction(prefs, icon);
	}
}
