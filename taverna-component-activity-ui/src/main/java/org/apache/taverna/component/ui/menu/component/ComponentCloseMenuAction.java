/**
 * 
 */
package org.apache.taverna.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;
import org.apache.taverna.component.ui.util.Utils;

import net.sf.taverna.t2.workbench.file.FileManager;

/**
 * @author alanrw
 */
public class ComponentCloseMenuAction extends AbstractComponentMenuAction {
	private static final URI CLOSE_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentClose");

	private Action action;
	private FileManager fm;
	private ComponentServiceIcon icon;
	private Utils utils;

	public ComponentCloseMenuAction() {
		super(1000, CLOSE_COMPONENT_URI);
	}
	
	public void setCloseWorkflowAction(Action action) {
		this.action = action;
	}

	public void setFileManager(FileManager fm) {
		this.fm = fm;
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}

	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	@Override
	protected Action createAction() {
		return new ComponentCloseAction(action, fm, icon, utils);
	}
}
