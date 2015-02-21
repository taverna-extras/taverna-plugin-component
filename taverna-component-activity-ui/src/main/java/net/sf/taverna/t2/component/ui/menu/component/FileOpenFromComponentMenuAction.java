/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

import java.net.URI;

import javax.swing.Action;

import net.sf.taverna.t2.component.preference.ComponentPreference;
import net.sf.taverna.t2.component.ui.serviceprovider.ComponentServiceIcon;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.file.FileType;
import net.sf.taverna.t2.workbench.views.graph.GraphViewComponent;

/**
 * @author alanrw
 */
public class FileOpenFromComponentMenuAction extends
		AbstractComponentMenuAction {
	private static final URI FILE_OPEN_FROM_COMPONENT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentOpen");
	private FileManager fm;
	private FileType ft;
	private ComponentPreference prefs;
	private GraphViewComponent graphView;
	private ComponentServiceIcon icon;

	public FileOpenFromComponentMenuAction() {
		super(700, FILE_OPEN_FROM_COMPONENT_URI);
	}

	public void setFileManager(FileManager fm) {
		this.fm = fm;
	}

	public void setFileType(FileType ft) {
		this.ft = ft;
	}

	public void setPreferences(ComponentPreference prefs) {
		this.prefs = prefs;
	}

	public void setGraphView(GraphViewComponent graphView) {
		this.graphView = graphView;
	}

	public void setIcon(ComponentServiceIcon icon) {
		this.icon = icon;
	}

	@Override
	protected Action createAction() {
		return new OpenWorkflowFromComponentAction(fm, ft, prefs, graphView,
				icon);
	}
}
