package org.apache.taverna.component.ui.menu.component;

import static org.apache.taverna.component.ui.menu.component.ComponentMenuSection.COMPONENT_SECTION;

import java.net.URI;

import net.sf.taverna.t2.ui.menu.AbstractMenuAction;

abstract class AbstractComponentMenuAction extends AbstractMenuAction {
	public AbstractComponentMenuAction(int positionHint, URI id) {
		super(COMPONENT_SECTION, positionHint, id);
	}
}
