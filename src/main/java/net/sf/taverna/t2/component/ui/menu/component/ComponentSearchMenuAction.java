/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

/**
 * @author alanrw
 */
public class ComponentSearchMenuAction extends AbstractComponentMenuAction {
	private static final URI SEARCH_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentSearch");

	private Action componentSearchAction; //FIXME beaninject ComponentSearchAction

	public ComponentSearchMenuAction() {
		super(1500, SEARCH_COMPONENT_URI);
	}

	@Override
	protected Action createAction() {
		return componentSearchAction;
	}
}
