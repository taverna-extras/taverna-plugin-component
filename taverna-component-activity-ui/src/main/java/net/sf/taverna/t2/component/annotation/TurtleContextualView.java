/**
 * 
 */
package net.sf.taverna.t2.component.annotation;

import static java.awt.BorderLayout.CENTER;
import static net.sf.taverna.t2.component.annotation.SemanticAnnotationUtils.findSemanticAnnotation;
import static net.sf.taverna.t2.component.annotation.SemanticAnnotationUtils.getStrippedAnnotationContent;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;

import org.slf4j.Logger;

import uk.org.taverna.scufl2.api.annotation.Annotation;
import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * @author alanrw
 */
public class TurtleContextualView extends ContextualView {
	private static final long serialVersionUID = -3401885589263647202L;
	private static final Logger log = getLogger(TurtleContextualView.class);
	private JPanel panel;
	private String annotationContent = "";

	public TurtleContextualView(AbstractNamed selection, WorkflowBundle bundle)  {
		Annotation annotation = findSemanticAnnotation(selection);
		try {
			if (annotation != null)
				annotationContent = getStrippedAnnotationContent(annotation);
		} catch (IOException e) {
			log.info("failed to read semantic annotation; using empty string", e);
		}
		initialise();
		initView();
	}

	@Override
	public JComponent getMainFrame() {
		return panel;
	}

	@Override
	public int getPreferredPosition() {
		return 512;
	}

	@Override
	public String getViewTitle() {
		return "Turtle representation";
	}

	@Override
	public void refreshView() {
		initialise();
	}

	protected final void initialise() {
		if (panel == null)
			panel = new JPanel(new BorderLayout());
		else
			panel.removeAll();
		JTextArea textArea = new JTextArea(20, 80);
		textArea.setEditable(false);
		textArea.setText(annotationContent);
		panel.add(textArea, CENTER);
		revalidate();
	}
}
