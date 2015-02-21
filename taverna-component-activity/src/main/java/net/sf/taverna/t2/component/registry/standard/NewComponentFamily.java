package net.sf.taverna.t2.component.registry.standard;

import static net.sf.taverna.t2.component.utils.SystemUtils.getElementString;

import java.util.List;

import net.sf.taverna.t2.component.api.Component;
import net.sf.taverna.t2.component.api.ComponentException;
import net.sf.taverna.t2.component.api.Version;
import net.sf.taverna.t2.component.api.profile.Profile;
import net.sf.taverna.t2.component.registry.ComponentFamily;
import net.sf.taverna.t2.component.registry.ComponentUtil;
import net.sf.taverna.t2.component.registry.api.ComponentFamilyType;
import net.sf.taverna.t2.component.registry.api.Description;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * A family of components in the new-interface registry.
 * 
 * @author Donal Fellows
 */
class NewComponentFamily extends ComponentFamily {
	static final String ELEMENTS = "title,description";

	private final NewComponentRegistry registry;
	private final NewComponentProfile profile;
	private final String id;
	private final String name;
	private final String description;
	private final String uri;
	private final String resource;

	NewComponentFamily(NewComponentRegistry componentRegistry,
			NewComponentProfile profile, Description familyDesc,
			ComponentUtil util) throws ComponentException {
		super(componentRegistry, util);
		uri = familyDesc.getUri();
		registry = componentRegistry;
		this.profile = profile;
		id = familyDesc.getId().trim();
		name = getElementString(familyDesc, "title");
		description = getElementString(familyDesc, "description");
		resource = familyDesc.getResource();
	}

	public NewComponentFamily(NewComponentRegistry componentRegistry,
			NewComponentProfile profile, ComponentFamilyType cft,
			ComponentUtil util) {
		super(componentRegistry, util);
		uri = cft.getUri();
		registry = componentRegistry;
		this.profile = profile;
		id = cft.getId();
		name = cft.getTitle();
		description = cft.getDescription();
		resource = cft.getResource();
	}

	@Override
	protected String internalGetName() {
		return name;
	}

	@Override
	protected String internalGetDescription() {
		return description;
	}

	@Override
	protected Profile internalGetComponentProfile() throws ComponentException {
		return profile;
	}

	public List<Component> getMemberComponents() throws ComponentException {
		return registry.listComponents(this);
	}

	@Override
	protected void populateComponentCache() throws ComponentException {
		for (Component c : getMemberComponents()) {
			NewComponent component = (NewComponent) c;
			componentCache.put(component.getName(), component);
		}
	}

	@Override
	protected Version internalCreateComponentBasedOn(String componentName,
			String description, WorkflowBundle bundle) throws ComponentException {
		if (componentName == null)
			componentName = registry.annUtils.getTitle(bundle, "Untitled");
		if (description == null)
			componentName = registry.annUtils.getDescription(bundle,
					"Undescribed");
		return registry.createComponentFrom(this, componentName, description,
				bundle, registry.getPreferredLicense(),
				registry.getDefaultSharingPolicy());
	}

	@Override
	protected void internalRemoveComponent(Component component)
			throws ComponentException {
		registry.deleteComponent((NewComponent) component);
	}

	String getId() {
		return id;
	}

	public String getUri() {
		return uri;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof NewComponentFamily) {
			NewComponentFamily other = (NewComponentFamily) o;
			return registry.equals(other.registry) && id.equals(other.id);
		}
		return false;
	}

	private static final int BASEHASH = NewComponentFamily.class.hashCode();

	@Override
	public int hashCode() {
		return BASEHASH ^ registry.hashCode() ^ id.hashCode();
	}

	public String getResourceLocation() {
		return resource;
	}
}
