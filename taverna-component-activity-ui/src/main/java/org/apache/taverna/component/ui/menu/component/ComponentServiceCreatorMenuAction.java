/**
 * 
 */
package org.apache.taverna.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

import org.apache.taverna.component.ui.menu.AbstractContextComponentMenuAction;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.core.Processor;
import net.sf.taverna.t2.workbench.selection.SelectionManager;

/**
 * @author alanrw
 */
public class ComponentServiceCreatorMenuAction extends
		AbstractContextComponentMenuAction {
	private static final URI configureSection = URI
			.create("http://taverna.sf.net/2009/contextMenu/configure");

	private ComponentCreatorSupport support;
	private SelectionManager sm;
	private ComponentServiceIcon icon;

	public ComponentServiceCreatorMenuAction() {
		super(configureSection, 60);
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}

	public void setSelectionManager(SelectionManager sm) {
		this.sm = sm;
	}
	
	public void setSupport(ComponentCreatorSupport support) {
		this.support = support;
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
				(Processor) getContextualSelection().getSelection(), sm,
				support, icon);
	}
}
