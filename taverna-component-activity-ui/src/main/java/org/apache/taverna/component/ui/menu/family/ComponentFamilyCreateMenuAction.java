/**
 * 
 */
package org.apache.taverna.component.ui.menu.family;

import static org.apache.taverna.component.ui.menu.family.ComponentFamilyMenuSection.COMPONENT_FAMILY_SECTION;

import java.net.URI;

import javax.swing.Action;

import org.apache.taverna.component.ui.preference.ComponentPreference;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;

import net.sf.taverna.t2.ui.menu.AbstractMenuAction;

/**
 * @author alanrw
 */
public class ComponentFamilyCreateMenuAction extends AbstractMenuAction {
	private static final URI COMPONENT_FAMILY_CREATE_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentFamilyCreate");

	private ComponentPreference prefs;
	private ComponentServiceIcon iconProvider;

	public ComponentFamilyCreateMenuAction() {
		super(COMPONENT_FAMILY_SECTION, 400, COMPONENT_FAMILY_CREATE_URI);
	}

	public void setPreferences(ComponentPreference prefs) {
		this.prefs = prefs;
	}

	public void setIcon(ComponentServiceIcon iconProvider) {
		this.iconProvider = iconProvider;
	}

	@Override
	protected Action createAction() {
		return new ComponentFamilyCreateAction(prefs, iconProvider);
	}
}
