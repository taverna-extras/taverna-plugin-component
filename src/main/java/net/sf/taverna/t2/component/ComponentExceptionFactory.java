/**
 * 
 */
package net.sf.taverna.t2.component;

/**
 * @author alanrw
 * 
 */
class ComponentExceptionFactory {

	private static final String UNEXPECTED_ID = "http://ns.taverna.org.uk/2012/component/unexpected";

	ComponentExceptionFactory() {
	}

	public ComponentException createComponentException(
			String exceptionId, String message) {
		ComponentException result = new ComponentException(message);
		result.setExceptionId(exceptionId);
		return result;
	}

	public ComponentException createUnexpectedComponentException(
			String message) {
		return createComponentException(UNEXPECTED_ID, message);
	}

}
