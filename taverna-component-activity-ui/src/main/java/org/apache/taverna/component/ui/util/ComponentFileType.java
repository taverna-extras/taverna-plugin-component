package org.apache.taverna.component.ui.util;

import net.sf.taverna.t2.workbench.file.FileType;

/**
 * The type of components.
 * 
 * @author alanrw
 */
public class ComponentFileType extends FileType {
	// TODO Change mimetype for sculf2?
	static final String COMPONENT_MIMETYPE = "application/vnd.taverna.component";

	private ComponentFileType() {
	}

	@Override
	public String getDescription() {
		return "Taverna component";
	}

	// Not really used
	@Override
	public String getExtension() {
		return "component";
	}

	@Override
	public String getMimeType() {
		return COMPONENT_MIMETYPE;
	}
}
