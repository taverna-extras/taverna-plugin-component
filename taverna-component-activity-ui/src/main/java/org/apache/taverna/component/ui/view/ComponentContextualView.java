package org.apache.taverna.component.ui.view;

import static java.lang.String.format;
//import static org.apache.taverna.component.ui.view.ViewUtil.getRawTablesHtml;
import static net.sf.taverna.t2.lang.ui.HtmlUtils.buildTableOpeningTag;
import static net.sf.taverna.t2.lang.ui.HtmlUtils.createEditorPane;
import static net.sf.taverna.t2.lang.ui.HtmlUtils.getHtmlHead;
import static net.sf.taverna.t2.lang.ui.HtmlUtils.panelForHtml;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JEditorPane;

import org.apache.taverna.component.api.Version;

import net.sf.taverna.t2.workbench.configuration.colour.ColourManager;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;

@SuppressWarnings("serial")
public class ComponentContextualView extends ContextualView {
	private JEditorPane editorPane;
	private final Version.ID component;
	ColourManager colourManager;//FIXME beaninject
	ViewUtil viewUtils;//FIXME beaninject;

	public ComponentContextualView(Version.ID component) {
		this.component = component;
		initView();
	}

	@Override
	public JComponent getMainFrame() {
		editorPane = createEditorPane(buildHtml());
		return panelForHtml(editorPane);
	}

	private String buildHtml() {
		StringBuilder html = new StringBuilder(getHtmlHead(getBackgroundColour()));
		html.append(buildTableOpeningTag());
		viewUtils.getRawTablesHtml(component, html);
		return html.append("</table></body></html>").toString();
	}

	public String getBackgroundColour() {
		Color colour = colourManager.getPreferredColour(
				"org.apache.taverna.component.registry.Component");
		return format("#%1$2x%2$2x%3$2x", colour.getRed(), colour.getGreen(),
				colour.getBlue());
	}

	@Override
	public int getPreferredPosition() {
		return 50;
	}

	private static int MAX_LENGTH = 50;

	private String limitName(String fullName) {
		if (fullName.length() > MAX_LENGTH)
			return fullName.substring(0, MAX_LENGTH - 3) + "...";
		return fullName;
	}

	@Override
	public String getViewTitle() {
		return "Component " + limitName(component.getComponentName());
	}

	@Override
	public void refreshView() {
		editorPane.setText(buildHtml());
		repaint();
	}
}
