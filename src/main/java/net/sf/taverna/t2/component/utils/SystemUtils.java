package net.sf.taverna.t2.component.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;

import javax.xml.bind.JAXBElement;

import net.sf.taverna.t2.component.api.RegistryException;
import uk.org.taverna.component.api.Description;
import uk.org.taverna.configuration.app.ApplicationConfiguration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;

public class SystemUtils {
	private static final String T2FLOW_TYPE = "application/vnd.taverna.t2flow+xml";
	private static final String SCUFL2_TYPE = "application/vnd.taverna.scufl2.workflow-bundle";// TODO check
	private ApplicationConfiguration appConfig;
	private WorkflowBundleIO workflowBundleIO;

	public byte[] serializeBundle(WorkflowBundle bundle) throws RegistryException {
		try {
			ByteArrayOutputStream dataflowStream = new ByteArrayOutputStream();
			workflowBundleIO.writeBundle(bundle, dataflowStream, SCUFL2_TYPE);
			return dataflowStream.toByteArray();
		} catch (Exception e) {
			throw new RegistryException(
					"failed to serialize component implementation", e);
		}
	}

	private String determineMediaTypeForFilename(File file) {
		String[] pieces = file.getName().split("\\.");
		switch (pieces[pieces.length - 1]) {
		case "t2flow":
			return T2FLOW_TYPE;
		default:
			return SCUFL2_TYPE;
		}
	}

	public void saveBundle(WorkflowBundle bundle, File file) throws Exception {
		workflowBundleIO.writeBundle(bundle, file,
				determineMediaTypeForFilename(file));
	}

	public WorkflowBundle getBundleFromUri(String uri) throws Exception {
		return workflowBundleIO.readBundle(new URL(uri), null);
	}

	public WorkflowBundle getBundle(File file) throws Exception {
		return workflowBundleIO.readBundle(file, null);
	}

	public static JAXBElement<?> getElement(Description d, String name)
			throws RegistryException {
		for (Object o : d.getContent())
			if (o instanceof JAXBElement) {
				JAXBElement<?> el = (JAXBElement<?>) o;
				if (el.getName().getLocalPart().equals(name))
					return el;
			}
		throw new RegistryException("no " + name + " element");
	}

	public static String getElementString(Description d, String name)
			throws RegistryException {
		return getElement(d, name).getValue().toString().trim();
	}

	public static String getValue(Description d) {
		StringBuilder sb = new StringBuilder();
		for (Object o : d.getContent())
			if (!(o instanceof JAXBElement))
				sb.append(o);
		return sb.toString();
	}

	public File getApplicationHomeDir() {
		return appConfig.getApplicationHomeDir();
	}

	public void setAppConfig(ApplicationConfiguration appConfig) {
		this.appConfig = appConfig;
	}

	public void setWorkflowBundler(WorkflowBundleIO workflowBundler) {
		this.workflowBundleIO = workflowBundler;
	}
}
