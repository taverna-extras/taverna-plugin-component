package org.apache.taverna.component;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.taverna.component.utils.AnnotationUtils;
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
