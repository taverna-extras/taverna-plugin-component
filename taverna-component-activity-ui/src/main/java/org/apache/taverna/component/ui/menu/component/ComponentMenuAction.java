package org.apache.taverna.component.ui.menu.component;

import static org.apache.taverna.component.ui.menu.component.ComponentMenuSection.COMPONENT_SECTION;

import java.net.URI;

import javax.swing.Action;

import net.sf.taverna.t2.ui.menu.AbstractMenuAction;

/**
 * Basis for all menu actions. Intended to be configured by Spring.
 * 
 * @author Donal Fellows
 */
public class ComponentMenuAction extends AbstractMenuAction {
	/**
	 * Construct a menu action to appear within the "Components" menu.
	 * @param positionHint
	 *            Where on the menu this should come.
	 * @param id
	 *            How this should be identified to Taverna.
	 */
	public ComponentMenuAction(int positionHint, String id) {
		super(COMPONENT_SECTION, positionHint, URI.create(id));
	}

	private Action action;

	public void setAction(Action action) {
		this.action = action;
	}

	@Override
	protected Action createAction() {
		return action;
	}
}
