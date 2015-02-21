package net.sf.taverna.t2.component.ui.view;

import static org.apache.taverna.component.api.config.ComponentConfig.URI;

import java.util.Arrays;
import java.util.List;

import org.apache.taverna.component.api.Version;

import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ContextualViewFactory;
import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public class ComponentContextViewFactory implements
		ContextualViewFactory<WorkflowBundle> {
	private FileManager fileManager;

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	@Override
	public boolean canHandle(Object selection) {
		if (selection instanceof WorkflowBundle) {
			Object dataflowSource = fileManager
					.getDataflowSource((WorkflowBundle) selection);
			//FIXME Is this right?
			return dataflowSource instanceof Version.ID;
		}
		return selection instanceof Activity
				&& ((Activity) selection).getType().equals(URI);
	}

	@Override
	public List<ContextualView> getViews(WorkflowBundle selection) {
		Object dataflowSource = fileManager.getDataflowSource(selection);
		return Arrays.<ContextualView> asList(new ComponentContextualView(
				(Version.ID) dataflowSource));
	}
}
