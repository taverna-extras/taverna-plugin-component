/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

/**
 * @author alanrw
 */
public class ComponentMergeMenuAction extends AbstractComponentMenuAction {
	private static final URI MERGE_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentMerge");

	private Action action;//FIXME beaninject ComponentMergeAction

	public ComponentMergeMenuAction() {
		super(900, MERGE_COMPONENT_URI);
	}

	@Override
	protected Action createAction() {
		return action;
	}
}
