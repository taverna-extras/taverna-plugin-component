/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

/**
 * @author alanrw
 */
public class ComponentCopyMenuAction extends AbstractComponentMenuAction {
	private static final URI COPY_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentCopy");

	private Action action;//FIXME beaninject ComponentCopyAction

	public ComponentCopyMenuAction() {
		super(800, COPY_COMPONENT_URI);
	}

	@Override
	protected Action createAction() {
		return action;
	}
}
