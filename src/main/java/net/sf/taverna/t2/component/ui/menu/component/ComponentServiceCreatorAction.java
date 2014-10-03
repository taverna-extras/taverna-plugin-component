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

import net.sf.taverna.t2.component.ComponentActivity;
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
import net.sf.taverna.t2.workflowmodel.CompoundEdit;
import net.sf.taverna.t2.workflowmodel.ConfigurationException;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.Datalink;
import net.sf.taverna.t2.workflowmodel.Edit;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.Edits;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.ProcessorInputPort;
import net.sf.taverna.t2.workflowmodel.ProcessorOutputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException;
import net.sf.taverna.t2.workflowmodel.processor.activity.NestedDataflow;
import net.sf.taverna.t2.workflowmodel.serialization.DeserializationException;
import net.sf.taverna.t2.workflowmodel.serialization.SerializationException;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * @author alanrw
 */
public class ComponentServiceCreatorAction extends AbstractAction {
	private static final long serialVersionUID = -2611514696254112190L;
	private static Logger logger = getLogger(ComponentServiceCreatorAction.class);

	private final Processor p;

	private ComponentCreatorSupport support;//FIXME beaninject
	private FileManager fm; //FIXME beaninject
	private Edits edits; //FIXME beaninject

	public ComponentServiceCreatorAction(Processor processor) {
		super("Create component...", getIcon());
		this.p = processor;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Version.ID ident = support.getNewComponentIdentification(p
				.getLocalName());
		if (ident == null)
			return;

		Activity<?> a = p.getActivityList().get(0);
		WorkflowBundle current = fm.getCurrentDataflow();

		try {
			Dataflow d;
			if (a instanceof NestedDataflow)
				d = ((NestedDataflow) a).getNestedDataflow();
			else {
				d = edits.createDataflow();

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

			ComponentActivity ca = new ComponentActivity();
			ca.configure(support.saveWorkflowAsComponent(d, ident));
			moveComponentActivityIntoPlace(a, current, ca);
		} catch (Exception e) {
			logger.error("failed to instantiate component", e);
			showMessageDialog(null, e.getCause().getMessage(),
					"Component creation failure", ERROR_MESSAGE);
		}
	}

}
