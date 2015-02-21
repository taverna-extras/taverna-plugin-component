package org.apache.taverna.component.ui.view;

import static org.apache.taverna.component.api.config.ComponentConfig.URI;

import java.awt.Frame;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;

import org.apache.taverna.component.api.ComponentFactory;
import org.apache.taverna.component.ui.config.ComponentConfigureAction;

import uk.org.taverna.commons.services.ServiceRegistry;
import org.apache.taverna.scufl2.api.activity.Activity;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.configuration.colour.ColourManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.actions.activity.HTMLBasedActivityContextualView;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ContextualViewFactory;

public class ComponentActivityContextViewFactory implements
		ContextualViewFactory<Activity> {
	private ColourManager colourManager;
	private ViewUtil util;
	private ComponentFactory factory;
	private ActivityIconManager aim;
	private ServiceDescriptionRegistry sdr;
	private EditManager em;
	private FileManager fm;
	private ServiceRegistry sr;

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
	}

	public void setColourManager(ColourManager colourManager) {
		this.colourManager = colourManager;
	}

	public void setViewUtils(ViewUtil util) {
		this.util = util;
	}

	public void setIconManager(ActivityIconManager aim) {
		this.aim = aim;
	}

	public void setServiceDescriptionRegistry(ServiceDescriptionRegistry sdr) {
		this.sdr = sdr;
	}

	public void setEditManager(EditManager em) {
		this.em = em;
	}

	public void setFileManager(FileManager fm) {
		this.fm = fm;
	}

	public void setServiceTypeRegistry(ServiceRegistry sr) {
		this.sr = sr;
	}

	@Override
	public boolean canHandle(Object selection) {
		return selection instanceof Activity
				&& ((Activity) selection).getType().equals(URI);
	}

	@Override
	public List<ContextualView> getViews(Activity selection) {
		return Arrays.<ContextualView>asList(new ComponentActivityContextualView(selection));
	}

	@SuppressWarnings("serial")
	private class ComponentActivityContextualView extends
			HTMLBasedActivityContextualView {
		public ComponentActivityContextualView(Activity activity) {
			super(activity, colourManager);
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
			return new ComponentConfigureAction(getActivity(), owner, factory,
					aim, sdr, em, fm, sr);
		}

		@Override
		protected String getRawTableRowsHtml() {
			try {
				return util.getRawTablesHtml(getConfigBean());
			} catch (MalformedURLException e) {
				return "<tr><td>malformed URL: <pre>" + e.getMessage()
						+ "</pre></td></tr>";
			}
		}
	}
}
