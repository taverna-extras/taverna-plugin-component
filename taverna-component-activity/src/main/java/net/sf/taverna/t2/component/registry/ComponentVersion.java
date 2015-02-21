/**
 * 
 */
package net.sf.taverna.t2.component.registry;

import org.apache.taverna.component.api.Component;
import org.apache.taverna.component.api.ComponentException;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * @author alanrw
 * 
 */
public abstract class ComponentVersion implements
		org.apache.taverna.component.api.Version {
	private Integer versionNumber;
	private String description;
	private Component component;

	protected ComponentVersion(Component component) {
		this.component = component;
	}

	@Override
	public final synchronized Integer getVersionNumber() {
		if (versionNumber == null)
			versionNumber = internalGetVersionNumber();
		return versionNumber;
	}

	protected abstract Integer internalGetVersionNumber();

	@Override
	public final synchronized String getDescription() {
		if (description == null)
			description = internalGetDescription();

		return description;
	}

	protected abstract String internalGetDescription();

	@Override
	public final synchronized WorkflowBundle getImplementation()
			throws ComponentException {
		// Cached in dataflow cache
		return internalGetImplementation();
	}

	protected abstract WorkflowBundle internalGetImplementation()
			throws ComponentException;

	@Override
	public final Component getComponent() {
		return component;
	}

	@Override
	public ID getID() {
		Component c = getComponent();
		return new ComponentVersionIdentification(c.getRegistry()
				.getRegistryBase(), c.getFamily().getName(), c.getName(),
				getVersionNumber());
	}
}
