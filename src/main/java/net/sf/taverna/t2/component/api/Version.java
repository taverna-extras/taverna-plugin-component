package net.sf.taverna.t2.component.api;

import java.io.Serializable;
import java.net.URL;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public interface Version {
	/** @return The version number of this version */
	Integer getVersionNumber();

	/** @return The description of this version */
	String getDescription();

	/** @return The implementation for this version */
	WorkflowBundle getImplementation() throws ComponentException;

	/** @return The component of which this is a version */
	Component getComponent();

	/** @return The identification token for this version */
	ID getID();

	URL getHelpURL();

	interface ID extends Serializable {
		/** @return The name of the family of the component to which we refer to */
		String getFamilyName();

		/** @return The base URL of the registry containing the component */
		URL getRegistryBase();

		/**
		 * @return The name of the component referred to, unique within its
		 *         family
		 */
		String getComponentName();

		/**
		 * @return The version number of the version of the component referred
		 *         to
		 */
		Integer getComponentVersion();

		/**
		 * Tests whether this ID is equal to the given one, <i>excluding</i> the
		 * version.
		 * 
		 * @param id
		 *            The ID to compare to.
		 * @return A boolean
		 */
		boolean mostlyEqualTo(ID id);

		/**
		 * Tests whether this ID is equal to the given component,
		 * <i>excluding</i> the version.
		 * 
		 * @param component
		 *            The component to compare to.
		 * @return A boolean
		 */
		boolean mostlyEqualTo(Component component);
	}

	class Identifier implements ID {
		private static final long serialVersionUID = 1139928258250264997L;

		private final URL registryBase;
		private final String familyName;
		private final String componentName;
		private final Integer componentVersion;

		public Identifier(URL registryBase, String familyName,
				String componentName, Integer componentVersion) {
			super();
			this.registryBase = registryBase;
			this.familyName = familyName;
			this.componentName = componentName;
			this.componentVersion = componentVersion;
		}

		/**
		 * @return the registryBase
		 */
		@Override
		public URL getRegistryBase() {
			return registryBase;
		}

		/**
		 * @return the familyName
		 */
		@Override
		public String getFamilyName() {
			return familyName;
		}

		/**
		 * @return the componentName
		 */
		@Override
		public String getComponentName() {
			return componentName;
		}

		/**
		 * @return the componentVersion
		 */
		@Override
		public Integer getComponentVersion() {
			return componentVersion;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result *= prime;
			result += (componentName == null) ? 0 : componentName.hashCode();
			result *= prime;
			result += (componentVersion == null) ? 0 : componentVersion
					.hashCode();
			result *= prime;
			result += (familyName == null) ? 0 : familyName.hashCode();
			result *= prime;
			result += (registryBase == null) ? 0 : registryBase.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!ID.class.isAssignableFrom(obj.getClass()))
				return false;
			ID other = (ID) obj;
			if (componentName == null) {
				if (other.getComponentName() != null)
					return false;
			} else if (!componentName.equals(other.getComponentName()))
				return false;
			if (componentVersion == null) {
				if (other.getComponentVersion() != null)
					return false;
			} else if (!componentVersion.equals(other.getComponentVersion()))
				return false;
			if (familyName == null) {
				if (other.getFamilyName() != null)
					return false;
			} else if (!familyName.equals(other.getFamilyName()))
				return false;
			if (registryBase == null) {
				if (other.getRegistryBase() != null)
					return false;
			} else if (!registryBase.toString().equals(
					other.getRegistryBase().toString()))
				// NB: Comparison of URLs is on their string form!
				return false;
			return true;
		}

		@Override
		public String toString() {
			return getComponentName() + " V. " + getComponentVersion()
					+ " in family " + getFamilyName() + " on "
					+ getRegistryBase().toExternalForm();
		}

		@Override
		public boolean mostlyEqualTo(ID id) {
			if (this == id)
				return true;
			if (id == null)
				return false;
			if (getClass() != id.getClass())
				return false;
			if (componentName == null) {
				if (id.getFamilyName() != null)
					return false;
			} else if (!componentName.equals(id.getComponentName()))
				return false;
			if (familyName == null) {
				if (id.getFamilyName() != null)
					return false;
			} else if (!familyName.equals(id.getFamilyName()))
				return false;
			if (registryBase == null) {
				if (id.getRegistryBase() != null)
					return false;
			} else if (!registryBase.toString().equals(
					id.getRegistryBase().toString()))
				// NB: Comparison of URLs is on their string form!
				return false;
			return true;
		}

		@Override
		public boolean mostlyEqualTo(Component c) {
			return mostlyEqualTo(new Identifier(c.getRegistry()
					.getRegistryBase(), c.getFamily().getName(), c.getName(), 0));
		}
	}
}
