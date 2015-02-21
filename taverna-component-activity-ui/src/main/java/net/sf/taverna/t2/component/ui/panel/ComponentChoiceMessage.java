/**
 * 
 */
package net.sf.taverna.t2.component.ui.panel;

import org.apache.taverna.component.api.Component;
import org.apache.taverna.component.api.Family;

/**
 * @author alanrw
 */
public class ComponentChoiceMessage {
	private final Component chosenComponent;
	private final Family componentFamily;

	public ComponentChoiceMessage(Family componentFamily, Component chosenComponent) {
		this.componentFamily = componentFamily;
		this.chosenComponent = chosenComponent;
	}

	/**
	 * @return the chosenComponent
	 */
	public Component getChosenComponent() {
		return chosenComponent;
	}

	/**
	 * @return the componentFamily
	 */
	public Family getComponentFamily() {
		return componentFamily;
	}
}
