/**
 * 
 */
package net.sf.taverna.t2.component.registry;

import static java.lang.System.currentTimeMillis;
import static org.apache.log4j.Logger.getLogger;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;
import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.api.Version;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;

/**
 * @author alanrw
 * 
 */
public class ComponentImplementationCache {
	private class Entry {
		WorkflowBundle implementation;
		long timestamp;
	}
	private final long VALIDITY = 15 * 60 * 1000;
	private final Logger logger = getLogger(ComponentImplementationCache.class);
	private final Map<Version.ID, Entry> cache = new WeakHashMap<>();
	private ComponentUtil utils;

	public void setComponentUtil(ComponentUtil utils) {
		this.utils = utils;
	}

	public WorkflowBundle getImplementation(Version.ID id) throws ComponentException {
		long now = currentTimeMillis();
		synchronized (id) {
			Entry entry = cache.get(id);
			if (entry != null && entry.timestamp >= now)
				return entry.implementation;
			logger.info("before calculate component version for " + id);
			Version componentVersion;
			try {
				componentVersion = utils.getVersion(id);
			} catch (RuntimeException e) {
				if (entry != null)
					return entry.implementation;
				throw new ComponentException(e.getMessage(), e);
			}
			logger.info("calculated component version for " + id + " as "
					+ componentVersion.getVersionNumber() + "; retrieving dataflow");
			WorkflowBundle implementation = componentVersion.getImplementation();
			//DataflowValidationReport report = implementation.checkValidity();
			//logger.info("component version " + id + " incomplete:"
			//		+ report.isWorkflowIncomplete() + " valid:"
			//		+ report.isValid());
			entry = new Entry();
			entry.implementation = implementation;
			entry.timestamp = now + VALIDITY;
			return cache.put(id, entry).implementation;
		}
	}
}
