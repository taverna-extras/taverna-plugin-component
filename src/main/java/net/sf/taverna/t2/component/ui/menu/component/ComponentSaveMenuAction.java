/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

/**
 * @author alanrw
 * 
 */
public class ComponentSaveMenuAction extends AbstractComponentMenuAction {
	private static final URI SAVE_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentSave");

	private Action action;//FIXME beaninject ComponentSaveAction

	public ComponentSaveMenuAction() {
		super(1100, SAVE_COMPONENT_URI);
	}

	@Override
	protected Action createAction() {
		return action;
	}
}
