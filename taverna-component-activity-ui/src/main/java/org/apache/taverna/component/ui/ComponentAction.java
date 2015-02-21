package org.apache.taverna.component.ui;

import static java.awt.Color.RED;
import static javax.swing.SwingUtilities.invokeLater;
import static org.apache.log4j.Logger.getLogger;

import javax.swing.AbstractAction;

import net.sf.taverna.t2.workbench.models.graph.GraphController;
import net.sf.taverna.t2.workbench.models.graph.svg.SVGGraph;
import net.sf.taverna.t2.workbench.views.graph.GraphViewComponent;

import org.apache.log4j.Logger;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

@SuppressWarnings("serial")
public abstract class ComponentAction extends AbstractAction {
	private static Logger logger = getLogger(ComponentAction.class);

	protected GraphViewComponent graphView;

	protected ComponentAction(String title, GraphViewComponent graphView) {
		this.graphView = graphView;
	}

	public void setIcon(ComponentServiceIcon icon) {
        putValue(SMALL_ICON, icon.getIcon());
	}

	protected void markGraphAsBelongingToComponent(WorkflowBundle bundle) {
		final GraphController gc = graphView.getGraphController(bundle
				.getMainWorkflow());
		invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					SVGGraph g = (SVGGraph) gc.getGraph();
					g.setFillColor(RED);
					gc.redraw();
				} catch (NullPointerException e) {
					logger.error(e);
				}
			}
		});
	}
}
