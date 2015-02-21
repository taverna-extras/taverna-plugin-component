package org.apache.taverna.component.registry.standard;

import org.apache.taverna.component.registry.ComponentFamilyTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 *
 *
 * @author David Withers
 */
@Ignore
public class NewComponentFamilyTest extends ComponentFamilyTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RegistrySupport.pre();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		RegistrySupport.post();
	}
}
