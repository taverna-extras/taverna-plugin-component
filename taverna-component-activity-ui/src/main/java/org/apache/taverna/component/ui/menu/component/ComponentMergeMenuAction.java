/**
 * 
 */
package org.apache.taverna.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

import org.apache.taverna.component.ui.preference.ComponentPreference;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;

/**
 * @author alanrw
 */
public class ComponentMergeMenuAction extends AbstractComponentMenuAction {
	private static final URI MERGE_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentMerge");

	private ComponentServiceIcon icon;
	private ComponentPreference prefs;

	public ComponentMergeMenuAction() {
		super(900, MERGE_COMPONENT_URI);
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}

	public void setPreferences(ComponentPreference prefs) {
		this.prefs = prefs;
	}

	@Override
	protected Action createAction() {
		return new ComponentMergeAction(prefs, icon);
	}
}
