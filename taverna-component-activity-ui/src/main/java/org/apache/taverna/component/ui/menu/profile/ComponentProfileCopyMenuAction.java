/**
 * 
 */
package org.apache.taverna.component.ui.menu.profile;

import static org.apache.taverna.component.ui.menu.profile.ComponentProfileMenuSection.COMPONENT_PROFILE_SECTION;

import java.net.URI;

import javax.swing.Action;

import org.apache.taverna.component.ui.preference.ComponentPreference;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;

import net.sf.taverna.t2.ui.menu.AbstractMenuAction;

/**
 * @author alanrw
 */
public class ComponentProfileCopyMenuAction extends AbstractMenuAction {
	private static final URI COMPONENT_PROFILE_COPY_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentProfileCopy");

	private ComponentPreference prefs;
	private ComponentServiceIcon icon;

	public ComponentProfileCopyMenuAction() {
		super(COMPONENT_PROFILE_SECTION, 250, COMPONENT_PROFILE_COPY_URI);
	}

	public void setPreferences(ComponentPreference prefs) {
		this.prefs = prefs;
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}

	@Override
	protected Action createAction() {
		return new ComponentProfileCopyAction(prefs, icon);
	}
}
