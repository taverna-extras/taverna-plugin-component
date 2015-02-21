/**
 * 
 */
package org.apache.taverna.component.ui.menu;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.awt.BorderLayout.SOUTH;
import static java.util.Collections.sort;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.apache.log4j.Logger.getLogger;
import static org.apache.taverna.component.ui.util.Utils.uniqueName;
import static uk.org.taverna.scufl2.api.common.Scufl2Tools.NESTED_WORKFLOW;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import net.sf.taverna.t2.lang.ui.DeselectingButton;
import net.sf.taverna.t2.workbench.edits.CompoundEdit;
import net.sf.taverna.t2.workbench.edits.Edit;
import net.sf.taverna.t2.workbench.edits.EditException;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.helper.HelpEnabledDialog;
import net.sf.taverna.t2.workbench.models.graph.GraphController;
import net.sf.taverna.t2.workbench.views.graph.GraphViewComponent;
import net.sf.taverna.t2.workflow.edits.AddActivityEdit;
import net.sf.taverna.t2.workflow.edits.AddActivityInputPortMappingEdit;
import net.sf.taverna.t2.workflow.edits.AddActivityOutputPortMappingEdit;
import net.sf.taverna.t2.workflow.edits.AddChildEdit;
import net.sf.taverna.t2.workflow.edits.AddDataLinkEdit;
import net.sf.taverna.t2.workflow.edits.AddProcessorInputPortEdit;
import net.sf.taverna.t2.workflow.edits.AddProcessorOutputPortEdit;
import net.sf.taverna.t2.workflow.edits.AddWorkflowInputPortEdit;
import net.sf.taverna.t2.workflow.edits.AddWorkflowOutputPortEdit;
import net.sf.taverna.t2.workflow.edits.RemoveChildEdit;
import net.sf.taverna.t2.workflow.edits.RemoveDataLinkEdit;
import net.sf.taverna.t2.workflow.edits.SetIterationStrategyStackEdit;

import org.apache.log4j.Logger;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.annotation.Annotation;
import uk.org.taverna.scufl2.api.common.Named;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.BlockingControlLink;
import uk.org.taverna.scufl2.api.core.ControlLink;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;
import uk.org.taverna.scufl2.api.port.ProcessorPort;
import uk.org.taverna.scufl2.api.port.ReceiverPort;
import uk.org.taverna.scufl2.api.port.SenderPort;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * @author alanrw
 */
