/**
 * 
 */
package org.apache.taverna.component.ui.menu;

import java.net.URI;

import javax.swing.Action;

import org.apache.taverna.component.api.ComponentFactory;
import org.apache.taverna.component.ui.preference.ComponentPreference;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;

import net.sf.taverna.t2.ui.menu.AbstractContextualMenuAction;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.selection.SelectionManager;
import uk.org.taverna.scufl2.api.core.Processor;

/**
 * @author alanrw
 */
public class ReplaceByComponentMenuAction extends AbstractContextualMenuAction {
	private static final URI configureSection = URI
			.create("http://taverna.sf.net/2009/contextMenu/configure");

	private ComponentPreference preferences;
	private EditManager editManager;
	private SelectionManager selectionManager;
	private ComponentFactory factory;
	private ComponentServiceIcon icon;

	public ReplaceByComponentMenuAction() {
		super(configureSection, 75);
	}

	public void setPreferences(ComponentPreference preferences) {
		this.preferences = preferences;
	}

	public void setEditManager(EditManager editManager) {
		this.editManager = editManager;
	}

	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
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
		ReplaceByComponentAction action = new ReplaceByComponentAction(
				preferences, factory, editManager, selectionManager, icon);
		action.setSelection((Processor) getContextualSelection().getSelection());
		return action;
	}
}
