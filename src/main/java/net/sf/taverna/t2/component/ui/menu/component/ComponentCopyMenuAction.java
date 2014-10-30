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
public class ComponentCopyMenuAction extends AbstractComponentMenuAction {
	private static final URI COPY_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentCopy");

	private Action action;
	private ComponentPreference prefs;

	public ComponentCopyMenuAction() {
		super(800, COPY_COMPONENT_URI);
	}

	public void setPreferences(ComponentPreference prefs) {//FIXME beaninject
		this.prefs = prefs;
	}

	@Override
	protected Action createAction() {
		if (action == null)
			action = new ComponentCopyAction(prefs);
		return action;
	}
}
