package net.sf.taverna.t2.component.ui.menu.component;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;
import static net.sf.taverna.t2.component.ui.util.Utils.refreshComponentServiceProvider;
import static net.sf.taverna.t2.workflowmodel.utils.Tools.uniqueProcessorName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.jdom.JDOMException;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import net.sf.taverna.t2.component.api.Component;
import net.sf.taverna.t2.component.api.ComponentException;
import net.sf.taverna.t2.component.api.ComponentFactory;
import net.sf.taverna.t2.component.api.Version;
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

public class ComponentCreatorSupport {
	private ComponentFactory factory; //FIXME beaninject
	private FileManager fm; //FIXME beaninject
	private EditManager em; //FIXME beaninject
	private Edits edits; //FIXME beaninject

	public class CopiedProcessor {
		Element processor;
		Map<String,Element> requiredSubworkflows;
	}
	private void moveComponentActivityIntoPlace(Activity<?> a,
			Dataflow current, ComponentActivity ca) throws EditException {
		List<Edit<?>> editsToDo = new ArrayList<>();

		for (ProcessorInputPort pip : p.getInputPorts())
			editsToDo.add(edits.getAddActivityInputPortMappingEdit(ca,
					pip.getName(), pip.getName()));

		for (ProcessorOutputPort pop : p.getOutputPorts())
			editsToDo.add(edits.getAddActivityOutputPortMappingEdit(ca,
					pop.getName(), pop.getName()));

		editsToDo.add(edits.getRemoveActivityEdit(p, a));
		editsToDo.add(edits.getAddActivityEdit(p, ca));
		em.doDataflowEdit(current, new CompoundEdit(editsToDo));
	}

	void connectNewProcessor(Dataflow d, Processor newProcessor)
			throws EditException {
		List<Edit<?>> editsToDo = new ArrayList<>();

		for (ProcessorInputPort pip : newProcessor.getInputPorts()) {
			DataflowInputPort dip = edits.createDataflowInputPort(
					pip.getName(), pip.getDepth(), pip.getDepth(), d);
			editsToDo.add(edits.getAddDataflowInputPortEdit(d, dip));

			Datalink dl = edits
					.createDatalink(dip.getInternalOutputPort(), pip);
			editsToDo.add(edits.getConnectDatalinkEdit(dl));
		}

		for (ProcessorOutputPort pop : newProcessor.getOutputPorts()) {
			DataflowOutputPort dop = edits.createDataflowOutputPort(
					pop.getName(), d);
			editsToDo.add(edits.getAddDataflowOutputPortEdit(d, dop));

			Datalink dl = edits.createDatalink(pop, dop.getInternalInputPort());
			editsToDo.add(edits.getConnectDatalinkEdit(dl));
		}
		em.doDataflowEdit(d, new CompoundEdit(editsToDo));
	}

	public ComponentActivityConfigurationBean saveWorkflowAsComponent(
			WorkflowBundle d, Version.ID ident) throws SaveException, IOException,
			ConfigurationException, ComponentException {
		if (ident == null)
			return null;

		createInitialComponent(d, ident);

		refreshComponentServiceProvider(new ComponentServiceProviderConfig(
				ident));
		return new ComponentActivityConfigurationBean(ident);
	}

	Version.ID getNewComponentIdentification(String defaultName) {
		RegistryAndFamilyChooserComponentEntryPanel panel = new RegistryAndFamilyChooserComponentEntryPanel();
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

	CopiedProcessor copyProcessor(Processor p) throws IOException, JDOMException,
			SerializationException {
		CopiedProcessor copy = new CopiedProcessor();
		copy.processor = ProcessorXMLSerializer.getInstance().processorToXML(p);
		copy.requiredSubworkflows = new HashMap<>();
		rememberSubworkflows(p, copy);
		return copy;
	}

	void rememberSubworkflows(Processor p, CopiedProcessor copy) throws SerializationException {
		for (Activity<?> a : p.getActivityList())
			if (a instanceof NestedDataflow) {
				NestedDataflow da = (NestedDataflow) a;
				Dataflow df = da.getNestedDataflow();
				if (!copy.requiredSubworkflows.containsKey(df.getIdentifier())) {
					copy.requiredSubworkflows.put(df.getIdentifier(),
							DataflowXMLSerializer.getInstance()
									.serializeDataflow(df));
					for (Processor sp : df.getProcessors())
						rememberSubworkflows(sp, copy);
				}
			}
	}

	public Processor pasteProcessor(CopiedProcessor copy, Dataflow d)
			throws ActivityConfigurationException, Exception,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, DeserializationException {
		Processor result = ProcessorXMLDeserializer.getInstance()
				.deserializeProcessor(copy.processor, copy.requiredSubworkflows);
		if (result == null)
			return null;

		String newName = uniqueProcessorName(result.getLocalName(), d);

		List<Edit<?>> editList = new ArrayList<>();
		if (!newName.equals(result.getLocalName()))
			editList.add(edits.getRenameProcessorEdit(result, newName));
		editList.add(edits.getAddProcessorEdit(d, result));
		em.doDataflowEdit(d, new CompoundEdit(editList));

		return result;
	}

	public Version.ID createInitialComponent(WorkflowBundle d, Version.ID ident)
			throws ComponentException {
		try {
			fm.saveDataflow(d, ComponentFileType.instance, ident, false);

			em.doDataflowEdit(d,
					edits.getUpdateDataflowNameEdit(d, d.getName()));
		} catch (OverwriteException|SaveException|IllegalStateException|EditException e) {
			throw new ComponentException(e);
		}
		return ident;
	}
}
