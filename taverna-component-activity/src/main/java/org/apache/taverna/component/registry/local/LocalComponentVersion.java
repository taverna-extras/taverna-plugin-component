/**
 * 
 */
package org.apache.taverna.component.registry.local;

import static java.lang.Integer.parseInt;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.log4j.Logger.getLogger;
import static org.apache.taverna.component.registry.local.LocalComponent.COMPONENT_FILENAME;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.registry.ComponentVersion;
import org.apache.taverna.component.utils.SystemUtils;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * @author alanrw
 * 
 */
class LocalComponentVersion extends ComponentVersion {
	private static Logger logger = getLogger(LocalComponentVersion.class);

	private final File componentVersionDir;
	private SystemUtils system;

	protected LocalComponentVersion(LocalComponent component,
			File componentVersionDir, SystemUtils system) {
		super(component);
		this.componentVersionDir = componentVersionDir;
		this.system = system;
	}

	@Override
	protected final String internalGetDescription() {
		File descriptionFile = new File(componentVersionDir, "description");
		try {
			if (descriptionFile.isFile())
				return readFileToString(descriptionFile);
		} catch (IOException e) {
			logger.error("failed to get description from " + descriptionFile, e);
		}
		return "";
	}

	@Override
	protected final Integer internalGetVersionNumber() {
		return parseInt(componentVersionDir.getName());
	}

	@Override
	protected final WorkflowBundle internalGetImplementation()
			throws ComponentException {
		File filename = new File(componentVersionDir, COMPONENT_FILENAME);
		try {
			return system.getBundle(filename);
		} catch (Exception e) {
			logger.error(
					"failed to get component realization from " + filename, e);
			throw new ComponentException("Unable to open dataflow", e);
		}
	}

	@Override
	public int hashCode() {
		return 31 + ((componentVersionDir == null) ? 0 : componentVersionDir
				.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocalComponentVersion other = (LocalComponentVersion) obj;
		if (componentVersionDir == null)
			return (other.componentVersionDir == null);
		return componentVersionDir.equals(other.componentVersionDir);
	}

	@Override
	public URL getHelpURL() {
		return null;
	}
}
