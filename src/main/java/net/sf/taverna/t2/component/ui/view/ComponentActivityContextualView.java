package net.sf.taverna.t2.component.ui.view;

import static org.apache.log4j.Logger.getLogger;

import java.awt.Frame;
import java.net.URL;
import java.util.List;

import javax.swing.Action;

import net.sf.taverna.t2.component.ComponentActivity;
import net.sf.taverna.t2.component.ComponentActivityConfigurationBean;
import net.sf.taverna.t2.component.api.Component;
import net.sf.taverna.t2.component.api.Family;
import net.sf.taverna.t2.component.api.ComponentException;
import net.sf.taverna.t2.component.api.Version;
import net.sf.taverna.t2.component.ui.config.ComponentConfigureAction;
import net.sf.taverna.t2.workbench.ui.actions.activity.HTMLBasedActivityContextualView;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class ComponentActivityContextualView extends
		HTMLBasedActivityContextualView {
	public ComponentActivityContextualView(ComponentActivity activity) {
		super(activity);
		init();
	}

	private void init() {
	}

	@Override
	public String getViewTitle() {
		return "Component service";
	}

	/**
	 * View position hint
	 */
	@Override
	public int getPreferredPosition() {
		// We want to be on top
		return 100;
	}

	@Override
	public Action getConfigureAction(Frame owner) {
		return new ComponentConfigureAction((ComponentActivity) getActivity(),
				owner);
	}

	@Override
	protected String getRawTableRowsHtml() {
		return ViewUtil.getRawTablesHtml(getConfigBean());
	}
}
