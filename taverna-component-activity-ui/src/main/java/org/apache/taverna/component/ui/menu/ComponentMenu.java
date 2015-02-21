/**
 * 
 */
package org.apache.taverna.component.ui.menu;

import static net.sf.taverna.t2.ui.menu.DefaultMenuBar.DEFAULT_MENU_BAR;

import java.net.URI;

import net.sf.taverna.t2.ui.menu.AbstractMenu;

/**
 * @author alanrw
 */
public class ComponentMenu extends AbstractMenu {
	public static final URI COMPONENT = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#component");
	public static final String TITLE = "Components";

	public ComponentMenu() {
		super(DEFAULT_MENU_BAR, 950, COMPONENT, makeAction());
	}

	public static DummyAction makeAction() {
		return new DummyAction(TITLE);
	}
}
