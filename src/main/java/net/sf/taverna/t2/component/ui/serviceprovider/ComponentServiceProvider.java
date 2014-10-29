package net.sf.taverna.t2.component.ui.serviceprovider;

import static java.util.Arrays.asList;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static net.sf.taverna.t2.component.api.config.ComponentPropertyNames.FAMILY_NAME;
import static net.sf.taverna.t2.component.api.config.ComponentPropertyNames.REGISTRY_BASE;
import static net.sf.taverna.t2.component.ui.ComponentConstants.ACTIVITY_URI;
import static org.apache.log4j.Logger.getLogger;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import javax.swing.Icon;

import net.sf.taverna.t2.component.api.Component;
import net.sf.taverna.t2.component.api.ComponentException;
import net.sf.taverna.t2.component.api.ComponentFactory;
import net.sf.taverna.t2.component.api.Family;
import net.sf.taverna.t2.component.api.Registry;
import net.sf.taverna.t2.component.api.Version;
import net.sf.taverna.t2.component.preference.ComponentPreference;
import net.sf.taverna.t2.component.ui.panel.RegistryAndFamilyChooserPanel;
import net.sf.taverna.t2.servicedescriptions.AbstractConfigurableServiceProvider;
import net.sf.taverna.t2.servicedescriptions.CustomizedConfigurePanelProvider;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionProvider;

import org.apache.log4j.Logger;

import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.configurations.Configuration;

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
	private ComponentPreference prefs;

	public ComponentServiceProvider(ComponentFactory factory, ComponentPreference prefs) {
		super(makeConfig(null, null));
		this.factory = factory;
		this.prefs = prefs;
	}

	public void setPreferences(ComponentPreference pref) {
		this.prefs = pref;
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
									prefs, factory, versions.get(
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
		return ComponentServiceIcon.getIcon();
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
		return new ComponentServiceProvider(factory, prefs);
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
}
