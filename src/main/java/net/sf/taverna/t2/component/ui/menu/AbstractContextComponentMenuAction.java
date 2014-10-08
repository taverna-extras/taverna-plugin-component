package net.sf.taverna.t2.component.ui.menu;

import java.net.URI;

import net.sf.taverna.t2.ui.menu.AbstractContextualMenuAction;
import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.core.Processor;

public abstract class AbstractContextComponentMenuAction extends AbstractContextualMenuAction {
	public AbstractContextComponentMenuAction(URI parentId, int positionHint) {
		super(parentId, positionHint);
	}

	public AbstractContextComponentMenuAction(URI parentId, int positionHint, URI id) {
		super(parentId, positionHint, id);
	}

	protected boolean isComponentActivity(Activity act) {
		if (act == null)
			return false;
		Configuration c = act.getParent().getConfigurations()
				.getByName(act.getName());
		if (c == null)
			return false;
		// How do we actually check if an activity is a component activity?
		String type = c.getJsonAsObjectNode().get("???").asText();// FIXME
		return COMPONENT_TYPE.equals(type);//FIXME
	}

	protected Activity findActivity() {
		if (getContextualSelection() == null)
			return null;
		Object selection = getContextualSelection().getSelection();
		if (selection instanceof Processor) {
			Processor processor = (Processor) selection;
			return processor.getParent().getParent().getMainProfile()
					.getProcessorBindings().getByName(processor.getName())
					.getBoundActivity();
		} else if (selection instanceof Activity)
			return (Activity) selection;
		return null;
	}
}
