package org.apache.taverna.component.api;

import java.util.List;

import org.apache.taverna.component.api.profile.Profile;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public interface Family extends NamedItem {
	/**
	 * Creates a new Component and adds it to this ComponentFamily.
	 * 
	 * @param componentName
	 *            the name of the Component to create. Must not be null.
	 * @param bundle
	 *            the workflow for the Component. Must not be null.
	 * @return the new Component.
	 * @throws ComponentException
	 *             <ul>
	 *             <li>if componentName is null,
	 *             <li>if dataflow is null,
	 *             <li>if a Component with this name already exists,
	 *             <li>if there is a problem accessing the ComponentRegistry.
	 *             </ul>
	 */
	Version createComponentBasedOn(String componentName, String description,
			WorkflowBundle bundle) throws ComponentException;

	/**
	 * Returns the Component with the specified name.
	 * <p>
	 * If this ComponentFamily does not contain a Component with the specified
	 * name <code>null</code> is returned.
	 * 
	 * @param componentName
	 *            the name of the Component to return. Must not be null.
	 * @return the Component with the specified name.
	 * @throws ComponentException
	 *             if there is a problem accessing the ComponentRegistry.
	 */
	Component getComponent(String componentName) throws ComponentException;

	/**
	 * Removes the specified Component from this ComponentFamily.
	 * <p>
	 * If this ComponentFamily does not contain the Component this method has no
	 * effect.
	 * 
	 * @param component
	 *            the Component to remove.
	 * @throws ComponentException
	 *             if there is a problem accessing the ComponentRegistry.
	 */
	void removeComponent(Component component) throws ComponentException;

	/**
	 * Returns all the Components in this ComponentFamily.
	 * <p>
	 * If this ComponentFamily does not contain any Components an empty list is
	 * returned.
	 * 
	 * @return all the Components in this ComponentFamilies.
	 * @throws ComponentException
	 *             if there is a problem accessing the ComponentRegistry.
	 */
	List<Component> getComponents() throws ComponentException;

	/**
	 * Returns the ComponentProfile for this ComponentFamily.
	 * 
	 * @return the ComponentProfile for this ComponentFamily.
	 * @throws ComponentException
	 */
	Profile getComponentProfile() throws ComponentException;

	/**
	 * Returns the ComponentRegistry that contains this ComponentFamily.
	 * 
	 * @return the ComponentRegistry that contains this ComponentFamily.
	 */
	Registry getComponentRegistry();

	/**
	 * @return the name of the component Family.
	 */
	@Override
	String getName();

	/**
	 * @return the description of the component Family.
	 */
	@Override
	String getDescription();

	/**
	 * Delete this family from its registry.
	 * @throws ComponentException
	 */
	void delete() throws ComponentException;
}
