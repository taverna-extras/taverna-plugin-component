/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu;

import java.net.URI;

import javax.swing.Action;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.core.Processor;
import net.sf.taverna.t2.component.api.ComponentFactory;
import net.sf.taverna.t2.ui.menu.AbstractContextualMenuAction;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.selection.SelectionManager;

/**
 * @author alanrw
 */
public class OpenComponentFromComponentActivityMenuAction extends
		AbstractContextualMenuAction {
	private static final URI configureSection = URI
			.create("http://taverna.sf.net/2009/contextMenu/configure");

	private SelectionManager sm;//FIXME beaninject
	private FileManager fileManager;//FIXME beaninject
	private ComponentFactory factory;//FIXME beaninject

	public OpenComponentFromComponentActivityMenuAction() {
		super(configureSection, 75);
	}

	@Override
	public boolean isEnabled() {
		return getSelectedActivity() != null;
	}

	@Override
	protected Action createAction() {
		OpenComponentFromComponentActivityAction action = new OpenComponentFromComponentActivityAction(
				fileManager, factory);
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
