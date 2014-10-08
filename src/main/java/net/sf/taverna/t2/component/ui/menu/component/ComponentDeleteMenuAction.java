/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

/**
 * @author alanrw
 */
public class ComponentDeleteMenuAction extends AbstractComponentMenuAction {
	private static final URI DELETE_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentDelete");

	private Action action;//FIXME beaninject ComponentDeleteAction

	public ComponentDeleteMenuAction() {
		super(1200, DELETE_COMPONENT_URI);
	}

	@Override
	protected Action createAction() {
		return action;
	}
}
