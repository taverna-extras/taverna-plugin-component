package net.sf.taverna.t2.util;

import static org.junit.Assert.*;

import java.io.IOException;

import net.sf.taverna.t2.component.utils.AnnotationUtils;

import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;

public class AnnotationTest {
	//uk.org.taverna.scufl2.annotation.AnnotationTools anntoo;
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
