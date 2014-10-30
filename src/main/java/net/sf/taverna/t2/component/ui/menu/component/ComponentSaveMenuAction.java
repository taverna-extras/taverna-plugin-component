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
public class ComponentSaveMenuAction extends AbstractComponentMenuAction {
	private static final URI SAVE_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentSave");

	private Action action;
	private FileManager fm;

	public ComponentSaveMenuAction() {
		super(1100, SAVE_COMPONENT_URI);
	}

	//FIXME beaninject net.sf.taverna.t2.workbench.file.impl.actions.SaveWorkflowAction
	public void setSaveWorkflowAction(Action action) {
		this.action = action;
	}

	public void setFileManager(FileManager fm) {
		this.fm = fm;
	}

	@Override
	protected Action createAction() {
		return new ComponentSaveAction(action, fm);
	}
}
