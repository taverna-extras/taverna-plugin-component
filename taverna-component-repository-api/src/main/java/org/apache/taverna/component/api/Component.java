package org.apache.taverna.component.api;

import java.net.URL;
import java.util.SortedMap;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * The abstract interface supported by a component.
 * 
 * @author Donal Fellows
 * @author David Withers
 */
public interface Component extends NamedItem {
	/**
	 * @return the name of the Component.
	 */
	@Override
	String getName();

	/**
	 * Returns the URL for the Component.
	 * 
	 * @return the URL for the Component.
	 */
	URL getComponentURL();

	/**
	 * Creates a new version of this Component.
	 * 
	 * @param bundle
	 *            the workflow that the new ComponentVersion will use.
	 * @return a new version of this Component.
	 * @throws ComponentException
	 *             if there is a problem accessing the ComponentRegistry.
	 */
	Version addVersionBasedOn(WorkflowBundle bundle, String revisionComment)
			throws ComponentException;

	/**
	 * Returns the ComponentVersion that has the specified version number.
	 * 
	 * @param version
	 *            the version number of the ComponentVersion to return.
	 * @return the ComponentVersion that has the specified version number.
	 * @throws ComponentException
	 *             if there is a problem accessing the ComponentRegistry.
	 */
	Version getComponentVersion(Integer version) throws ComponentException;

	/**
	 * @return the description of the Component.
	 */
	@Override
	String getDescription();

	/**
	 * Returns a SortedMap of version number to ComponentVersion.
	 * <p>
	 * The returned map is sorted increasing numeric order.
	 * 
	 * @return a SortedMap of version number to ComponentVersion.
	 */
	SortedMap<Integer, Version> getComponentVersionMap();

	Registry getRegistry();

	Family getFamily();

	void delete() throws ComponentException;
}
