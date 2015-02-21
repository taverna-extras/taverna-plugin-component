/**
 * 
 */
package org.apache.taverna.component.ui.menu.family;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.WEST;
import static java.lang.String.format;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.apache.log4j.Logger.getLogger;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import net.sf.taverna.t2.workbench.file.FileManager;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.Family;
import org.apache.taverna.component.api.Registry;
import org.apache.taverna.component.api.Version;
import org.apache.taverna.component.ui.panel.FamilyChooserPanel;
import org.apache.taverna.component.ui.panel.RegistryChooserPanel;
import org.apache.taverna.component.ui.preference.ComponentPreference;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceProviderConfig;
import org.apache.taverna.component.ui.util.Utils;

import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;

/**
 * @author alanrw
 */
public class ComponentFamilyDeleteAction extends AbstractAction {
	private static final String CONFIRM_MSG = "Are you sure you want to delete %s";
	private static final String CONFIRM_TITLE = "Delete Component Family Confirmation";
	private static final String DELETE_FAMILY_LABEL = "Delete family...";
	private static final String ERROR_TITLE = "Component Family Deletion Error";
	private static final String FAILED_MSG = "Unable to delete %s: %s";
	private static final String FAMILY_FAIL_TITLE = "Component Family Problem";
	private static final String OPEN_MSG = "Components in the family are open";
	private static final String PICK_FAMILY_TITLE = "Delete Component Family";
	private static final String REGISTRY_FAIL_TITLE = "Component Registry Problem";
	private static final String WHAT_FAMILY_MSG = "Unable to determine family";
	private static final String WHAT_REGISTRY_MSG = "Unable to determine registry";
	private static final Logger logger = getLogger(ComponentFamilyDeleteAction.class);
	private static final long serialVersionUID = -4976161883778371344L;

	private final FileManager fm;
	private final ComponentPreference prefs;
	private final Utils utils;

	public ComponentFamilyDeleteAction(FileManager fm,
			ComponentPreference prefs, ComponentServiceIcon icon, Utils utils) {
		super(DELETE_FAMILY_LABEL, icon.getIcon());
		this.fm = fm;
		this.prefs = prefs;
		this.utils = utils;
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		JPanel overallPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		RegistryChooserPanel registryPanel = new RegistryChooserPanel(prefs);

		gbc.insets = new Insets(0, 5, 0, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = WEST;
		gbc.fill = BOTH;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		overallPanel.add(registryPanel, gbc);

		FamilyChooserPanel familyPanel = new FamilyChooserPanel(registryPanel);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weighty = 1;
		overallPanel.add(familyPanel, gbc);

		int answer = showConfirmDialog(null, overallPanel, PICK_FAMILY_TITLE,
				OK_CANCEL_OPTION);
		if (answer == OK_OPTION)
			deletionActionFlow(registryPanel.getChosenRegistry(),
					familyPanel.getChosenFamily());
	}

	/**
	 * Check if the preconditions for the deletion action are satisfied.
	 * 
	 * @param chosenRegistry
	 *            What registry contains the family.
	 * @param chosenFamily
	 */
	private void deletionActionFlow(Registry chosenRegistry,
			final Family chosenFamily) {
		if (chosenRegistry == null) {
			showMessageDialog(null, WHAT_REGISTRY_MSG, REGISTRY_FAIL_TITLE,
					ERROR_MESSAGE);
			return;
		} else if (chosenFamily == null) {
			showMessageDialog(null, WHAT_FAMILY_MSG, FAMILY_FAIL_TITLE,
					ERROR_MESSAGE);
			return;
		} else if (familyIsInUse(chosenRegistry, chosenFamily)) {
			showMessageDialog(null, OPEN_MSG, FAMILY_FAIL_TITLE, ERROR_MESSAGE);
			return;
		} else if (showConfirmDialog(null,
				format(CONFIRM_MSG, chosenFamily.getName()), CONFIRM_TITLE,
				YES_NO_OPTION) == YES_OPTION)
			new SwingWorker<ComponentServiceProviderConfig, Object>() {
				@Override
				protected ComponentServiceProviderConfig doInBackground()
						throws Exception {
					return deleteFamily(chosenFamily);
				}

				@Override
				protected void done() {
					deletionDone(chosenFamily, this);
				}
			}.execute();
	}

	private ComponentServiceProviderConfig deleteFamily(Family family)
			throws ComponentException {
		ComponentServiceProviderConfig config = new ComponentServiceProviderConfig(
				family);
		family.delete();
		return config;
	}

	private void deletionDone(Family family,
			SwingWorker<ComponentServiceProviderConfig, Object> worker) {
		Configuration config;
		try {
		config = worker.get().getConfiguration();
		} catch (InterruptedException e) {
			logger.warn("interrupted during removal of component family", e);
			return;
		} catch (ExecutionException e) {
			logger.error("failed to delete family", e.getCause());
			showMessageDialog(
					null,
					format(FAILED_MSG, family.getName(), e.getCause()
							.getMessage()), ERROR_TITLE, ERROR_MESSAGE);
			return;
		}
		try {
			utils.removeComponentServiceProvider(config);
		} catch (Exception e) {
			logger.error("failed to update service provider panel "
					+ "after deleting family", e);
		}
	}

	private boolean familyIsInUse(Registry chosenRegistry, Family chosenFamily) {
		for (WorkflowBundle d : fm.getOpenDataflows()) {
			Object dataflowSource = fm.getDataflowSource(d);
			if (dataflowSource instanceof Version.ID) {
				Version.ID ident = (Version.ID) dataflowSource;
				if (ident.getRegistryBase().toString()
						.equals(chosenRegistry.getRegistryBase().toString())
						&& ident.getFamilyName().equals(chosenFamily.getName()))
					return true;
			}
		}
		return false;
	}
}
