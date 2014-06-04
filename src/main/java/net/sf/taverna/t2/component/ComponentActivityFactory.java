package net.sf.taverna.t2.component;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import net.sf.taverna.t2.component.api.Version.ID;
import net.sf.taverna.t2.component.registry.ComponentDataflowCache;
import net.sf.taverna.t2.component.registry.ComponentUtil;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityFactory;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityInputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityOutputPort;

import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ComponentActivityFactory implements ActivityFactory {
	private ComponentUtil util;
	private ComponentDataflowCache cache;

	@Override
	public Activity<?> createActivity() {
		return new ComponentActivity(util);
	}

	@Override
	public URI getActivityType() {
		return URI.create(ComponentActivity.URI);
	}

	@Override
	public JsonNode getActivityConfigurationSchema() {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
 			return objectMapper.readTree(getClass().getResource("/schema.json"));
		} catch (IOException e) {
			return objectMapper.createObjectNode();
		}
	}

	@Override
	public Set<ActivityInputPort> getInputPorts(JsonNode configuration)
			throws ActivityConfigurationException {
		// FIXME Auto-generated method stub
		return null;
	}

	@Override
	public Set<ActivityOutputPort> getOutputPorts(JsonNode configuration)
			throws ActivityConfigurationException {
		// FIXME Auto-generated method stub
		return null;
	}

	public ComponentActivityConfigurationBean createConfiguration(ID id) {
		return new ComponentActivityConfigurationBean(id, util, cache);
	}

	@Required
	public void setComponentUtil(ComponentUtil util) {
		this.util = util;
	}
	@Required
	public void setDataflowCache(ComponentDataflowCache cache) {
		this.cache = cache;
	}
}
