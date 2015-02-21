package org.apache.taverna.component.ui.config;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import org.apache.taverna.component.api.ComponentFactory;

import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.actions.activity.ActivityConfigurationAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;
import uk.org.taverna.commons.services.ServiceRegistry;
import org.apache.taverna.scufl2.api.activity.Activity;

@SuppressWarnings("serial")
public class ComponentConfigureAction extends ActivityConfigurationAction {
	private EditManager editManager;
	private FileManager fileManager;
	private ServiceRegistry serviceRegistry;
	private ComponentFactory factory;

	public ComponentConfigureAction(Activity activity, Frame owner,
			ComponentFactory factory, ActivityIconManager activityIconManager,
			ServiceDescriptionRegistry serviceDescriptionRegistry,
			EditManager editManager, FileManager fileManager,
			ServiceRegistry serviceRegistry) {
		super(activity, activityIconManager, serviceDescriptionRegistry);
		this.editManager = editManager;
		this.fileManager = fileManager;
		this.serviceRegistry = serviceRegistry;
		this.factory = factory;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ActivityConfigurationDialog currentDialog = getDialog(getActivity());
		if (currentDialog != null) {
			currentDialog.toFront();
			return;
		}

		ComponentConfigurationPanel configView = new ComponentConfigurationPanel(
				activity, factory, serviceRegistry);
		ActivityConfigurationDialog dialog = new ActivityConfigurationDialog(
				getActivity(), configView, editManager);
		setDialog(getActivity(), dialog, fileManager);
	}
}
