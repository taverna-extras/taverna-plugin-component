package org.apache.taverna.component.ui.serviceprovider;

import static java.util.Arrays.asList;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static org.apache.log4j.Logger.getLogger;
import static org.apache.taverna.component.api.config.ComponentPropertyNames.FAMILY_NAME;
import static org.apache.taverna.component.api.config.ComponentPropertyNames.REGISTRY_BASE;
import static org.apache.taverna.component.ui.ComponentConstants.ACTIVITY_URI;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import javax.swing.Icon;

import net.sf.taverna.t2.servicedescriptions.AbstractConfigurableServiceProvider;
import net.sf.taverna.t2.servicedescriptions.CustomizedConfigurePanelProvider;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionProvider;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.Component;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.ComponentFactory;
import org.apache.taverna.component.api.Family;
import org.apache.taverna.component.api.Registry;
import org.apache.taverna.component.api.Version;
import org.apache.taverna.component.ui.panel.RegistryAndFamilyChooserPanel;
import org.apache.taverna.component.ui.preference.ComponentPreference;
import org.apache.taverna.component.ui.util.Utils;

import org.apache.taverna.scufl2.api.common.Visitor;
import org.apache.taverna.scufl2.api.configurations.Configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ComponentServiceProvider extends
		AbstractConfigurableServiceProvider implements
		CustomizedConfigurePanelProvider {
	static final URI providerId = URI
			.create("http://taverna.sf.net/2012/service-provider/component");
	private static Logger logger = getLogger(ComponentServiceProvider.class);

	private final ComponentFactory factory;
	private final ComponentPreference prefs;
	private final ComponentServiceIcon iconProvider;
	private final Utils utils;

	public ComponentServiceProvider(ComponentFactory factory,
			ComponentPreference prefs, ComponentServiceIcon iconProvider,
			Utils utils) {
		super(makeConfig(null, null));
		this.factory = factory;
		this.prefs = prefs;
		this.iconProvider = iconProvider;
		this.utils = utils;
	}

	private static class Conf {
		URL registryBase;
		String familyName;

		Conf(Configuration config) throws MalformedURLException  {
			ObjectNode node = config.getJsonAsObjectNode();
			JsonNode item = node.get(REGISTRY_BASE);
			if (item != null && !item.isNull())
				registryBase = URI.create(item.textValue()).toURL();
			item = node.get(FAMILY_NAME);
			if (item != null && !item.isNull())
				familyName = item.textValue();
		}
	}

	/**
	 * Do the actual search for services. Return using the callBack parameter.
	 */
	@Override
	public void findServiceDescriptionsAsync(
			FindServiceDescriptionsCallBack callBack) {
		Conf config;

		Registry registry;
		try {
			config = new Conf(getConfiguration());
			registry = factory.getRegistry(config.registryBase);
		} catch (ComponentException | MalformedURLException e) {
			logger.error("failed to get registry API", e);
			callBack.fail("Unable to read components", e);
			return;
		}

		try {
			List<ComponentServiceDesc> results = new ArrayList<>();
			for (Family family : registry.getComponentFamilies()) {
				// TODO get check on family name in there
				if (family.getName().equals(config.familyName))
					for (Component component : family.getComponents())
						try {
							SortedMap<Integer, Version> versions = component
									.getComponentVersionMap();
							ComponentServiceDesc newDesc = new ComponentServiceDesc(
									prefs, factory, iconProvider, versions.get(
											versions.lastKey()).getID());
							results.add(newDesc);
						} catch (Exception e) {
							logger.error("problem getting service descriptor",
									e);
						}
				callBack.partialResults(results);
				callBack.finished();
			}
		} catch (ComponentException e) {
			logger.error("problem querying registry", e);
			callBack.fail("Unable to read components", e);
		}
	}

	/**
	 * Icon for service provider
	 */
	@Override
	public Icon getIcon() {
		return iconProvider.getIcon();
	}

	/**
	 * Name of service provider, appears in right click for 'Remove service
	 * provider'
	 */
	@Override
	public String getName() {
		return "Component service";
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getId() {
		return providerId.toASCIIString();
	}

	@Override
	protected List<? extends Object> getIdentifyingData() {
		try {
			Conf config = new Conf(getConfiguration());
			return asList(config.registryBase.toString(), config.familyName);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void createCustomizedConfigurePanel(
			CustomizedConfigureCallBack callBack) {
		RegistryAndFamilyChooserPanel panel = new RegistryAndFamilyChooserPanel(prefs);

		if (showConfirmDialog(null, panel, "Component family import",
				OK_CANCEL_OPTION) != OK_OPTION)
			return;

		Registry registry = panel.getChosenRegistry();
		Family family = panel.getChosenFamily();
		if (registry == null || family == null)
			return;
		callBack.newProviderConfiguration(makeConfig(
				registry.getRegistryBaseString(), family.getName()));
	}

	private static Configuration makeConfig(String registryUrl,
			String familyName) {
		ObjectNode cfg = JsonNodeFactory.instance.objectNode();
		cfg.put(REGISTRY_BASE, registryUrl);
		cfg.put(FAMILY_NAME, familyName);
		Configuration conf = new Configuration();
		conf.setJson(cfg);
		conf.setType(providerId);
		return conf;
	}

	@Override
	public ServiceDescriptionProvider newInstance() {
		return new ComponentServiceProvider(factory, prefs, iconProvider, utils);
	}

	@Override
	public URI getType() {
		return ACTIVITY_URI;
	}

	@Override
	public void setType(URI type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean accept(Visitor visitor) {
		// TODO Auto-generated method stub
		return false;
	}

	public void refreshProvidedComponent(Version.ID ident) {
		try {
			utils.refreshComponentServiceProvider(new ComponentServiceProviderConfig(
					ident).getConfiguration());
		} catch (Exception e) {
			logger.error("Unable to refresh service panel", e);
		}
	}
}
