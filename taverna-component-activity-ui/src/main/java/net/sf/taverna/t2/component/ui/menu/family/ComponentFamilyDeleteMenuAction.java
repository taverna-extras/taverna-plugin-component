/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.family;

import java.net.URI;

import javax.swing.Action;

import net.sf.taverna.t2.component.preference.ComponentPreference;
import net.sf.taverna.t2.component.ui.serviceprovider.ComponentServiceIcon;
import net.sf.taverna.t2.component.ui.util.Utils;
import net.sf.taverna.t2.ui.menu.AbstractMenuAction;
import net.sf.taverna.t2.workbench.file.FileManager;

/**
 * @author alanrw
 */
public class ComponentFamilyDeleteMenuAction extends AbstractMenuAction {
	private static final URI COMPONENT_FAMILY_DELETE_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentFamilyDelete");

	private FileManager fm;
	private ComponentPreference prefs;
	private ComponentServiceIcon icon;
	private Utils utils;

	public ComponentFamilyDeleteMenuAction() {
		super(ComponentFamilyMenuSection.COMPONENT_FAMILY_SECTION, 500,
				COMPONENT_FAMILY_DELETE_URI);
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
		return new ComponentFamilyDeleteAction(fm, prefs, icon, utils);
	}
}
