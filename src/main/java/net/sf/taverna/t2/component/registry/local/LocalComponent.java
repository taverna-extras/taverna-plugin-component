/**
 * 
 */
package net.sf.taverna.t2.component.registry.local;

import static net.sf.taverna.t2.component.registry.local.LocalComponentRegistry.ENC;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.apache.log4j.Logger.getLogger;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import net.sf.taverna.t2.component.api.Family;
import net.sf.taverna.t2.component.api.Registry;
import net.sf.taverna.t2.component.api.RegistryException;
import net.sf.taverna.t2.component.api.Version;
import net.sf.taverna.t2.component.registry.Component;
import net.sf.taverna.t2.component.utils.SystemUtils;
import net.sf.taverna.t2.workflowmodel.Dataflow;

import org.apache.log4j.Logger;

/**
 * @author alanrw
 * 
 */
class LocalComponent extends Component {
	static final String COMPONENT_FILENAME = "dataflow.t2flow";
	private final File componentDir;
	private final LocalComponentRegistry registry;
	private final LocalComponentFamily family;
	private static Logger logger = getLogger(LocalComponent.class);
	private SystemUtils system;

	public LocalComponent(File componentDir, LocalComponentRegistry registry,
			LocalComponentFamily family, SystemUtils system) {
		super(componentDir);
		this.system = system;
		this.componentDir = componentDir;
		this.registry = registry;
		this.family = family;
	}

	@Override
	protected final Version internalAddVersionBasedOn(Dataflow dataflow,
			String revisionComment) throws RegistryException {
		Integer nextVersionNumber = 1;
		try {
			nextVersionNumber = getComponentVersionMap().lastKey() + 1;
		} catch (NoSuchElementException e) {
			// This is OK
		}
		File newVersionDir = new File(componentDir,
				nextVersionNumber.toString());
		newVersionDir.mkdirs();
		LocalComponentVersion newComponentVersion = new LocalComponentVersion(
				this, newVersionDir, system);
		try {
			system.saveDataflow(dataflow, new File(newVersionDir,
					COMPONENT_FILENAME));
		} catch (Exception e) {
			throw new RegistryException("Unable to save component version", e);
		}
		File revisionCommentFile = new File(newVersionDir, "description");
		try {
			writeStringToFile(revisionCommentFile, revisionComment, ENC);
		} catch (IOException e) {
			throw new RegistryException("Could not write out description", e);
		}

		return newComponentVersion;
	}

	@Override
	protected final String internalGetName() {
		return componentDir.getName();
	}

	@Override
	protected final void populateComponentVersionMap() {
		for (File subFile : componentDir.listFiles())
			try {
				if (subFile.isDirectory())
					versionMap.put(Integer.valueOf(subFile.getName()),
							new LocalComponentVersion(this, subFile, system));
			} catch (NumberFormatException e) {
				// Ignore
			}
	}

	@Override
	public int hashCode() {
		return 31 + ((componentDir == null) ? 0 : componentDir.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocalComponent other = (LocalComponent) obj;
		if (componentDir == null)
			return (other.componentDir == null);
		return componentDir.equals(other.componentDir);
	}

	@Override
	protected final String internalGetDescription() {
		File descriptionFile = new File(componentDir, "description");
		try {
			if (descriptionFile.isFile())
				return readFileToString(descriptionFile);
		} catch (IOException e) {
			logger.error("failed to get description from " + descriptionFile, e);
		}
		return "";
	}

	@Override
	public Registry getRegistry() {
		return registry;
	}

	@Override
	public Family getFamily() {
		return family;
	}
}
