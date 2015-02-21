/*******************************************************************************
 * Copyright (C) 2012 The University of Manchester
 *
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.component.profile;

import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.component.api.profile.PortProfile;
import net.sf.taverna.t2.component.api.profile.SemanticAnnotationProfile;
import net.sf.taverna.t2.component.api.profile.doc.Port;
import net.sf.taverna.t2.component.api.profile.doc.SemanticAnnotation;

/**
 * Specifies the semantic annotations that a port must have.
 * 
 * @author David Withers
 */
public class PortProfileImpl implements PortProfile {
	private final ComponentProfileImpl componentProfile;
	private final Port port;

	public PortProfileImpl(ComponentProfileImpl componentProfile, Port port) {
		this.componentProfile = componentProfile;
		this.port = port;
	}

	@Override
	public List<SemanticAnnotationProfile> getSemanticAnnotations() {
		List<SemanticAnnotationProfile> saProfiles = new ArrayList<>();
		for (SemanticAnnotation annotation : port.getSemanticAnnotation())
			saProfiles.add(new SemanticAnnotationProfileImpl(componentProfile,
					annotation));
		return saProfiles;
	}

	@Override
	public String toString() {
		return "PortProfile \n  SemanticAnnotations : "
				+ getSemanticAnnotations();
	}
}
