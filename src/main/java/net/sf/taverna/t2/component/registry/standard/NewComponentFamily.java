package net.sf.taverna.t2.component.registry.standard;

import static net.sf.taverna.t2.component.utils.SystemUtils.getElementString;
import static net.sf.taverna.t2.workflowmodel.utils.AnnotationTools.getAnnotationString;

import java.util.List;

import net.sf.taverna.t2.annotation.annotationbeans.DescriptiveTitle;
import net.sf.taverna.t2.annotation.annotationbeans.FreeTextDescription;
import net.sf.taverna.t2.component.api.Component;
import net.sf.taverna.t2.component.api.Profile;
import net.sf.taverna.t2.component.api.RegistryException;
import net.sf.taverna.t2.component.api.Version;
import net.sf.taverna.t2.component.registry.ComponentFamily;
import net.sf.taverna.t2.component.registry.ComponentUtil;
import uk.org.taverna.component.api.ComponentFamilyType;
import uk.org.taverna.component.api.Description;
import uk.org.taverna.scufl2.api.annotation.Annotation;
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
			ComponentUtil util) throws RegistryException {
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
	protected Profile internalGetComponentProfile() throws RegistryException {
		return profile;
	}

	public List<Component> getMemberComponents() throws RegistryException {
		return registry.listComponents(this);
	}

	@Override
	protected void populateComponentCache() throws RegistryException {
		for (Component c : getMemberComponents()) {
			NewComponent component = (NewComponent) c;
			componentCache.put(component.getName(), component);
		}
	}

	@Override
	protected Version internalCreateComponentBasedOn(String componentName,
			String description, WorkflowBundle bundle) throws RegistryException {
		bundle.getAnnotations();//FIXME
		if (componentName == null)
			componentName = getAnnotationString(bundle,
					DescriptiveTitle.class, "Untitled");
		if (description == null)
			description = getAnnotationString(bundle,
					FreeTextDescription.class, "Undescribed");
		return registry.createComponentFrom(this, componentName, description,
				bundle, registry.getPreferredLicense(),
				registry.getDefaultSharingPolicy());
	}

	@Override
	protected void internalRemoveComponent(Component component)
			throws RegistryException {
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
