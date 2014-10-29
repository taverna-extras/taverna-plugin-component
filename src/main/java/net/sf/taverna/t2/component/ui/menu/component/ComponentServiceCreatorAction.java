/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu.component;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static net.sf.taverna.t2.component.ui.serviceprovider.ComponentServiceIcon.getIcon;
import static org.apache.log4j.Logger.getLogger;
import static uk.org.taverna.scufl2.api.common.Scufl2Tools.NESTED_WORKFLOW;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.sf.taverna.t2.component.api.Version;
import net.sf.taverna.t2.component.ui.menu.component.ComponentCreatorSupport.CopiedProcessor;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import uk.org.taverna.scufl2.api.activity.Activity;
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

	private ComponentCreatorSupport support;

	public ComponentServiceCreatorAction(Processor processor, ComponentCreatorSupport support) {
		super("Create component...", getIcon());
		this.support = support;
		p = processor;
		profile = p.getParent().getParent().getMainProfile();
	}

	private Activity getActivity() {
		return profile.getProcessorBindings().getByName(p.getName())
				.getBoundActivity();
	}

	private Workflow getNestedWorkflow(Activity a) {
		JsonNode nw = a.getConfiguration().getJson().get("nestedWorkflow");
		return a.getParent().getParent().getWorkflows().getByName(nw.asText());
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Version.ID ident = support.getNewComponentIdentification(p.getName());
		if (ident == null)
			return;

		Activity a = getActivity();

		try {
			Workflow d;
			if (NESTED_WORKFLOW.equals(a.getType()))
				d = getNestedWorkflow(a);
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

			Activity ca = new Activity();
			support.saveWorkflowAsComponent(d, ident).installConfiguration(ca);
			support.moveComponentActivityIntoPlace(a, p, ca);
		} catch (Exception e) {
			logger.error("failed to instantiate component", e);
			showMessageDialog(null, e.getCause().getMessage(),
					"Component creation failure", ERROR_MESSAGE);
		}
	}
}
