package net.sf.taverna.t2.component.ui.config;

import static java.awt.event.ItemEvent.SELECTED;
import static net.sf.taverna.t2.component.ui.util.Utils.SHORT_STRING;
import static org.apache.log4j.Logger.getLogger;
import static org.apache.taverna.component.api.config.ComponentPropertyNames.COMPONENT_NAME;
import static org.apache.taverna.component.api.config.ComponentPropertyNames.COMPONENT_VERSION;
import static org.apache.taverna.component.api.config.ComponentPropertyNames.FAMILY_NAME;
import static org.apache.taverna.component.api.config.ComponentPropertyNames.REGISTRY_BASE;

import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.SortedMap;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.sf.taverna.t2.component.ui.panel.ComponentListCellRenderer;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationPanel;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.Component;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.ComponentFactory;
import org.apache.taverna.component.api.Version;

import uk.org.taverna.commons.services.ServiceRegistry;
import uk.org.taverna.scufl2.api.activity.Activity;

@SuppressWarnings("serial")
public class ComponentConfigurationPanel extends ActivityConfigurationPanel {
	private static Logger logger = getLogger(ComponentConfigurationPanel.class);

	private ComponentFactory factory;//FIXME beaninject
	private ServiceRegistry sr;

	private final JComboBox<Object> componentVersionChoice = new JComboBox<>();

	public ComponentConfigurationPanel(Activity activity,
			ComponentFactory factory, ServiceRegistry serviceRegistry) {
		super(activity);
		sr = serviceRegistry;
		this.factory = factory;
		componentVersionChoice.setPrototypeDisplayValue(SHORT_STRING);
		initGui();
	}

	private Version getSelectedVersion() {
		return (Version) componentVersionChoice.getSelectedItem();
	}
	private URI getRegistryBase() {
		return URI.create(getProperty(REGISTRY_BASE));
	}
	private String getFamilyName() {
		return getProperty(FAMILY_NAME);
	}
	private String getComponentName() {
		return getProperty(COMPONENT_NAME);
	}
	private Integer getComponentVersion() {
		return Integer.parseInt(getProperty(COMPONENT_VERSION));
	}

	protected void initGui() {
		removeAll();
		setLayout(new GridLayout(0, 2));

		componentVersionChoice.setRenderer(new ComponentListCellRenderer<>());
		componentVersionChoice.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == SELECTED)
					updateToolTipText();
			}
		});
		updateComponentVersionChoice();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 5, 0, 5);
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 2;
		this.add(new JLabel("Component version:"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1;
		this.add(componentVersionChoice, gbc);

		// Populate fields from activity configuration bean
		refreshConfiguration();
	}

	/**
	 * Check that user values in UI are valid
	 */
	@Override
	public boolean checkValues() {
		return true;
	}

	/**
	 * Check if the user has changed the configuration from the original
	 */
	@Override
	public boolean isConfigurationChanged() {
		return !getSelectedVersion().getVersionNumber().equals(
				getComponentVersion());
	}

	/**
	 * Prepare a new configuration bean from the UI, to be returned with
	 * getConfiguration()
	 */
	@Override
	public void noteConfiguration() {
		setProperty(COMPONENT_VERSION, getSelectedVersion().getVersionNumber()
				.toString());
		//FIXME is this right at all???
		configureInputPorts(sr);
		configureOutputPorts(sr);
	}

	private void updateComponentVersionChoice() {
		Component component;
		componentVersionChoice.removeAllItems();
		componentVersionChoice.setToolTipText(null);
		try {
			component = factory.getComponent(getRegistryBase().toURL(),
					getFamilyName(), getComponentName());
		} catch (ComponentException | MalformedURLException e) {
			logger.error("failed to get component", e);
			return;
		}
		SortedMap<Integer, Version> componentVersionMap = component
				.getComponentVersionMap();
		for (Version v : componentVersionMap.values())
			componentVersionChoice.addItem(v);
		componentVersionChoice.setSelectedItem(componentVersionMap
				.get(getComponentVersion()));
		updateToolTipText();
	}

	private void updateToolTipText() {
		Version selectedVersion = (Version) componentVersionChoice
				.getSelectedItem();
		componentVersionChoice.setToolTipText(selectedVersion.getDescription());
	}
}
