package io.github.taverna_extras.component.profile;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.ArrayList;
import java.util.List;

import io.github.taverna_extras.component.api.profile.ActivityProfile;
import io.github.taverna_extras.component.api.profile.SemanticAnnotationProfile;

import io.github.taverna_extras.component.api.profile.doc.Activity;
import io.github.taverna_extras.component.api.profile.doc.SemanticAnnotation;

/**
 * Specifies the semantic annotations that an activity must have.
 * 
 * @author David Withers
 */
public class ActivityProfileImpl implements ActivityProfile {
	private final ComponentProfileImpl componentProfile;
	private final Activity activity;

	public ActivityProfileImpl(ComponentProfileImpl componentProfile,
			Activity activity) {
		this.componentProfile = componentProfile;
		this.activity = activity;
	}

	@Override
	public List<SemanticAnnotationProfile> getSemanticAnnotations() {
		List<SemanticAnnotationProfile> saProfiles = new ArrayList<>();
		for (SemanticAnnotation annotation : activity.getSemanticAnnotation())
			saProfiles.add(new SemanticAnnotationProfileImpl(componentProfile,
					annotation));
		return saProfiles;
	}
}
