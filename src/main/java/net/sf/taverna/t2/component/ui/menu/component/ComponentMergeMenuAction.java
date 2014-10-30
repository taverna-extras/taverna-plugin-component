/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

import net.sf.taverna.t2.component.preference.ComponentPreference;

/**
 * @author alanrw
 */
public class ComponentMergeMenuAction extends AbstractComponentMenuAction {
	private static final URI MERGE_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentMerge");

	private ComponentPreference prefs;

	public ComponentMergeMenuAction() {
		super(900, MERGE_COMPONENT_URI);
	}

	public void setPreferences(ComponentPreference prefs) {
		this.prefs = prefs;
	}

	@Override
	protected Action createAction() {
		return new ComponentMergeAction(prefs);
	}
}
