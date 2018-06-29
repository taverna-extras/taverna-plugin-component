package io.github.taverna_extras.component;
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

import static org.junit.Assert.*;

import java.io.IOException;

import io.github.taverna_extras.component.utils.AnnotationUtils;
import org.junit.Test;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;

public class AnnotationTest {
	//org.apache.taverna.scufl2.annotation.AnnotationTools anntoo;
	@Test
	public void test() throws ReaderException, IOException {
		AnnotationUtils au = new AnnotationUtils();
		WorkflowBundleIO b = new WorkflowBundleIO();
		final String WORKFLOW_FILE = "/hello_anyone.wfbundle";
		final String TITLE = "Hello Anyone";
		final String DESC = "An extension to helloworld.t2flow - this workflow takes a workflow input \"name\" which is combined with the string constant \"Hello, \" using the local worker \"Concatenate two strings\", and outputs the produced string to the workflow output \"greeting\".";

		WorkflowBundle bdl = b.readBundle(
				getClass().getResource(WORKFLOW_FILE), null);
		assertEquals(TITLE, au.getTitle(bdl, "---NOT---GIVEN---"));
		assertEquals(DESC, au.getDescription(bdl, "---NOT---GIVEN---"));
	}

}
