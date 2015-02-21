/**
 * 
 */
package org.apache.taverna.component.ui.menu.component;

import static org.apache.log4j.Logger.getLogger;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.file.events.FileManagerEvent;

import org.apache.log4j.Logger;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;
import org.apache.taverna.component.ui.util.Utils;

/**
 * @author alanrw
 */
public class ComponentSaveAction extends AbstractAction implements
		Observer<FileManagerEvent> {
	private static final long serialVersionUID = -2391891750558659714L;
	@SuppressWarnings("unused")
	private static Logger logger = getLogger(ComponentSaveAction.class);
	private static final String SAVE_COMPONENT = "Save component";

	private Utils utils;
	private Action saveWorkflowAction;

	public ComponentSaveAction(Action saveAction, FileManager fm,
			ComponentServiceIcon icon, Utils utils) {
		super(SAVE_COMPONENT, icon.getIcon());
		saveWorkflowAction = saveAction;
		this.utils = utils;
		fm.addObserver(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		saveWorkflowAction.actionPerformed(e);
	}

	@Override
	public void notify(Observable<FileManagerEvent> sender,
			FileManagerEvent message) throws Exception {
		setEnabled(saveWorkflowAction.isEnabled()
				&& utils.currentDataflowIsComponent());
	}
}