public class NestedWorkflowCreationDialog extends HelpEnabledDialog {
	private static final long serialVersionUID = 727059218457420449L;
	private static final Logger logger = getLogger(NestedWorkflowCreationDialog.class);
	private static final Comparator<Processor> processorComparator = new Comparator<Processor>() {
		@Override
		public int compare(Processor o1, Processor o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};
	private static final ListCellRenderer<Object> defaultRenderer = new DefaultListCellRenderer();
	private static final ListCellRenderer<Processor> processorRenderer = new ListCellRenderer<Processor>() {
		@Override
		public Component getListCellRendererComponent(
				JList<? extends Processor> list,
				Processor value, int index, boolean isSelected,
				boolean cellHasFocus) {
			return defaultRenderer.getListCellRendererComponent(list,
					value.getName(), index, isSelected, cellHasFocus);
		}
	};

	private final EditManager em;
	private final GraphViewComponent graphView;
	private final List<Processor> includedProcessors = new ArrayList<>();
	private List<Processor> allProcessors;
	private final List<Processor> includableProcessors = new ArrayList<>();

	private JList<Processor> includableList = new JList<>();
	private JList<Processor> includedList = new JList<>();
	private final Workflow currentDataflow;
	private JButton excludeButton;
	private JButton includeButton;
	private JButton okButton;
	private JButton resetButton;
	private JTextField nameField = new JTextField(30);

	public NestedWorkflowCreationDialog(Frame owner, Object o,
			Workflow dataflow, EditManager em, GraphViewComponent graphView) {
		super(owner, "Nested workflow creation", true, null);
		this.em = em;
		this.graphView = graphView;

		if (o instanceof Processor)
			includedProcessors.add((Processor) o);
		this.currentDataflow = dataflow;

		allProcessors = new ArrayList<>(dataflow.getProcessors());

		this.setLayout(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());

		okButton = new DeselectingButton(new OKAction(this));
		buttonPanel.add(okButton);

		resetButton = new DeselectingButton(new ResetAction(this));
		buttonPanel.add(resetButton);

		JButton cancelButton = new DeselectingButton(new CancelAction(this));
		buttonPanel.add(cancelButton);

		JPanel innerPanel = new JPanel(new BorderLayout());
		JPanel processorChoice = createProcessorChoicePanel(dataflow);
		innerPanel.add(processorChoice, CENTER);

		JPanel namePanel = new JPanel(new FlowLayout());
		namePanel.add(new JLabel("Workflow name: "));
		nameField.setText("nested");
		namePanel.add(nameField);
		innerPanel.add(namePanel, SOUTH);

		this.add(innerPanel, CENTER);

		this.add(buttonPanel, SOUTH);
		this.pack();
		this.setSize(new Dimension(500, 800));
	}

	private JPanel createProcessorChoicePanel(Workflow dataflow) {
		JPanel result = new JPanel();
		result.setLayout(new GridLayout(0, 2));

		JPanel includedProcessorsPanel = createIncludedProcessorsPanel();
		JPanel includableProcessorsPanel = createIncludableProcessorsPanel();
		result.add(includableProcessorsPanel);
		result.add(includedProcessorsPanel);
		updateLists();
		return result;
	}

	private JPanel createIncludableProcessorsPanel() {
		JPanel result = new JPanel();
		result.setLayout(new BorderLayout());
		result.add(new JLabel("Possible services"), NORTH);
		includableList.setModel(new DefaultComboBoxModel<>(new Vector<>(
				includableProcessors)));
		includableList.setCellRenderer(processorRenderer);
		result.add(new JScrollPane(includableList), CENTER);

		includeButton = new DeselectingButton("Include", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				includedProcessors.addAll(includableList
						.getSelectedValuesList());
				calculateIncludableProcessors();
				updateLists();
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(includeButton);
		result.add(buttonPanel, SOUTH);
		return result;
	}

	private void resetLists() {
		includedProcessors.clear();
		updateLists();
	}

	private JPanel createIncludedProcessorsPanel() {
		JPanel result = new JPanel();
		result.setLayout(new BorderLayout());
		result.add(new JLabel("Included services"), NORTH);
		includedList.setModel(new DefaultComboBoxModel<>(new Vector<>(
				includedProcessors)));
		includedList.setCellRenderer(processorRenderer);
		result.add(new JScrollPane(includedList), CENTER);

		excludeButton = new DeselectingButton("Exclude", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				includedProcessors.removeAll(includedList
						.getSelectedValuesList());
				calculateIncludableProcessors();
				updateLists();
			}
		});
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(excludeButton);

		result.add(buttonPanel, SOUTH);
		return result;
	}

	private void updateLists() {
		calculateIncludableProcessors();
		sort(includedProcessors, processorComparator);
		sort(includableProcessors, processorComparator);
		includedList.setModel(new DefaultComboBoxModel<>(new Vector<>(
				includedProcessors)));
		includableList.setModel(new DefaultComboBoxModel<>(new Vector<>(
				includableProcessors)));
		boolean someIncludedProcessors = includedProcessors.size() > 0;
		excludeButton.setEnabled(someIncludedProcessors);
		okButton.setEnabled(someIncludedProcessors);
		resetButton.setEnabled(someIncludedProcessors);
		boolean someIncludableProcessors = includableProcessors.size() > 0;
		includeButton.setEnabled(someIncludableProcessors);
	}

