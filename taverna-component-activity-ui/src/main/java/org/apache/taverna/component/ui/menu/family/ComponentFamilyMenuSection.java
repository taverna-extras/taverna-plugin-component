/**
 * 
 */
package org.apache.taverna.component.ui.menu.family;

import java.net.URI;

import org.apache.taverna.component.ui.menu.ComponentMenu;

import net.sf.taverna.t2.ui.menu.AbstractMenuSection;

/**
 * @author alanrw
 */
public class ComponentFamilyMenuSection extends AbstractMenuSection {
	public static final URI COMPONENT_FAMILY_SECTION = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#componentFamilySection");

	public ComponentFamilyMenuSection() {
		super(ComponentMenu.COMPONENT, 300, COMPONENT_FAMILY_SECTION);
	}
}
