/**
 * 
 */
package net.sf.taverna.t2.component.api.profile;

import net.sf.taverna.t2.component.api.profile.doc.Replacement;

/**
 * @author alanrw
 * 
 */
public class ExceptionReplacement {
	private final String id, message;

	public ExceptionReplacement(Replacement replacement) {
		id = replacement.getReplacementId();
		message = replacement.getReplacementMessage();
	}

	public String getReplacementId() {
		return id;
	}

	public String getReplacementMessage() {
		return message;
	}
}
