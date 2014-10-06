/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu;

import static java.awt.Color.RED;
import static javax.swing.SwingUtilities.invokeLater;
import static net.sf.taverna.t2.component.ui.serviceprovider.ComponentServiceIcon.getIcon;
import static org.apache.log4j.Logger.getLogger;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.sf.taverna.t2.component.ComponentActivity;
import net.sf.taverna.t2.component.api.Version;
import net.sf.taverna.t2.component.ui.ComponentAction;
import net.sf.taverna.t2.component.ui.util.ComponentFileType;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.file.exceptions.OpenException;
import net.sf.taverna.t2.workbench.models.graph.GraphController;
import net.sf.taverna.t2.workbench.models.graph.svg.SVGGraph;
import net.sf.taverna.t2.workbench.views.graph.GraphViewComponent;
import net.sf.taverna.t2.workflowmodel.Dataflow;

import org.apache.log4j.Logger;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * @author alanrw
 */
@SuppressWarnings("serial")
public class OpenComponentFromComponentActivityAction extends ComponentAction {
	private static Logger logger = getLogger(OpenComponentFromComponentActivityAction.class);

	private FileManager fileManager;//FIXME beaninject

	public OpenComponentFromComponentActivityAction() {
		super("Open component...");
	}

	private ComponentActivity selection;

	@Override
	public void actionPerformed(ActionEvent ev) {
		Version.ID ident = selection.getConfiguration();

		try {
			WorkflowBundle d = fileManager.openDataflow(
					ComponentFileType.instance, ident);
			markGraphAsBelongingToComponent(d);
		} catch (OpenException e) {
			logger.error("failed to open component", e);
		}
	}

	public void setSelection(ComponentActivity selection) {
		this.selection = selection;
	}
}