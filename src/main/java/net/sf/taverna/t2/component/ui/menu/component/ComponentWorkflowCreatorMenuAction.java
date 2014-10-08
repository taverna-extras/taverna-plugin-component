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
public class ComponentWorkflowCreatorMenuAction extends AbstractComponentMenuAction {
	private static final URI COMPONENT_WORKFLOW_CREATE_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentCreate");

	private Action action; // FIXME beaninject ComponentWorkflowCreatorAction

	public ComponentWorkflowCreatorMenuAction() {
		super(600, COMPONENT_WORKFLOW_CREATE_URI);
	}

	@Override
	protected Action createAction() {
		return action;
	}
}
