/**
 * 
 */
package net.sf.taverna.t2.component.registry.local;

import static java.lang.Integer.parseInt;
import static net.sf.taverna.t2.component.registry.local.LocalComponent.COMPONENT_FILENAME;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.log4j.Logger.getLogger;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import net.sf.taverna.t2.component.api.RegistryException;
import net.sf.taverna.t2.component.registry.ComponentVersion;
import net.sf.taverna.t2.component.utils.Utils;
import net.sf.taverna.t2.workflowmodel.Dataflow;

import org.apache.log4j.Logger;

/**
 * @author alanrw
 * 
 */
class LocalComponentVersion extends ComponentVersion {
	private static Logger logger = getLogger(LocalComponentVersion.class);

	private final File componentVersionDir;
	private Utils loader;//FIXME

	protected LocalComponentVersion(LocalComponent component,
			File componentVersionDir) {
		super(component);
		this.componentVersionDir = componentVersionDir;
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
	protected final Dataflow internalGetDataflow() throws RegistryException {
		File filename = new File(componentVersionDir, COMPONENT_FILENAME);
		try {
			return loader.getDataflow(filename);
		} catch (Exception e) {
			logger.error(
					"failed to get component realization from " + filename, e);
			throw new RegistryException("Unable to open dataflow", e);
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
