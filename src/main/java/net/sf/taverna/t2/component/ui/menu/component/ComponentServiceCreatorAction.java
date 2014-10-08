/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;
import static net.sf.taverna.t2.component.ui.serviceprovider.ComponentServiceIcon.getIcon;
import static net.sf.taverna.t2.component.ui.util.Utils.refreshComponentServiceProvider;
import static net.sf.taverna.t2.workflowmodel.utils.Tools.uniqueProcessorName;
import static org.apache.log4j.Logger.getLogger;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;

import net.sf.taverna.t2.component.api.Component;
import net.sf.taverna.t2.component.api.ComponentException;
import net.sf.taverna.t2.component.api.ComponentFactory;
import net.sf.taverna.t2.component.api.Version;
import net.sf.taverna.t2.component.ui.menu.component.ComponentCreatorSupport.CopiedProcessor;
import net.sf.taverna.t2.component.ui.panel.RegistryAndFamilyChooserComponentEntryPanel;
import net.sf.taverna.t2.component.ui.serviceprovider.ComponentServiceProviderConfig;
import net.sf.taverna.t2.component.ui.util.ComponentFileType;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.file.exceptions.OverwriteException;
import net.sf.taverna.t2.workbench.file.exceptions.SaveException;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * @author alanrw
 */
public class ComponentServiceCreatorAction extends AbstractAction {
	private static final long serialVersionUID = -2611514696254112190L;
	private static Logger logger = getLogger(ComponentServiceCreatorAction.class);

	private final Processor p;
	private final Profile profile;

	private ComponentCreatorSupport support;//FIXME beaninject
	private FileManager fm; //FIXME beaninject

	public ComponentServiceCreatorAction(Processor processor) {
		super("Create component...", getIcon());
		p = processor;
		profile = p.getParent().getParent().getMainProfile();
	}

	private Activity getActivity() {
		return profile.getProcessorBindings().getByName(p.getName())
				.getBoundActivity();
	}

	private Workflow getNestedWorkflow(Activity a) {
		Configuration c = profile.getConfigurations().getByName(a.getName());
		c.getJsonAsObjectNode();
		return null;//FIXME!
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Version.ID ident = support.getNewComponentIdentification(p.getName());
		if (ident == null)
			return;

		Activity a = getActivity();
		WorkflowBundle current = fm.getCurrentDataflow();

		try {
			Workflow d;
			if (a instanceof NestedDataflow)
				d = ((NestedDataflow) a).getNestedDataflow();
			else {
				d = new Workflow();

				/* TODO: Keep the description */
				// fm.setCurrentDataflow(current);

				CopiedProcessor processorElement = support.copyProcessor(p);

				Processor newProcessor;
				try {
					newProcessor = support.pasteProcessor(processorElement, d);
				} catch (IllegalArgumentException e) {
					logger.error(
							"failed to paste processor representing component",
							e);
					showMessageDialog(null, e.getMessage(),
							"Component creation failure", ERROR_MESSAGE);
					return;
				}

				support.connectNewProcessor(d, newProcessor);
			}

			Activity ca = new ComponentActivity();
			ca.configure(support.saveWorkflowAsComponent(d, ident));
			support.moveComponentActivityIntoPlace(a, p, ca);
		} catch (Exception e) {
			logger.error("failed to instantiate component", e);
			showMessageDialog(null, e.getCause().getMessage(),
					"Component creation failure", ERROR_MESSAGE);
		}
	}
}
