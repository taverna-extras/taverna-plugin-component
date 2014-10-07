/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu;

import java.net.URI;
import java.util.List;

import javax.swing.Action;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.core.Processor;
import net.sf.taverna.t2.ui.menu.AbstractContextualMenuAction;

/**
 * @author alanrw
 */
public class OpenComponentFromComponentActivityMenuAction extends
		AbstractContextualMenuAction {
	private static final URI configureSection = URI
			.create("http://taverna.sf.net/2009/contextMenu/configure");

	private static OpenComponentFromComponentActivityAction action;//FIXME beaninject

	public OpenComponentFromComponentActivityMenuAction() {
		super(configureSection, 75);
	}

	@Override
	public boolean isEnabled() {
		return getSelectedActivity() != null;
	}

	@Override
	protected Action createAction() {
		action.setSelection(getSelectedActivity());
		return action;
	}

	private Activity getSelectedActivity() {
		Object selection = getContextualSelection().getSelection();
		if (!super.isEnabled() || !(selection instanceof Processor))
			return null;

		Processor p = (Processor) selection;
		List<? extends Activity<?>> activities = p.getActivityList();
		if (activities.isEmpty())
			return null;

		return activities.get(0);
	}
}
