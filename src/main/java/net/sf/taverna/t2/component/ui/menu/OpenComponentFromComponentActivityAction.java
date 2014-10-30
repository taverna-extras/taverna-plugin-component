/**
 * 
 */
package net.sf.taverna.t2.component.ui.menu;

import static org.apache.log4j.Logger.getLogger;

import java.awt.event.ActionEvent;

import net.sf.taverna.t2.component.api.ComponentFactory;
import net.sf.taverna.t2.component.api.Version;
import net.sf.taverna.t2.component.ui.ComponentAction;
import net.sf.taverna.t2.component.ui.ComponentActivityConfigurationBean;
import net.sf.taverna.t2.component.ui.util.ComponentFileType;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.file.exceptions.OpenException;

import org.apache.log4j.Logger;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * @author alanrw
 */
@SuppressWarnings("serial")
public class OpenComponentFromComponentActivityAction extends ComponentAction {
	private static Logger logger = getLogger(OpenComponentFromComponentActivityAction.class);

	private final FileManager fileManager;
	private final ComponentFactory factory;

	public OpenComponentFromComponentActivityAction(FileManager fileManager,
			ComponentFactory factory) {
		super("Open component...");
		this.fileManager = fileManager;
		this.factory = factory;
	}

	private Activity selection;

	@Override
	public void actionPerformed(ActionEvent ev) {
		Version.ID ident = new ComponentActivityConfigurationBean(
				selection.getConfiguration(), factory);

		try {
			WorkflowBundle d = fileManager.openDataflow(
					ComponentFileType.instance, ident);
			markGraphAsBelongingToComponent(d);
		} catch (OpenException e) {
			logger.error("failed to open component", e);
		}
	}

	public void setSelection(Activity selection) {
		this.selection = selection;
	}
}
