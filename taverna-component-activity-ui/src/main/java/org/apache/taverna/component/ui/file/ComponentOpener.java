/**
 * 
 */
package org.apache.taverna.component.ui.file;

import static org.apache.log4j.Logger.getLogger;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.sf.taverna.t2.workbench.file.AbstractDataflowPersistenceHandler;
import net.sf.taverna.t2.workbench.file.DataflowInfo;
import net.sf.taverna.t2.workbench.file.DataflowPersistenceHandler;
import net.sf.taverna.t2.workbench.file.FileType;
import net.sf.taverna.t2.workbench.file.exceptions.OpenException;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.ComponentFactory;
import org.apache.taverna.component.api.Version;
import org.apache.taverna.component.api.Version.ID;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;

/**
 * @author alanrw
 */
public class ComponentOpener extends AbstractDataflowPersistenceHandler
		implements DataflowPersistenceHandler {
	private static Logger logger = getLogger(ComponentOpener.class);

	private ComponentFactory factory;
	private FileType fileType;

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}
	
	@Override
	public DataflowInfo openDataflow(FileType fileType, Object source)
			throws OpenException {
		if (!getOpenFileTypes().contains(fileType))
			throw new IllegalArgumentException("Unsupported file type "
					+ fileType);
		if (!(source instanceof Version.ID))
			throw new IllegalArgumentException("Unsupported source type "
					+ source.getClass().getName());

		WorkflowBundle d;
		try {
			d = factory.getVersion((ID) source).getImplementation();
		} catch (ComponentException e) {
			logger.error("Unable to read dataflow", e);
			throw new OpenException("Unable to read dataflow", e);
		}
		return new DataflowInfo(fileType, source, d, new Date());
	}

	@Override
	public List<FileType> getOpenFileTypes() {
		return Arrays.<FileType> asList(fileType);
	}

	@Override
	public List<Class<?>> getOpenSourceTypes() {
		return Arrays.<Class<?>> asList(Version.ID.class);
	}
}
