/**
 * 
 */
package org.apache.taverna.component.ui.menu.component;

import static javax.swing.JOptionPane.CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.showOptionDialog;
import static org.apache.log4j.Logger.getLogger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.file.FileType;
import net.sf.taverna.t2.workbench.file.exceptions.OpenException;
import net.sf.taverna.t2.workbench.views.graph.GraphViewComponent;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.Component;
import org.apache.taverna.component.api.Family;
import org.apache.taverna.component.api.Registry;
import org.apache.taverna.component.api.Version;
import org.apache.taverna.component.ui.ComponentAction;
import org.apache.taverna.component.ui.panel.ComponentChoiceMessage;
import org.apache.taverna.component.ui.panel.ComponentVersionChooserPanel;
import org.apache.taverna.component.ui.preference.ComponentPreference;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * @author alanrw
 */
public class OpenWorkflowFromComponentAction extends ComponentAction {
	private static final long serialVersionUID = 7382677337746318211L;
	private static final Logger logger = getLogger(OpenWorkflowFromComponentAction.class);
	private static final String ACTION_NAME = "Open component...";
	private static final String ACTION_DESCRIPTION = "Open the workflow that implements a component";

	private final FileManager fm;
	private final FileType ft;
	private final ComponentPreference prefs;

	public OpenWorkflowFromComponentAction(FileManager fm, FileType ft,
			ComponentPreference prefs, GraphViewComponent graphView,
			ComponentServiceIcon icon) {
		super(ACTION_NAME, graphView);
		this.fm = fm;
		this.ft = ft;
		this.prefs = prefs;
		setIcon(icon);
		putValue(SHORT_DESCRIPTION, ACTION_DESCRIPTION);
	}

	@Override
	public void actionPerformed(ActionEvent arg) {
		final ComponentVersionChooserPanel panel = new ComponentVersionChooserPanel(prefs);	
		
		final JButton okay = new JButton("OK");
		okay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getOptionPane((JComponent) e.getSource()).setValue(OK_OPTION);
				doOpen(panel.getChosenRegistry(), panel.getChosenFamily(),
						panel.getChosenComponent(),
						panel.getChosenComponentVersion());
			}
		});
		okay.setEnabled(false);
		// Only enable the OK button of a component is not null
		panel.getComponentChooserPanel().addObserver(
				new Observer<ComponentChoiceMessage>() {
					@Override
					public void notify(
							Observable<ComponentChoiceMessage> sender,
							ComponentChoiceMessage message) throws Exception {
						okay.setEnabled(message.getChosenComponent() != null);
					}
				});

		final JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
                getOptionPane((JComponent)e.getSource()).setValue(CANCEL_OPTION);
		    }
		});

		showOptionDialog(graphView, panel, "Component version choice",
				YES_NO_OPTION, QUESTION_MESSAGE, null, new Object[] { okay,
						cancel }, okay);
	}
	
    protected JOptionPane getOptionPane(JComponent parent) {
		if (parent instanceof JOptionPane)
			return (JOptionPane) parent;
		return getOptionPane((JComponent) parent.getParent());
    }

	private void doOpen(Registry registry, Family family, Component component,
			Version version) {
		Version.ID ident = new Version.Identifier(
				registry.getRegistryBase(), family.getName(),
				component.getName(), version.getVersionNumber());

		try {
			WorkflowBundle d = fm.openDataflow(ft, ident);
			markGraphAsBelongingToComponent(d);
		} catch (OpenException e) {
			logger.error("Failed to open component definition", e);
		}
	}
}
