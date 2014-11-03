/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

import static org.apache.log4j.Logger.getLogger;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import net.sf.taverna.t2.component.ui.serviceprovider.ComponentServiceIcon;
import net.sf.taverna.t2.component.ui.util.Utils;
import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.file.events.FileManagerEvent;

import org.apache.log4j.Logger;

/**
 * @author alanrw
 */
public class ComponentCloseAction extends AbstractAction implements
		Observer<FileManagerEvent> {
	private static final long serialVersionUID = -153986599735293879L;
	private static final String CLOSE_COMPONENT = "Close component";
	@SuppressWarnings("unused")
	private static Logger logger = getLogger(ComponentCloseAction.class);

	private Action closeAction;
	private final Utils utils;

	public ComponentCloseAction(Action closeWorkflowAction, FileManager fm,
			ComponentServiceIcon icon, Utils utils) {
		super(CLOSE_COMPONENT, icon.getIcon());
		closeAction = closeWorkflowAction;
		this.utils = utils;
		fm.addObserver(this);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		closeAction.actionPerformed(arg0);
	}

	@Override
	public boolean isEnabled() {
		return utils.currentDataflowIsComponent();
	}

	@Override
	public void notify(Observable<FileManagerEvent> sender,
			FileManagerEvent message) throws Exception {
		setEnabled(utils.currentDataflowIsComponent());
	}
}