	public void calculateIncludableProcessors() {
		includableProcessors.clear();
		if (includedProcessors.isEmpty())
			includableProcessors.addAll(allProcessors);
		else
			for (Processor p : includedProcessors) {
				considerNearestUpstream(p);
				considerNearestDownstream(p);
			}
		sort(includableProcessors, processorComparator);
	}

	private void considerNearestDownstream(Processor investigate) {
		for (BlockingControlLink condition : investigate.controlLinksWaitingFor())
			considerInclusion(condition.getBlock());

		for (OutputProcessorPort outputPort : investigate.getOutputPorts())
			for (DataLink datalink : outputPort.getDatalinksFrom()) {
				ReceiverPort sink = datalink.getSendsTo();
				if (sink instanceof InputProcessorPort)
					considerInclusion(((InputProcessorPort) sink).getParent());
			}
	}

	private void considerNearestUpstream(Processor investigate) {
		for (BlockingControlLink condition : investigate.controlLinksBlocking())
			considerInclusion(condition.getUntilFinished());
		for (InputProcessorPort inputPort : investigate.getInputPorts())
			for (DataLink incomingLink : inputPort.getDatalinksTo()) {
				if (incomingLink == null)
					continue;
				SenderPort source = incomingLink.getReceivesFrom();
				if (source instanceof OutputProcessorPort)
					considerInclusion(((OutputProcessorPort) source).getParent());
		}
	}

	private void considerInclusion(Processor p) {
		if (!includedProcessors.contains(p)
				&& !includableProcessors.contains(p))
			includableProcessors.add(p);
	}

	private void createNestedWorkflow() {
		final List<Edit<?>> currentWorkflowEditList = new ArrayList<>();
		Map<Object, Object> oldNewMapping = new HashMap<>();
		Map<DataLink, String> linkProcessorPortMapping = new HashMap<>();
		Map<SenderPort, OutputWorkflowPort> outputPortMap = new HashMap<>();
		Map<ReceiverPort, InputWorkflowPort> inputPortMap = new HashMap<>();

		Profile profile;//FIXME
		Processor nestingProcessor = createNestingProcessor(currentWorkflowEditList);
		Workflow nestedDataflow = createNestedDataflow();

		transferProcessors(currentWorkflowEditList, oldNewMapping,
				nestedDataflow);
		transferDatalinks(oldNewMapping, linkProcessorPortMapping,
				outputPortMap, inputPortMap, nestedDataflow);
		transferConditions(currentWorkflowEditList, oldNewMapping,
				nestingProcessor);
		addDataflowToNestingProcessor(nestingProcessor, nestedDataflow, profile);
		currentWorkflowEditList.add(new AddChildEdit<>(currentDataflow,
				nestingProcessor));
		createDatalinkEdits(currentWorkflowEditList, oldNewMapping,
				linkProcessorPortMapping, nestingProcessor);

		try {
			GraphController gc = graphView.getGraphController(currentDataflow);
			gc.setExpandNestedDataflow(nestingProcessor.getActivity(profile), true);
			em.doDataflowEdit(currentDataflow.getParent(), new CompoundEdit(
					currentWorkflowEditList));
			gc.redraw();
		} catch (EditException e1) {
			logger.error("failed to manufacture nested workflow", e1);
		}
	}

