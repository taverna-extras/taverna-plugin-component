package net.sf.taverna.t2.component.ui.view;

import static net.sf.taverna.t2.workflowmodel.utils.Tools.getFirstProcessorWithActivityInputPort;
import static net.sf.taverna.t2.workflowmodel.utils.Tools.getFirstProcessorWithActivityOutputPort;
import static org.apache.log4j.Logger.getLogger;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import net.sf.taverna.t2.component.annotation.AbstractSemanticAnnotationContextualView;
import net.sf.taverna.t2.component.api.ComponentException;
import net.sf.taverna.t2.component.api.profile.Profile;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ContextualViewFactory;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;
import net.sf.taverna.t2.workflowmodel.DataflowOutputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityInputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityOutputPort;

public class ComponentActivitySemanticAnnotationContextViewFactory implements
		ContextualViewFactory<Object> {
	private static final long serialVersionUID = 7403728889085410126L;
	public static final String VIEW_TITLE = "Inherited Semantic Annotations";
	private static final Logger logger = getLogger(ComponentActivitySemanticAnnotationContextViewFactory.class);

	private FileManager fm;// FIXME beaninject

	@Override
	public boolean canHandle(Object selection) {
		return getContainingComponentActivity(selection) != null;
	}

	public Activity getContainingComponentActivity(Object selection) {
		if (selection instanceof ComponentActivity)
			return (ComponentActivity) selection;

		if (selection instanceof ActivityInputPort) {
			Processor p = null;
			WorkflowBundle d = fm.getCurrentDataflow();
			p = getFirstProcessorWithActivityInputPort(d,
					(ActivityInputPort) selection);
			Activity a = p.getActivityList().get(0);
			return getContainingComponentActivity(a);
		}
		if (selection instanceof ActivityOutputPort) {
			Processor p = null;
			WorkflowBundle d = fm.getCurrentDataflow();
			p = getFirstProcessorWithActivityOutputPort(d,
					(ActivityOutputPort) selection);
			Activity a = p.getActivityList().get(0);
			return getContainingComponentActivity(a);
		}
		return null;
	}

	@Override
	public List<ContextualView> getViews(Object selection) {
		return Arrays
				.<ContextualView> asList(new SemanticAnnotationCV(
						selection));
	}

	@SuppressWarnings("serial")
	private class SemanticAnnotationCV extends
			AbstractSemanticAnnotationContextualView {
		private Profile componentProfile;

		public SemanticAnnotationCV(Object selection) {
			super(false);
			//FIXME! This is wrong entirely
			Activity componentActivity = getContainingComponentActivity(selection);
			ComponentActivityConfigurationBean configuration = componentActivity
					.getConfiguration();
			Dataflow underlyingDataflow;
			try {
				underlyingDataflow = getDataflow(configuration);
				setAnnotatedThing(selection, underlyingDataflow);
				componentProfile = calculateFamily(
						configuration.getRegistryBase(),
						configuration.getFamilyName()).getComponentProfile();
				setProfile(selection);
				super.initialise();
			} catch (ComponentException e) {
				logger.error("problem querying registry", e);
			}
		}

		private void setAnnotatedThing(Object selection,
				Dataflow underlyingDataflow) {
			if (selection instanceof ComponentActivity) {
				setAnnotated(underlyingDataflow);
			} else if (selection instanceof ActivityInputPort) {
				String name = ((ActivityInputPort) selection).getName();
				for (DataflowInputPort dip : underlyingDataflow.getInputPorts())
					if (dip.getName().equals(name)) {
						setAnnotated(dip);
						break;
					}
			} else if (selection instanceof ActivityOutputPort) {
				String name = ((ActivityOutputPort) selection).getName();
				for (DataflowOutputPort dop : underlyingDataflow
						.getOutputPorts())
					if (dop.getName().equals(name)) {
						setAnnotated(dop);
						break;
					}
			}
		}

		private void setProfile(Object selection) throws ComponentException {
			if (componentProfile == null)
				return;
			if (selection instanceof ComponentActivity) {
				setSemanticAnnotationProfiles(componentProfile
						.getSemanticAnnotations());
			} else if (selection instanceof ActivityInputPort) {
				setSemanticAnnotationProfiles(componentProfile
						.getInputSemanticAnnotationProfiles());
			} else if (selection instanceof ActivityOutputPort) {
				setSemanticAnnotationProfiles(componentProfile
						.getOutputSemanticAnnotationProfiles());
			}
		}
		
		@Override
		public String getViewTitle() {
			return VIEW_TITLE;
		}
	}
}
