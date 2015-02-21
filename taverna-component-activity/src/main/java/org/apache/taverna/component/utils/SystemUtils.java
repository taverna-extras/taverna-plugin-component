package org.apache.taverna.component.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.taverna.component.api.ComponentException;
import org.apache.taverna.component.registry.api.Description;

import net.sf.taverna.t2.workflowmodel.Dataflow;
import uk.org.taverna.configuration.app.ApplicationConfiguration;
import uk.org.taverna.platform.execution.api.InvalidWorkflowException;
import uk.org.taverna.platform.execution.api.WorkflowCompiler;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;

public class SystemUtils {
	private static final String T2FLOW_TYPE = "application/vnd.taverna.t2flow+xml";
	private static final String SCUFL2_TYPE = "application/vnd.taverna.scufl2.workflow-bundle";
	private ApplicationConfiguration appConfig;
	private WorkflowBundleIO workflowBundleIO;
	private List<WorkflowCompiler> compilers;

	public byte[] serializeBundle(WorkflowBundle bundle) throws ComponentException {
		try {
			ByteArrayOutputStream dataflowStream = new ByteArrayOutputStream();
			workflowBundleIO.writeBundle(bundle, dataflowStream, SCUFL2_TYPE);
			return dataflowStream.toByteArray();
		} catch (Exception e) {
			throw new ComponentException(
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
			throws ComponentException {
		for (Object o : d.getContent())
			if (o instanceof JAXBElement) {
				JAXBElement<?> el = (JAXBElement<?>) o;
				if (el.getName().getLocalPart().equals(name))
					return el;
			}
		throw new ComponentException("no " + name + " element");
	}

	public static String getElementString(Description d, String name)
			throws ComponentException {
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

	public void setCompilers(List<WorkflowCompiler> compilers) {
		this.compilers = compilers;
	}

	public Dataflow compile(WorkflowBundle implementation)
			throws InvalidWorkflowException {
		InvalidWorkflowException exn = null;
		if (compilers != null)
			for (WorkflowCompiler c : new ArrayList<>(compilers))
				try {
					return c.getDataflow(implementation);
				} catch (InvalidWorkflowException e) {
					if (exn == null)
						exn = e;
					continue;
				}
		if (exn != null)
			throw exn;
		throw new InvalidWorkflowException("no compiler available");
	}
}
