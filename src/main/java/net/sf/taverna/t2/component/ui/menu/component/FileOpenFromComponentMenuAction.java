/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

import net.sf.taverna.t2.component.preference.ComponentPreference;
import net.sf.taverna.t2.workbench.file.FileManager;

/**
 * @author alanrw
 */
public class FileOpenFromComponentMenuAction extends AbstractComponentMenuAction {
	private static final URI FILE_OPEN_FROM_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentOpen");
	private FileManager fm;
	private ComponentPreference prefs;

	public FileOpenFromComponentMenuAction() {
		super(700, FILE_OPEN_FROM_COMPONENT_URI);
	}

	public void setFileManager(FileManager fm) {
		this.fm = fm;
	}

	public void setPreferences(ComponentPreference prefs) {
		this.prefs = prefs;
	}

	@Override
	protected Action createAction() {
		return new OpenWorkflowFromComponentAction(fm, prefs);
	}
}
