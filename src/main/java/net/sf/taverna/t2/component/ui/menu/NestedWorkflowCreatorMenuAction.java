/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.Action;

import net.sf.taverna.t2.activities.dataflow.servicedescriptions.DataflowActivityIcon;
import net.sf.taverna.t2.ui.menu.AbstractContextualMenuAction;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.selection.SelectionManager;
import net.sf.taverna.t2.workbench.views.graph.GraphViewComponent;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;

/**
 * @author alanrw
 */
public class NestedWorkflowCreatorMenuAction extends
		AbstractContextualMenuAction {
	private static final URI configureSection = URI
			.create("http://taverna.sf.net/2009/contextMenu/configure");

	private SelectionManager sm;
	private EditManager em;
	private GraphViewComponent gv;

	public NestedWorkflowCreatorMenuAction() {
		super(configureSection, 70);
	}

	public void setEditManager(EditManager editManager) {
		em = editManager;
	}
	public void setGraphView(GraphViewComponent graphView) {
		gv = graphView;
	}
	public void setSelectionManager(SelectionManager selectionManager) {
		sm = selectionManager;
	}

	@Override
	public boolean isEnabled() {
		Object selection = getContextualSelection().getSelection();
		if (!super.isEnabled() || selection == null)
			return false;
		if (selection instanceof Processor)
			return true;
		if (!(selection instanceof Workflow))
			return false;
		return !((Workflow) selection).getProcessors().isEmpty();
	}

	@Override
	protected Action createAction() {
		return new AbstractAction("Create nested workflow...",
				DataflowActivityIcon.getDataflowIcon()) {
			private static final long serialVersionUID = -3121307982540205215L;

			@Override
			public void actionPerformed(ActionEvent e) {
				createNestedWorkflow();
			}
		};
	}

	private void createNestedWorkflow() {
		Dialog dialog = new NestedWorkflowCreationDialog(null,
				getContextualSelection().getSelection(),
				sm.getSelectedWorkflow(), em, gv);
		dialog.setVisible(true);
	}
}