	private void addDataflowToNestingProcessor(Processor nestingProcessor,
			Workflow nestedDataflow, Profile profile) {
		Activity da = new Activity();
		da.setParent(profile);
		da.createConfiguration(NESTED_WORKFLOW).getJsonAsObjectNode()
				.put("nestedWorkflow", nestedDataflow.getName());
		try {
			new AddActivityEdit(nestingProcessor, da).doEdit();
			new SetIterationStrategyStackEdit(nestingProcessor, null/*FIXME*/).doEdit();
			for (InputActivityPort aip : da.getInputPorts()) {
				InputProcessorPort pip = new InputProcessorPort();
				pip.setName(aip.getName());
				pip.setDepth(aip.getDepth());
				new AddProcessorInputPortEdit(nestingProcessor, pip).doEdit();
				new AddActivityInputPortMappingEdit(da, pip, aip).doEdit();
			}
			for (OutputActivityPort aop : da.getOutputPorts()) {
				OutputProcessorPort pop = new OutputProcessorPort();
				pop.setName(aop.getName());
				pop.setDepth(aop.getDepth());
				pop.setGranularDepth(aop.getGranularDepth());
				new AddProcessorOutputPortEdit(nestingProcessor, pop).doEdit();
				new AddActivityOutputPortMappingEdit(da, pop, aop).doEdit();
			}
		} catch (EditException e1) {
			logger.error("failed to add ports to processor", e1);
		}
	}

	private void createDatalinkEdits(List<Edit<?>> editList,
			Map<Object, Object> oldNewMapping,
			Map<DataLink, String> linkProcessorPortMapping,
			Processor nestingProcessor) {
		for (DataLink dl : currentDataflow.getDataLinks())
			if (oldNewMapping.containsKey(dl.getReceivesFrom())
					&& oldNewMapping.containsKey(dl.getSendsTo()))
				// Internal to nested workflow
				editList.add(new RemoveDataLinkEdit(dl.getParent(), dl));
			else if (oldNewMapping.containsKey(dl.getReceivesFrom())) {
				// Coming out of nested workflow
				OutputProcessorPort nestedPort = nestingProcessor
						.getOutputPorts().getByName(
								linkProcessorPortMapping.get(dl));
				if (nestedPort != null) {
					DataLink replacementDatalink = new DataLink(nestedPort
							.getParent().getParent(), nestedPort,
							dl.getSendsTo());
					editList.add(new RemoveDataLinkEdit(dl.getParent(), dl));
					editList.add(new AddDataLinkEdit(nestedPort.getParent()
							.getParent(), replacementDatalink));
				}
			} else if (oldNewMapping.containsKey(dl.getSendsTo())) {
				// Coming into nested workflow
				InputProcessorPort nestedPort = nestingProcessor
						.getInputPorts().getByName(
								linkProcessorPortMapping.get(dl));
				if (nestedPort != null) {
					DataLink replacementDatalink = new DataLink(nestedPort
							.getParent().getParent(), dl.getReceivesFrom(),
							nestedPort);
					editList.add(new RemoveDataLinkEdit(dl.getParent(), dl));
					editList.add(new AddDataLinkEdit(nestedPort.getParent()
							.getParent(), replacementDatalink));
				}
			}
	}

	private void transferConditions(List<Edit<?>> editList,
			Map<Object, Object> oldNewMapping, Processor nestingProcessor) {
		for (Processor p : currentDataflow.getProcessors()) {
			boolean isTargetMoved = oldNewMapping.containsKey(p);
			for (BlockingControlLink c : p.controlLinksWaitingFor()) {
				Processor pre = c.getUntilFinished();
				boolean isControlMoved = oldNewMapping.containsKey(pre);
				if (isTargetMoved && isControlMoved) {
					// Add in new condition
					new BlockingControlLink(
							(Processor) oldNewMapping.get(pre),
							(Processor) oldNewMapping.get(p));
				} else if (isTargetMoved) {
					editList.add(new RemoveChildEdit<>(c.getParent(),c));
					editList.add(new AddChildEdit<>(c.getParent(),
							new BlockingControlLink(pre, nestingProcessor)));
				} else if (isControlMoved) {
					editList.add(new RemoveChildEdit<>(c.getParent(), c));
					editList.add(new AddChildEdit<>(c.getParent(),
							new BlockingControlLink(nestingProcessor, p)));
				}
			}
		}
	}

