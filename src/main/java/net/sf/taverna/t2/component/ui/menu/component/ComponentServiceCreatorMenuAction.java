/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.core.Processor;
import net.sf.taverna.t2.component.ComponentActivity;
import net.sf.taverna.t2.ui.menu.AbstractContextualMenuAction;

/**
 * @author alanrw
 */
public class ComponentServiceCreatorMenuAction extends
		AbstractContextualMenuAction {
	private static final URI configureSection = URI
			.create("http://taverna.sf.net/2009/contextMenu/configure");

	public ComponentServiceCreatorMenuAction() {
		super(configureSection, 60);
	}

	@Override
	public boolean isEnabled() {
		if (!super.isEnabled())
			return false;
		Activity a = findActivity();
		return (a != null) && !(a instanceof ComponentActivity);
	}

	@Override
	protected Action createAction() {
		return new ComponentServiceCreatorAction(
				(Processor) getContextualSelection().getSelection());
	}

	protected Activity findActivity() {
		if (getContextualSelection() == null)
			return null;
		Object selection = getContextualSelection().getSelection();
		if (selection instanceof Processor) {
			Processor processor = (Processor) selection;
			for (Activity activity : processor.getActivityList())
				if (Activity.class.isInstance(activity))
					return Activity.class.cast(activity);
		}
		return null;
	}
}
