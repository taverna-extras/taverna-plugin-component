/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

/**
 * @author alanrw
 */
public class FileOpenFromComponentMenuAction extends AbstractComponentMenuAction {
	private static final URI FILE_OPEN_FROM_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentOpen");

	public FileOpenFromComponentMenuAction() {
		super(700, FILE_OPEN_FROM_COMPONENT_URI);
	}

	@Override
	protected Action createAction() {
		return new OpenWorkflowFromComponentAction(null);
	}
}
