package org.apache.taverna.component.ui.serviceprovider;

import static org.apache.taverna.component.ui.serviceprovider.Service.COMPONENT_ACTIVITY_URI;

import java.net.URI;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sf.taverna.t2.workbench.activityicons.ActivityIconSPI;

public class ComponentServiceIcon implements ActivityIconSPI {
	private static class Init {
		private static Icon icon = new ImageIcon(
				ComponentServiceIcon.class.getResource("/brick.png"));
	}

	@Override
	public int canProvideIconScore(URI activityType) {
		if (activityType.equals(COMPONENT_ACTIVITY_URI))
			return DEFAULT_ICON + 1;
		return NO_ICON;
	}

	@Override
	public Icon getIcon(URI activityType) {
		return Init.icon;
	}

	public Icon getIcon() {
		return Init.icon;
	}
}
