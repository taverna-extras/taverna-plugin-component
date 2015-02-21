/**
 * 
 */
package org.apache.taverna.component.activity;

/**
 * @author alanrw
 * 
 */
class ComponentExceptionFactory {
	private static final String UNEXPECTED_ID = "http://ns.taverna.org.uk/2012/component/unexpected";

	ComponentExceptionFactory() {
	}

	public ComponentImplementationException createComponentException(
			String exceptionId, String message) {
		ComponentImplementationException result = new ComponentImplementationException(message);
		result.setExceptionId(exceptionId);
		return result;
	}

	public ComponentImplementationException createUnexpectedComponentException(
			String message) {
		return createComponentException(UNEXPECTED_ID, message);
	}
}
