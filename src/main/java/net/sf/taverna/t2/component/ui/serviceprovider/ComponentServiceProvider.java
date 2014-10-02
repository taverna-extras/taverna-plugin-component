package net.sf.taverna.t2.component.ui.serviceprovider;

import static java.util.Arrays.asList;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.OK_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;
import static net.sf.taverna.t2.component.api.config.ComponentPropertyNames.FAMILY_NAME;
import static net.sf.taverna.t2.component.api.config.ComponentPropertyNames.REGISTRY_BASE;
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
import net.sf.taverna.t2.component.ui.panel.RegistryAndFamilyChooserPanel;
import net.sf.taverna.t2.servicedescriptions.AbstractConfigurableServiceProvider;
import net.sf.taverna.t2.servicedescriptions.CustomizedConfigurePanelProvider;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionProvider;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ComponentServiceProvider extends
		AbstractConfigurableServiceProvider implements
		CustomizedConfigurePanelProvider {
	private static final URI providerId = URI
			.create("http://taverna.sf.net/2012/service-provider/component");
	private static Logger logger = getLogger(ComponentServiceProvider.class);

	private ComponentFactory factory;//FIXME beaninject

	public ComponentServiceProvider() {
		super(JsonNodeFactory.instance.objectNode());
	}

	ComponentServiceProvider(ComponentFactory factory) {
		super(JsonNodeFactory.instance.objectNode());
		this.factory = factory;
	}

	private static class Conf {
		URL registryBase;
		String familyName;

		Conf(ObjectNode config) throws MalformedURLException  {
			registryBase = URI.create(config.get(REGISTRY_BASE).textValue()).toURL();
			familyName = config.get(FAMILY_NAME).textValue();
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
		} catch (ComponentException e) {
			logger.error("failed to get registry API", e);
			callBack.fail("Unable to read components", e);
			return;
		} catch (MalformedURLException e) {
			logger.error("failed to get registry URL", e);
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
							ComponentServiceDesc newDesc = new ComponentServiceDesc(null,null,//FIXME
									versions.get(versions.lastKey()).getID());
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
		RegistryAndFamilyChooserPanel panel = new RegistryAndFamilyChooserPanel();

		if (showConfirmDialog(null, panel, "Component family import",
				OK_CANCEL_OPTION) != OK_OPTION)
			return;

		Registry chosenRegistry = panel.getChosenRegistry();
		Family chosenFamily = panel.getChosenFamily();
		if ((chosenRegistry == null) || (chosenFamily == null))
			return;

		ObjectNode cfg = JsonNodeFactory.instance.objectNode();
		cfg.put(REGISTRY_BASE, chosenRegistry.getRegistryBaseString());
		cfg.put(FAMILY_NAME, chosenFamily.getName());
		callBack.newProviderConfiguration(cfg);
	}

	@Override
	public ServiceDescriptionProvider newInstance() {
		return new ComponentServiceProvider(factory);
	}
}
