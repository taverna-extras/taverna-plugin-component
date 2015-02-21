/**
 * 
 */
package org.apache.taverna.component.api.profile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author alanrw
 * 
 */
public class ExceptionHandling {
	private final boolean failLists;
	private final List<HandleException> remapped = new ArrayList<HandleException>();

	public ExceptionHandling(
			net.sf.taverna.t2.component.api.profile.doc.ExceptionHandling proxied) {
		for (net.sf.taverna.t2.component.api.profile.doc.HandleException he : proxied
				.getHandleException())
			remapped.add(new HandleException(he));
		this.failLists = proxied.getFailLists() != null;
	}

	public boolean failLists() {
		return failLists;
	}

	public List<HandleException> getHandleExceptions() {
		return remapped;
	}
}
