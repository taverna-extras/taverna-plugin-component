/**
 * 
 */
package org.apache.taverna.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

import org.apache.taverna.component.ui.preference.ComponentPreference;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;
import org.apache.taverna.component.ui.util.Utils;

/**
 * @author alanrw
 */
public class ComponentCopyMenuAction extends AbstractComponentMenuAction {
	private static final URI COPY_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentCopy");

	private ComponentPreference prefs;
	private ComponentServiceIcon icon;
	private Utils utils;

	public ComponentCopyMenuAction() {
		super(800, COPY_COMPONENT_URI);
	}
	
	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}

	public void setPreferences(ComponentPreference prefs) {//FIXME beaninject
		this.prefs = prefs;
	}

	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	@Override
	protected Action createAction() {
		return new ComponentCopyAction(prefs, icon, utils);
	}
}
