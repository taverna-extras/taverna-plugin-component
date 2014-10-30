/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

import static net.sf.taverna.t2.component.ui.serviceprovider.ComponentServiceIcon.getIcon;
import static net.sf.taverna.t2.component.ui.util.Utils.currentDataflowIsComponent;
import static org.apache.log4j.Logger.getLogger;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.file.events.FileManagerEvent;

import org.apache.log4j.Logger;

/**
 * @author alanrw
 */
public class ComponentSaveAction extends AbstractAction implements
		Observer<FileManagerEvent> {
	private static final long serialVersionUID = -2391891750558659714L;
	@SuppressWarnings("unused")
	private static Logger logger = getLogger(ComponentSaveAction.class);
	private static final String SAVE_COMPONENT = "Save component";

	private Action saveWorkflowAction;

	public ComponentSaveAction(Action saveAction, FileManager fm) {
		super(SAVE_COMPONENT, getIcon());
		saveWorkflowAction = saveAction;
		fm.addObserver(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		saveWorkflowAction.actionPerformed(e);
	}

	@Override
	public void notify(Observable<FileManagerEvent> sender,
			FileManagerEvent message) throws Exception {
		setEnabled(saveWorkflowAction.isEnabled() && currentDataflowIsComponent());
	}
}
