/**
 * 
 */
package net.sf.taverna.t2.component.registry;

import static java.lang.System.currentTimeMillis;
import static org.apache.log4j.Logger.getLogger;

import java.util.Map;
import java.util.WeakHashMap;

import net.sf.taverna.t2.component.api.RegistryException;
import net.sf.taverna.t2.component.api.Version;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.DataflowValidationReport;

import org.apache.log4j.Logger;

/**
 * @author alanrw
 * 
 */
public class ComponentDataflowCache {
	private class Entry {
		Dataflow dataflow;
		long timestamp;
	}
	private final long VALIDITY = 15 * 60 * 1000;
	private final Logger logger = getLogger(ComponentDataflowCache.class);
	private final Map<Version.ID, Entry> cache = new WeakHashMap<>();
	private ComponentUtil utils;

	public void setComponentUtil(ComponentUtil utils) {
		this.utils = utils;
	}

	public Dataflow getDataflow(Version.ID id) throws RegistryException {
		long now = currentTimeMillis();
		synchronized (id) {
			Entry entry = cache.get(id);
			if (entry != null && entry.timestamp >= now)
				return entry.dataflow;
			logger.info("before calculate component version for " + id);
			Version componentVersion;
			try {
				componentVersion = utils.getVersion(id);
			} catch (RuntimeException e) {
				throw new RegistryException(e.getMessage(), e);
			}
			logger.info("calculated component version for " + id + " as "
					+ componentVersion.getVersionNumber() + "; retrieving dataflow");
			Dataflow dataflow = componentVersion.getDataflow();
			DataflowValidationReport report = dataflow.checkValidity();
			logger.info("component version " + id + " incomplete:"
					+ report.isWorkflowIncomplete() + " valid:"
					+ report.isValid());
			entry = new Entry();
			entry.dataflow = dataflow;
			entry.timestamp = now + VALIDITY;
			return cache.put(id, entry).dataflow;
		}
	}
}
