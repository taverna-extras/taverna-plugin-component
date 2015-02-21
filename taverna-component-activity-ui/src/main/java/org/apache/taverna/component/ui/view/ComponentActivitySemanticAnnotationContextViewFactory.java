package org.apache.taverna.component.ui.view;

import static org.apache.log4j.Logger.getLogger;
import static org.apache.taverna.component.api.config.ComponentConfig.URI;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.ComponentFactory;
import org.apache.taverna.component.api.profile.Profile;
import org.apache.taverna.component.ui.ComponentActivityConfigurationBean;
import org.apache.taverna.component.ui.annotation.AbstractSemanticAnnotationContextualView;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;
import uk.org.taverna.scufl2.api.port.Port;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ContextualViewFactory;

public class ComponentActivitySemanticAnnotationContextViewFactory implements
		ContextualViewFactory<Object> {
	public static final String VIEW_TITLE = "Inherited Semantic Annotations";
	private static final Logger logger = getLogger(ComponentActivitySemanticAnnotationContextViewFactory.class);

	private FileManager fm;
	private ComponentFactory factory;

	public void setFileManager(FileManager fm) {
		this.fm = fm;
	}

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
	}

	@Override
	public boolean canHandle(Object selection) {
		return getContainingComponentActivity(selection) != null;
	}

	public Activity getContainingComponentActivity(Object selection) {
		if (selection instanceof Activity) {
			Activity a = (Activity) selection;
			if (a.getType().equals(URI))
				return a;
		}
		if (selection instanceof InputActivityPort
				|| selection instanceof OutputActivityPort)
			return getContainingComponentActivity(((OutputActivityPort) selection)
					.getParent());
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
			super(fm, false);
			Activity componentActivity = getContainingComponentActivity(selection);
			try {
				ComponentActivityConfigurationBean configuration = new ComponentActivityConfigurationBean(
						componentActivity.getConfiguration(), factory);
				setAnnotatedThing(selection, configuration.getVersion()
						.getImplementation().getMainWorkflow());
				componentProfile = configuration.getComponent().getFamily()
						.getComponentProfile();
				setProfile(selection);
				super.initialise();
			} catch (ComponentException e) {
				logger.error("problem querying registry", e);
			} catch (MalformedURLException e) {
				logger.error("malformed URL in component description", e);
			}
		}

		private void setAnnotatedThing(Object selection,
				Workflow underlyingDataflow) {
			if (selection instanceof Activity) {
				setAnnotated(underlyingDataflow);
			} else if (selection instanceof InputActivityPort) {
				String name = ((Port) selection).getName();
				for (InputWorkflowPort dip : underlyingDataflow.getInputPorts())
					if (dip.getName().equals(name)) {
						setAnnotated(dip);
						break;
					}
			} else if (selection instanceof OutputActivityPort) {
				String name = ((Port) selection).getName();
				for (OutputWorkflowPort dop : underlyingDataflow
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
			if (selection instanceof Activity) {
				setSemanticAnnotationProfiles(componentProfile
						.getSemanticAnnotations());
			} else if (selection instanceof InputActivityPort) {
				setSemanticAnnotationProfiles(componentProfile
						.getInputSemanticAnnotationProfiles());
			} else if (selection instanceof OutputActivityPort) {
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
