/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

import net.sf.taverna.t2.component.ui.serviceprovider.ComponentServiceIcon;
import net.sf.taverna.t2.component.ui.util.Utils;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.views.graph.GraphViewComponent;

/**
 * @author alanrw
 */
public class ComponentWorkflowCreatorMenuAction extends AbstractComponentMenuAction {
	private static final URI COMPONENT_WORKFLOW_CREATE_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentCreate");

	private ComponentCreatorSupport support;
	private FileManager fm;
	private GraphViewComponent graphView;
	private ComponentServiceIcon icon;
	private Utils utils;

	public ComponentWorkflowCreatorMenuAction() {
		super(600, COMPONENT_WORKFLOW_CREATE_URI);
	}

	public void setSupport(ComponentCreatorSupport support) {
		this.support = support;
	}

	public void setFileManager(FileManager fm) {
		this.fm = fm;
	}

	public void setGraphView(GraphViewComponent graphView) {
		this.graphView = graphView;
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}

	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	@Override
	protected Action createAction() {
		return new ComponentWorkflowCreatorAction(support, fm, graphView, icon, utils);
	}
}
