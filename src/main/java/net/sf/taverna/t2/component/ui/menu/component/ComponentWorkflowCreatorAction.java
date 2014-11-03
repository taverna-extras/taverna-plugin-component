/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.apache.log4j.Logger.getLogger;

import java.awt.event.ActionEvent;

import net.sf.taverna.t2.component.api.Version;
import net.sf.taverna.t2.component.ui.ComponentAction;
import net.sf.taverna.t2.component.ui.serviceprovider.ComponentServiceIcon;
import net.sf.taverna.t2.component.ui.util.Utils;
import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.file.events.FileManagerEvent;
import net.sf.taverna.t2.workbench.views.graph.GraphViewComponent;

import org.apache.log4j.Logger;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * @author alanrw
 */
public class ComponentWorkflowCreatorAction extends ComponentAction implements
		Observer<FileManagerEvent> {
	private static final long serialVersionUID = -299685223430721587L;
	private static Logger logger = getLogger(ComponentWorkflowCreatorAction.class);
	private static final String CREATE_COMPONENT = "Create component from current workflow...";

	private ComponentCreatorSupport support;
	private FileManager fileManager;
	private Utils utils;

	public ComponentWorkflowCreatorAction(ComponentCreatorSupport support,
			FileManager fm, GraphViewComponent graphView,
			ComponentServiceIcon icon, Utils utils) {
		super(CREATE_COMPONENT, graphView);
		this.support = support;
		this.utils = utils;
		fm.addObserver(this);
		this.setIcon(icon);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		WorkflowBundle bundle = fileManager.getCurrentDataflow();
		try {
			Version.ID ident = support.getNewComponentIdentification(bundle.getName());//TODO is this right
			if (ident == null)
				return;
			support.saveWorkflowAsComponent(bundle, ident);
		} catch (Exception e) {
			showMessageDialog(graphView, e.getCause().getMessage(),
					"Component creation failure", ERROR_MESSAGE);
			logger.error("failed to save workflow as component", e);
		}
	}

	@Override
	public void notify(Observable<FileManagerEvent> sender,
			FileManagerEvent message) throws Exception {
		setEnabled(!utils.currentDataflowIsComponent());
	}
}
