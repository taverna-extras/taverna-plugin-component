/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

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

import net.sf.taverna.t2.component.api.Component;
import net.sf.taverna.t2.component.api.Family;
import net.sf.taverna.t2.component.api.Registry;
import net.sf.taverna.t2.component.api.Version;
import net.sf.taverna.t2.component.ui.ComponentAction;
import net.sf.taverna.t2.component.ui.panel.ComponentChoiceMessage;
import net.sf.taverna.t2.component.ui.panel.ComponentVersionChooserPanel;
import net.sf.taverna.t2.component.ui.util.ComponentFileType;
import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.file.exceptions.OpenException;

import org.apache.log4j.Logger;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * @author alanrw
 */
public class OpenWorkflowFromComponentAction extends ComponentAction {
	private static final long serialVersionUID = 7382677337746318211L;
	private static final Logger logger = getLogger(OpenWorkflowFromComponentAction.class);
	private static final String ACTION_NAME = "Open component...";
	private static final String ACTION_DESCRIPTION = "Open the workflow that implements a component";

	private FileManager fm;//FIXME beaninject

	public OpenWorkflowFromComponentAction(final java.awt.Component component) {
		super(ACTION_NAME);
		putValue(SHORT_DESCRIPTION, ACTION_DESCRIPTION);
	}

	@Override
	public void actionPerformed(ActionEvent arg) {
		final ComponentVersionChooserPanel panel = new ComponentVersionChooserPanel();	
		
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
			WorkflowBundle d = fm.openDataflow(ComponentFileType.instance, ident);
			markGraphAsBelongingToComponent(d);
		} catch (OpenException e) {
			logger.error("Failed to open component definition", e);
		}
	}
}
