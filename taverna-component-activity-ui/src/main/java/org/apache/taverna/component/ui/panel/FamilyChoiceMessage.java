/**
 * 
 */
package org.apache.taverna.component.ui.panel;

import org.apache.taverna.component.api.Family;

/**
 * @author alanrw
 */
public class FamilyChoiceMessage {
	private final Family chosenFamily;

	public FamilyChoiceMessage(Family chosenFamily) {
		this.chosenFamily = chosenFamily;
	}

	/**
	 * @return the chosenFamily
	 */
	public Family getChosenFamily() {
		return chosenFamily;
	}
}
