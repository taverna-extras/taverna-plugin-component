/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.component.ui.view;

import static org.apache.taverna.component.api.config.ComponentConfig.URI;

import java.awt.Frame;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;

import org.apache.taverna.component.api.ComponentFactory;
import org.apache.taverna.component.ui.config.ComponentConfigureAction;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.services.ServiceRegistry;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.configuration.colour.ColourManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.ui.actions.activity.HTMLBasedActivityContextualView;
import org.apache.taverna.workbench.ui.views.contextualviews.ContextualView;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.ContextualViewFactory;

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
