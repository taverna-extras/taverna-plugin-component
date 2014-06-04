/**
 * 
 */
package net.sf.taverna.t2.component.registry;

import static org.apache.log4j.Logger.getLogger;

import java.util.Map;
import java.util.WeakHashMap;

import net.sf.taverna.t2.component.api.RegistryException;
import net.sf.taverna.t2.component.api.Version;
import net.sf.taverna.t2.workflowmodel.Dataflow;

import org.apache.log4j.Logger;

/**
 * @author alanrw
 * 
 */
public class ComponentDataflowCache {
	private final Logger logger = getLogger(ComponentDataflowCache.class);
	private final Map<Version.ID, Dataflow> cache = new WeakHashMap<>();
	private ComponentUtil utils;

	public void setComponentUtil(ComponentUtil utils) {
		this.utils = utils;
	}

	public Dataflow getDataflow(Version.ID id) throws RegistryException {
		synchronized (id) {
			Dataflow dataflow = cache.get(id);
			if (dataflow != null)
				return dataflow;
			logger.info("Before Calculate component version");
			Version componentVersion;
			try {
				componentVersion = utils.getVersion(id);
			} catch (Exception e) {
				throw new RegistryException(e.getMessage(), e);
			}
			logger.info("Calculated component version");
			dataflow = componentVersion.getDataflow();
			dataflow.checkValidity();
			return cache.put(id, dataflow);
		}
	}

}
