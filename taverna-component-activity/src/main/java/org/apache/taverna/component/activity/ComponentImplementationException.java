/**
 * 
 */
package org.apache.taverna.component.activity;

/**
 * @author alanrw
 * 
 */
public class ComponentImplementationException extends Exception {
	public ComponentImplementationException(String string) {
		super(string);
		this.setStackTrace(new StackTraceElement[] {});
	}

	private static final long serialVersionUID = -3844030382222698090L;
	private String exceptionId;

	public void setExceptionId(String exceptionId) {
		this.exceptionId = exceptionId;
	}

	public String getExceptionId() {
		return exceptionId;
	}
}
