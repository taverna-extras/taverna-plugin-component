package net.sf.taverna.t2.component.ui.config;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import net.sf.taverna.t2.component.ComponentActivity;
import net.sf.taverna.t2.component.ComponentActivityConfigurationBean;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.ui.actions.activity.ActivityConfigurationAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;
import net.sf.taverna.t2.workflowmodel.Edits;

@SuppressWarnings("serial")
public class ComponentConfigureAction extends ActivityConfigurationAction {
	EditManager edits;//FIXME beaninject

	public ComponentConfigureAction(ComponentActivity activity, Frame owner) {
		super(activity);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ActivityConfigurationDialog currentDialog = getDialog(getActivity());
		if (currentDialog != null) {
			currentDialog.toFront();
			return;
		}
		ActivityConfigurationDialog dialog = new ActivityConfigurationDialog(
				getActivity(), new ComponentConfigurationPanel(getActivity()), edits);
		setDialog(getActivity(), dialog);
	}

}
