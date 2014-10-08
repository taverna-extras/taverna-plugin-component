package net.sf.taverna.t2.component.ui.menu.component;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;
import static net.sf.taverna.t2.component.ui.util.Utils.refreshComponentServiceProvider;
import static net.sf.taverna.t2.workflowmodel.utils.Tools.uniqueProcessorName;
import static org.apache.log4j.Logger.getLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;
import net.sf.taverna.t2.component.api.Component;
import net.sf.taverna.t2.component.api.ComponentException;
import net.sf.taverna.t2.component.api.ComponentFactory;
import net.sf.taverna.t2.component.api.Version;
import net.sf.taverna.t2.component.preference.ComponentPreference;
import net.sf.taverna.t2.component.ui.ComponentActivityConfigurationBean;
import net.sf.taverna.t2.component.ui.panel.RegistryAndFamilyChooserComponentEntryPanel;
import net.sf.taverna.t2.component.ui.serviceprovider.ComponentServiceProviderConfig;
import net.sf.taverna.t2.component.ui.util.ComponentFileType;
import net.sf.taverna.t2.workbench.edits.CompoundEdit;
import net.sf.taverna.t2.workbench.edits.Edit;
import net.sf.taverna.t2.workbench.edits.EditException;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.file.exceptions.OverwriteException;
import net.sf.taverna.t2.workbench.file.exceptions.SaveException;
import net.sf.taverna.t2.workflow.edits.AddActivityEdit;
import net.sf.taverna.t2.workflow.edits.AddActivityInputPortMappingEdit;
import net.sf.taverna.t2.workflow.edits.AddActivityOutputPortMappingEdit;
import net.sf.taverna.t2.workflow.edits.AddDataLinkEdit;
import net.sf.taverna.t2.workflow.edits.AddProcessorEdit;
import net.sf.taverna.t2.workflow.edits.AddWorkflowInputPortEdit;
import net.sf.taverna.t2.workflow.edits.AddWorkflowOutputPortEdit;
import net.sf.taverna.t2.workflow.edits.RemoveActivityEdit;
import net.sf.taverna.t2.workflow.edits.RenameEdit;

public class ComponentCreatorSupport {
	private static final Logger logger = getLogger(ComponentCreatorSupport.class);
	private ComponentFactory factory; //FIXME beaninject
	private FileManager fm; //FIXME beaninject
	private EditManager em; //FIXME beaninject
	private ComponentPreference pref;//FIXME beaninject

	public class CopiedProcessor {
		Element processor;
		Map<String,Element> requiredSubworkflows;
	}

	void moveComponentActivityIntoPlace(Activity toReplace, Processor contextProcessor,
			Activity replacingActivity) throws EditException {
		List<Edit<?>> editsToDo = new ArrayList<>();
		for (InputProcessorPort pip : contextProcessor.getInputPorts())
			editsToDo.add(new AddActivityInputPortMappingEdit(toReplace, pip, null/*FIXME*/));
		for (OutputProcessorPort pop : contextProcessor.getOutputPorts())
			editsToDo.add(new AddActivityOutputPortMappingEdit(toReplace, pop, null/*FIXME*/));
		editsToDo.add(new RemoveActivityEdit(contextProcessor, toReplace));
		editsToDo.add(new AddActivityEdit(contextProcessor, replacingActivity));
		em.doDataflowEdit(contextProcessor.getParent().getParent(),
				new CompoundEdit(editsToDo));
	}

	void connectNewProcessor(Workflow d, Processor newProcessor)
			throws EditException {
		List<Edit<?>> editsToDo = new ArrayList<>();

		for (InputProcessorPort pip : newProcessor.getInputPorts()) {
			InputWorkflowPort dip = new InputWorkflowPort(d, pip.getName());
			// FIXME How to set depth?
			editsToDo.add(new AddWorkflowInputPortEdit(d, dip));
			editsToDo.add(new AddDataLinkEdit(d, new DataLink(d, dip, pip)));
		}

		for (OutputProcessorPort pop : newProcessor.getOutputPorts()) {
			OutputWorkflowPort dop = new OutputWorkflowPort(d, pop.getName());
			// TODO How to indicate depth?
			editsToDo.add(new AddWorkflowOutputPortEdit(d, dop));
			editsToDo.add(new AddDataLinkEdit(d, new DataLink(d, pop, dop)));
		}
		em.doDataflowEdit(d.getParent(), new CompoundEdit(editsToDo));
	}

