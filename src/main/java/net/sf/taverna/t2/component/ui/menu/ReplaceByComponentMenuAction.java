/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu;

import java.net.URI;

import javax.swing.Action;

import uk.org.taverna.scufl2.api.core.Processor;
import net.sf.taverna.t2.ui.menu.AbstractContextualMenuAction;

/**
 * @author alanrw
 * 
 */
public class ReplaceByComponentMenuAction extends AbstractContextualMenuAction {
	private static final URI configureSection = URI
			.create("http://taverna.sf.net/2009/contextMenu/configure");
	private ReplaceByComponentAction action;//FIXME beaninject

	public ReplaceByComponentMenuAction() {
		super(configureSection, 75);
	}

	@Override
	public boolean isEnabled() {
		Object selection = getContextualSelection().getSelection();
		if (!super.isEnabled())
			return false;
		return (selection instanceof Processor);
	}

	@Override
	protected Action createAction() {
		action.setSelection((Processor) getContextualSelection().getSelection());
		return action;
	}
}