	private void transferDatalinks(Map<Object, Object> oldNewMapping,
			Map<DataLink, String> linkProcessorPortMapping,
			Map<SenderPort, OutputWorkflowPort> outputPortMap,
			Map<ReceiverPort, InputWorkflowPort> inputPortMap,
			Workflow nestedDataflow) {
		NamedSet<InputWorkflowPort> inputPorts = new NamedSet<>();
		NamedSet<OutputWorkflowPort> outputPorts = new NamedSet<>();

		for (DataLink dl : currentDataflow.getDataLinks()) {
			final SenderPort datalinkSource = dl.getReceivesFrom();
			final ReceiverPort datalinkSink = dl.getSendsTo();
			if (oldNewMapping.containsKey(datalinkSource)
					&& oldNewMapping.containsKey(datalinkSink)) {
				// Internal to nested workflow
				DataLink newDatalink = new DataLink(null,
						(SenderPort) oldNewMapping.get(datalinkSource),
						(ReceiverPort) oldNewMapping.get(datalinkSink));
				try {
					new AddDataLinkEdit(nestedDataflow, newDatalink).doEdit();
				} catch (EditException e1) {
					logger.error("failed to connect datalink", e1);
				}
			} else if (oldNewMapping.containsKey(datalinkSource)) {
				OutputWorkflowPort dop = null;
				if (!outputPortMap.containsKey(datalinkSource)) {
					dop = new OutputWorkflowPort(nestedDataflow, uniqueName(
							datalinkSource.getName(), outputPorts));
					outputPorts.add(dop);
					outputPortMap.put(datalinkSource, dop);
				} else
					dop = outputPortMap.get(datalinkSource);
				String portName = dop.getName();
				// Coming out of nested workflow
				linkProcessorPortMapping.put(dl, portName);
				try {
					new AddWorkflowOutputPortEdit(nestedDataflow, dop).doEdit();
					DataLink newDatalink = new DataLink(
							(SenderPort) oldNewMapping.get(datalinkSource),
							dop.getInternalInputPort());
					new AddDataLinkEdit(nestedDataflow, newDatalink).doEdit();
				} catch (EditException e1) {
					logger.error("failed to add dataflow output", e1);
				}
			} else if (oldNewMapping.containsKey(datalinkSink)) {
				InputWorkflowPort dip = null;
				if (!inputPortMap.containsKey(datalinkSink)) {
					dip = new InputWorkflowPort(nestedDataflow, uniqueName(
							datalinkSink.getName(), inputPorts));
					inputPorts.add(dip);
					dip.setDepth(dl.getResolvedDepth());
					inputPortMap.put(datalinkSink, dip);
				} else
					dip = inputPortMap.get(datalinkSink);
				String portName = dip.getName();
				// Coming into nested workflow
				linkProcessorPortMapping.put(dl, portName);
				try {
					new AddWorkflowInputPortEdit(nestedDataflow, dip).doEdit();
					DataLink newDatalink = new DataLink(
							dip.getInternalOutputPort(),
							(ReceiverPort) oldNewMapping.get(datalinkSink));
					new AddDataLinkEdit(nestedDataflow, newDatalink).doEdit();
				} catch (EditException e1) {
					logger.error("failed to add dataflow input", e1);
				}
			}
		}
	}

	private void transferProcessors(List<Edit<?>> editList,
			Map<Object, Object> oldNewMapping, Workflow nestedDataflow) {
		for (Processor entity : includedProcessors)
			try {
				if (entity instanceof Processor)
					transferProcessor(editList, oldNewMapping, nestedDataflow,
							(Processor) entity);
				/*else if (entity instanceof Merge)
					//FIXME what to do here? Anything?
					transferMerge(editList, oldNewMapping, nestedDataflow,
							(Merge) entity);*/
			} catch (Exception e1) {
				logger.error("failed to transfer processor", e1);
			}
	}

