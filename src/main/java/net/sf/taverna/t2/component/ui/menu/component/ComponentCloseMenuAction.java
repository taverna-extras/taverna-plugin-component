/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

/**
 * @author alanrw
 */
public class ComponentCloseMenuAction extends AbstractComponentMenuAction {
	private static final URI CLOSE_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentClose");

	private Action action; //FIXME beaninject ComponentCloseAction

	public ComponentCloseMenuAction() {
		super(1000, CLOSE_COMPONENT_URI);
	}

	@Override
	protected Action createAction() {
		return action;
	}
}
