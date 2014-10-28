/**
 * 
 */
package net.sf.taverna.t2.component.ui.file;

import static org.apache.log4j.Logger.getLogger;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.sf.taverna.t2.component.api.ComponentException;
import net.sf.taverna.t2.component.api.ComponentFactory;
import net.sf.taverna.t2.component.api.Version;
import net.sf.taverna.t2.component.api.Version.ID;
import net.sf.taverna.t2.component.ui.util.ComponentFileType;
import net.sf.taverna.t2.workbench.file.AbstractDataflowPersistenceHandler;
import net.sf.taverna.t2.workbench.file.DataflowInfo;
import net.sf.taverna.t2.workbench.file.DataflowPersistenceHandler;
import net.sf.taverna.t2.workbench.file.FileType;
import net.sf.taverna.t2.workbench.file.exceptions.OpenException;

import org.apache.log4j.Logger;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * @author alanrw
 */
public class ComponentOpener extends AbstractDataflowPersistenceHandler
		implements DataflowPersistenceHandler {
	private static final FileType COMPONENT_FILE_TYPE = ComponentFileType.instance;//TODO beaninject?
	private static Logger logger = getLogger(ComponentOpener.class);

	private ComponentFactory factory;

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
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
		return new DataflowInfo(COMPONENT_FILE_TYPE, source, d, new Date());
	}

	@Override
	public List<FileType> getOpenFileTypes() {
		return Arrays.<FileType> asList(COMPONENT_FILE_TYPE);
	}

	@Override
	public List<Class<?>> getOpenSourceTypes() {
		return Arrays.<Class<?>> asList(Version.ID.class);
	}
}