	/*private void transferMerge(List<Edit<?>> editList,
			Map<Object, Object> oldNewMapping, Workflow nestedDataflow,
			Merge merge) throws EditException {
		editList.add(edits.getRemoveMergeEdit(currentDataflow, merge));
		Merge newMerge = edits.createMerge(nestedDataflow);
		edits.getAddMergeEdit(nestedDataflow, newMerge).doEdit();
		oldNewMapping.put(merge, newMerge);
		for (MergeInputPort mip : merge.getInputPorts()) {
			MergeInputPort newMip = edits.createMergeInputPort(newMerge,
					mip.getName(), mip.getDepth());
			edits.getAddMergeInputPortEdit(newMerge, newMip).doEdit();
			oldNewMapping.put(mip, newMip);
		}
		oldNewMapping.put(merge.getOutputPort(), newMerge.getOutputPort());
	}*/

	private void transferProcessor(List<Edit<?>> editList,
			Map<Object, Object> oldNewMapping, Workflow nestedDataflow,
			Processor p) throws Exception {
		editList.add(new RemoveChildEdit<>(currentDataflow, p));
		Processor newProcessor = (Processor) p.clone();
		newProcessor.setParent(nestedDataflow);
		oldNewMapping.put(p, newProcessor);
		for (InputProcessorPort pip : p.getInputPorts())
			for (InputProcessorPort newPip : newProcessor.getInputPorts())
				if (pip.getName().equals(newPip.getName())) {
					oldNewMapping.put(pip, newPip);
					break;
				}
		for (OutputProcessorPort pop : p.getOutputPorts())
			for (OutputProcessorPort newPop : newProcessor.getOutputPorts())
				if (pop.getName().equals(newPop.getName())) {
					oldNewMapping.put(pop, newPop);
					break;
				}
	}

	private Processor createNestingProcessor(List<Edit<?>> editList) {
		//TODO check what workflow the new processor is going into
		Processor nestingProcessor = new Processor(currentDataflow, uniqueName(
				nameField.getText(), currentDataflow.getProcessors()));
		if (includedProcessors.size() != 1)
			return nestingProcessor;
		Processor includedProcessor = includedProcessors.get(0);
		for (Annotation a: includedProcessor.getAnnotations()) {
			Annotation newAnn = (Annotation) a.clone();
			newAnn.setTarget(nestingProcessor);
			editList.add(new AddChildEdit<>(a.getParent(), newAnn));
		}
		return nestingProcessor;
	}

	private Workflow createNestedDataflow() {
		Workflow nestedDataflow = new Workflow(uniqueName(nameField.getText(),
				currentDataflow.getParent().getWorkflows()));
		// Set the title of the nested workflow to the name suggested by the user
		try {
			new AnnotationTools().setAnnotationString(nestedDataflow,
					DescriptiveTitle.class, nameField.getText()).doEdit();
		} catch (EditException ex) {
			logger.error("failed to put annotation on nested dataflow", ex);
		}
		return nestedDataflow;
	}

	private final class OKAction extends AbstractAction {
		private static final long serialVersionUID = 6516891432445682857L;
		private final JDialog dialog;

		private OKAction(JDialog dialog) {
			super("OK");
			this.dialog = dialog;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (includedProcessors.isEmpty()) {
				showMessageDialog(
						null,
						"At least one service must be included in the nested workflow",
						"Nested workflow creation", WARNING_MESSAGE);
				return;
			}

			createNestedWorkflow();
			dialog.setVisible(false);
		}
	}

	private final class ResetAction extends AbstractAction {
		private static final long serialVersionUID = 7296742769289881218L;

		private ResetAction(JDialog dialog) {
			super("Reset");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			resetLists();
		}
	}

	private final class CancelAction extends AbstractAction {
		private static final long serialVersionUID = -7842176979437027091L;
		private final JDialog dialog;

		private CancelAction(JDialog dialog) {
			super("Cancel");
			this.dialog = dialog;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			dialog.setVisible(false);
		}
	}
}
