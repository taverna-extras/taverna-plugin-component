package net.sf.taverna.t2.component.ui.file;

import static java.awt.Color.WHITE;
import static java.awt.Font.BOLD;
import static javax.swing.SwingUtilities.invokeLater;
import static javax.swing.SwingUtilities.isEventDispatchThread;
import static net.sf.taverna.t2.component.ui.util.Utils.currentDataflowIsComponent;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

import net.sf.taverna.t2.component.api.Version;
import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.workbench.StartupSPI;
import net.sf.taverna.t2.workbench.configuration.colour.ColourManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.file.events.FileManagerEvent;
import net.sf.taverna.t2.workbench.models.graph.svg.SVGGraphController;
import net.sf.taverna.t2.workbench.views.graph.GraphViewComponent;

import org.apache.batik.swing.JSVGCanvas;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public class FileManagerObserver implements StartupSPI {
	private static final Color COLOR = new Color(230, 147, 210);

	private FileManager fileManager;
	private ColourManager colours;
	private GraphViewComponent graphView;

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setColours(ColourManager colours) {
		this.colours = colours;
	}

	public void setGraphView(GraphViewComponent graphView) {
		this.graphView = graphView;
	}

	@Override
	public boolean startup() {
		colours.setPreferredColour(
				"net.sf.taverna.t2.component.registry.Component", COLOR);
		colours.setPreferredColour(
				"net.sf.taverna.t2.component.ComponentActivity", COLOR);
		fileManager.addObserver(new Observer<FileManagerEvent>() {
			@Override
			public void notify(Observable<FileManagerEvent> observable,
					FileManagerEvent event) throws Exception {
				FileManagerObserverRunnable runnable = new FileManagerObserverRunnable();
				if (isEventDispatchThread())
					runnable.run();
				else
					invokeLater(runnable);
			}
		});
		return true;
	}

	@Override
	public int positionHint() {
		return 200;
	}

	public class FileManagerObserverRunnable implements Runnable {
		@Override
		public void run() {
			WorkflowBundle currentDataflow = fileManager.getCurrentDataflow();
			if (currentDataflow == null)
				return;
			SVGGraphController graphController = (SVGGraphController) graphView
					.getGraphController(currentDataflow.getMainWorkflow());
			if (graphController == null)
				return;
			JSVGCanvas svgCanvas = graphController.getSVGCanvas();
			Object dataflowSource = fileManager
					.getDataflowSource(currentDataflow);
			if (currentDataflowIsComponent())
				svgCanvas.setBorder(new ComponentBorder(
						(Version.ID) dataflowSource));
			else
				svgCanvas.setBorder(null);
			svgCanvas.repaint();
		}
	}

	static class ComponentBorder implements Border {
		private final Insets insets = new Insets(25, 0, 0, 0);
		private final String text;

		public ComponentBorder(Version.ID identification) {
			text = "Component : " + identification.getComponentName();
		}

		@Override
		public Insets getBorderInsets(java.awt.Component c) {
			return insets;
		}

		@Override
		public boolean isBorderOpaque() {
			return true;
		}

		@Override
		public void paintBorder(java.awt.Component c, Graphics g, int x, int y,
				int width, int height) {
			g.setColor(COLOR);
			g.fillRect(x, y, width, 20);
			g.setFont(g.getFont().deriveFont(BOLD));
			g.setColor(WHITE);
			g.drawString(text, x + 5, y + 15);
		}
	}
}
