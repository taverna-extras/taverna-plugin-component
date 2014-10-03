package net.sf.taverna.t2.component.ui;

import static java.awt.Color.RED;
import static javax.swing.SwingUtilities.invokeLater;
import static net.sf.taverna.t2.component.ui.serviceprovider.ComponentServiceIcon.getIcon;
import static org.apache.log4j.Logger.getLogger;

import javax.swing.AbstractAction;

import net.sf.taverna.t2.workbench.models.graph.GraphController;
import net.sf.taverna.t2.workbench.models.graph.svg.SVGGraph;
import net.sf.taverna.t2.workbench.views.graph.GraphViewComponent;

import org.apache.log4j.Logger;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

@SuppressWarnings("serial")
public abstract class ComponentAction extends AbstractAction {
	private static Logger logger = getLogger(ComponentAction.class);

	protected GraphViewComponent graphView; //FIXME beaninject

	protected ComponentAction(String title) {
		super(title, getIcon());
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
