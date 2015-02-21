/**
 *
 */
package org.apache.taverna.component.ui.menu.family;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.WEST;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.apache.log4j.Logger.getLogger;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.License;
import org.apache.taverna.component.api.Registry;
import org.apache.taverna.component.api.SharingPolicy;
import org.apache.taverna.component.api.profile.Profile;
import org.apache.taverna.component.ui.panel.LicenseChooserPanel;
import org.apache.taverna.component.ui.panel.ProfileChooserPanel;
import org.apache.taverna.component.ui.panel.RegistryChooserPanel;
import org.apache.taverna.component.ui.panel.SharingPolicyChooserPanel;
import org.apache.taverna.component.ui.preference.ComponentPreference;
import org.apache.taverna.component.ui.serviceprovider.ComponentServiceIcon;

/**
 * @author alanrw
 */
public class ComponentFamilyCreateAction extends AbstractAction {
	private static final long serialVersionUID = -7780471499146286881L;
	private static final Logger logger = getLogger(ComponentFamilyCreateAction.class);
	private static final String CREATE_FAMILY = "Create family...";

	private ComponentPreference prefs;
	private JPanel overallPanel;
	private GridBagConstraints gbc;

	public ComponentFamilyCreateAction(ComponentPreference prefs,
			ComponentServiceIcon iconProvider) {
		super(CREATE_FAMILY, iconProvider.getIcon());
		this.prefs = prefs;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		overallPanel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();

		RegistryChooserPanel registryPanel = new RegistryChooserPanel(prefs);

		gbc.insets.left = 5;
		gbc.insets.right = 5;
		gbc.gridx = 0;
		gbc.anchor = WEST;
		gbc.fill = BOTH;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.gridy++;
		overallPanel.add(registryPanel, gbc);

		ProfileChooserPanel profilePanel = new ProfileChooserPanel(
				registryPanel);
		gbc.gridx = 0;
		gbc.weighty = 1;
		gbc.gridy++;
		overallPanel.add(profilePanel, gbc);

		gbc.gridx = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridy++;
		overallPanel.add(new JLabel("Component family name:"), gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		JTextField familyNameField = new JTextField(60);
		overallPanel.add(familyNameField, gbc);

		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridy++;
		JTextArea familyDescription = new JTextArea(10, 60);
		JScrollPane familyDescriptionPane = new JScrollPane(familyDescription);
		familyDescriptionPane.setBorder(new TitledBorder("Family description"));
		overallPanel.add(familyDescriptionPane, gbc);

		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridy++;
		SharingPolicyChooserPanel permissionPanel = new SharingPolicyChooserPanel(
				registryPanel);
		overallPanel.add(permissionPanel, gbc);

		gbc.gridy++;
		LicenseChooserPanel licensePanel = new LicenseChooserPanel();
		registryPanel.addObserver(licensePanel);
		overallPanel.add(licensePanel, gbc);

		int answer = showConfirmDialog(null, overallPanel,
				"Create Component Family", OK_CANCEL_OPTION);
		if (answer == OK_OPTION)
			doCreate(registryPanel.getChosenRegistry(),
					profilePanel.getChosenProfile(), familyNameField.getText(),
					familyDescription.getText(),
					permissionPanel.getChosenPermission(),
					licensePanel.getChosenLicense());
	}

	private void doCreate(Registry chosenRegistry, Profile chosenProfile,
			String newName, String familyDescription, SharingPolicy permission,
			License license) {
		if (chosenRegistry == null) {
			showMessageDialog(null, "Unable to determine registry",
					"Component Registry Problem", ERROR_MESSAGE);
			return;
		} else if (chosenProfile == null) {
			showMessageDialog(null, "Unable to determine profile",
					"Component Profile Problem", ERROR_MESSAGE);
			return;
		} else if ((newName == null) || newName.isEmpty()) {
			showMessageDialog(null, "Name must be specified",
					"Missing component family name", ERROR_MESSAGE);
			return;
		}

		try {
			if (chosenRegistry.getComponentFamily(newName) != null) {
				showMessageDialog(null, newName + " is already used",
						"Duplicate component family name", ERROR_MESSAGE);
				return;
			}
			chosenRegistry.createComponentFamily(newName, chosenProfile,
					familyDescription, license, permission);
		} catch (ComponentException e) {
			logger.error("failed to create family", e);
			showMessageDialog(null,
					"Unable to create family: " + e.getMessage(),
					"Family creation problem", ERROR_MESSAGE);
		}
	}
}
