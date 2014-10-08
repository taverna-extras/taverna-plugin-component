/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.core.Processor;
import net.sf.taverna.t2.component.ui.menu.AbstractContextComponentMenuAction;

/**
 * @author alanrw
 */
public class ComponentServiceCreatorMenuAction extends
		AbstractContextComponentMenuAction {
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
		if (a == null)
			return false;
		return !isComponentActivity(a);
	}

	@Override
	protected Action createAction() {
		return new ComponentServiceCreatorAction(
				(Processor) getContextualSelection().getSelection());
	}
}
