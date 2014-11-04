/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

import net.sf.taverna.t2.component.preference.ComponentPreference;
import net.sf.taverna.t2.component.ui.serviceprovider.ComponentServiceIcon;
import net.sf.taverna.t2.component.ui.util.Utils;
import net.sf.taverna.t2.workbench.file.FileManager;

/**
 * @author alanrw
 */
public class ComponentDeleteMenuAction extends AbstractComponentMenuAction {
	private static final URI DELETE_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentDelete");

	private FileManager fm;
	private ComponentServiceIcon icon;
	private ComponentPreference prefs;
	private Utils utils;

	public ComponentDeleteMenuAction() {
		super(1200, DELETE_COMPONENT_URI);
	}

	public void setFileManager(FileManager fm) {
		this.fm = fm;
	}
	
	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}

	public void setPreferences(ComponentPreference prefs) {
		this.prefs = prefs;
	}

	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	@Override
	protected Action createAction() {
		return new ComponentDeleteAction(fm, prefs, icon, utils);
	}
}