	public ComponentActivityConfigurationBean saveWorkflowAsComponent(
			WorkflowBundle d, Version.ID ident) throws SaveException, IOException,
			ComponentException {
		if (ident == null)
			return null;

		createInitialComponent(d, ident);

		refreshComponentServiceProvider(new ComponentServiceProviderConfig(
				ident));
		return new ComponentActivityConfigurationBean(ident, factory);
	}

	Version.ID getNewComponentIdentification(String defaultName) {
		RegistryAndFamilyChooserComponentEntryPanel panel = new RegistryAndFamilyChooserComponentEntryPanel(pref);
		panel.setComponentName(defaultName);
		int result = showConfirmDialog(null, panel, "Component location",
				OK_CANCEL_OPTION);
		if (result != OK_OPTION)
			return null;

		Version.ID ident = panel.getComponentVersionIdentification();
		if (ident == null) {
			showMessageDialog(null,
					"Not enough information to create component",
					"Component creation problem", ERROR_MESSAGE);
			return null;
		}

		try {
			Component existingComponent = factory.getComponent(ident);
			if (existingComponent != null) {
				showMessageDialog(null,
						"Component with this name already exists",
						"Component creation problem", ERROR_MESSAGE);
				return null;
			}
		} catch (ComponentException e) {
			logger.error("failed to search registry", e);
			showMessageDialog(null,
					"Problem searching registry: " + e.getMessage(),
					"Component creation problem", ERROR_MESSAGE);
			return null;
		}
		return ident;
	}

	CopiedProcessor copyProcessor(Processor p) throws IOException {
		CopiedProcessor copy = new CopiedProcessor();
		copy.processor = ProcessorXMLSerializer.getInstance().processorToXML(p);
		copy.requiredSubworkflows = new HashMap<>();
		rememberSubworkflows(p, copy);
		return copy;
	}

	void rememberSubworkflows(Processor p, CopiedProcessor copy) {
		for (Activity a : p.getActivityList())
			if (a instanceof NestedDataflow) {
				NestedDataflow da = (NestedDataflow) a;
				Workflow df = da.getNestedDataflow();
				if (!copy.requiredSubworkflows.containsKey(df.getIdentifier())) {
					copy.requiredSubworkflows.put(df.getIdentifier(),
							DataflowXMLSerializer.getInstance()
									.serializeDataflow(df));
					for (Processor sp : df.getProcessors())
						rememberSubworkflows(sp, copy);
				}
			}
	}

	public Processor pasteProcessor(CopiedProcessor copy, Workflow d)
			throws 
			ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		Processor result = ProcessorXMLDeserializer.getInstance()
				.deserializeProcessor(copy.processor, copy.requiredSubworkflows);
		if (result == null)
			return null;

		String newName = uniqueProcessorName(result.getName(), d);

		List<Edit<?>> editList = new ArrayList<>();
		if (!newName.equals(result.getName()))
			editList.add(new RenameEdit<>(result, newName));
		editList.add(new AddProcessorEdit(d, result));
		em.doDataflowEdit(d.getParent(), new CompoundEdit(editList));

		return result;
	}

	public Version.ID createInitialComponent(WorkflowBundle d, Version.ID ident)
			throws ComponentException {
		try {
			fm.saveDataflow(d, ComponentFileType.instance, ident, false);

			em.doDataflowEdit(d, new RenameEdit<>(d, d.getName()));
		} catch (SaveException | IllegalStateException | EditException e) {
			throw new ComponentException(e);
		}
		return ident;
	}
}
