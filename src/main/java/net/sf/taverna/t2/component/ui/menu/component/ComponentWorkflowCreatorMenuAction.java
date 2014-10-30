/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

import net.sf.taverna.t2.workbench.file.FileManager;

/**
 * @author alanrw
 */
public class ComponentWorkflowCreatorMenuAction extends AbstractComponentMenuAction {
	private static final URI COMPONENT_WORKFLOW_CREATE_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentCreate");

	private ComponentCreatorSupport support;
	private FileManager fm;

	public ComponentWorkflowCreatorMenuAction() {
		super(600, COMPONENT_WORKFLOW_CREATE_URI);
	}

	public void setSupport(ComponentCreatorSupport support) {
		this.support = support;
	}

	public void setFileManager(FileManager fm) {
		this.fm = fm;
	}

	@Override
	protected Action createAction() {
		return new ComponentWorkflowCreatorAction(support, fm);
	}
}
