/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu;

import java.net.URI;

import javax.swing.Action;

import org.apache.taverna.component.api.ComponentFactory;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.core.Processor;
import net.sf.taverna.t2.component.ui.serviceprovider.ComponentServiceIcon;
import net.sf.taverna.t2.ui.menu.AbstractContextualMenuAction;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.file.FileType;
import net.sf.taverna.t2.workbench.selection.SelectionManager;
import net.sf.taverna.t2.workbench.views.graph.GraphViewComponent;

/**
 * @author alanrw
 */
public class OpenComponentFromComponentActivityMenuAction extends
		AbstractContextualMenuAction {
	private static final URI configureSection = URI
			.create("http://taverna.sf.net/2009/contextMenu/configure");

	private SelectionManager sm;
	private FileManager fileManager;
	private ComponentFactory factory;
	private FileType fileType;
	private GraphViewComponent graphView;
	private ComponentServiceIcon icon;

	public OpenComponentFromComponentActivityMenuAction() {
		super(configureSection, 75);
	}

	public void setSelectionManager(SelectionManager sm) {
		this.sm = sm;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	public void setGraphView(GraphViewComponent graphView) {
		this.graphView = graphView;
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}

	@Override
	public boolean isEnabled() {
		return getSelectedActivity() != null;
	}

	@Override
	protected Action createAction() {
		OpenComponentFromComponentActivityAction action = new OpenComponentFromComponentActivityAction(
				fileManager, factory, fileType, graphView, icon);
		action.setSelection(getSelectedActivity());
		return action;
	}

	private Activity getSelectedActivity() {
		Object selection = getContextualSelection().getSelection();
		if (!super.isEnabled() || !(selection instanceof Processor))
			return null;

		try {
			return ((Processor) selection).getActivity(sm.getSelectedProfile());
		} catch (RuntimeException e) {
			return null;
		}
	}
}
