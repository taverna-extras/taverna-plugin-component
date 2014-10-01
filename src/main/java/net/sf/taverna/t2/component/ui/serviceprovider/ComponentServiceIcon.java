package net.sf.taverna.t2.component.ui.serviceprovider;

import static net.sf.taverna.t2.component.ui.serviceprovider.Service.COMPONENT_ACTIVITY_URI;

import java.net.URI;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sf.taverna.t2.workbench.activityicons.ActivityIconSPI;

public class ComponentServiceIcon implements ActivityIconSPI {
	private static Icon icon;

	public static Icon getIcon() {
		if (icon == null)
			icon = new ImageIcon(
					ComponentServiceIcon.class.getResource("/brick.png"));
		return icon;
	}

	@Override
	public int canProvideIconScore(URI activityType) {
		if (activityType.equals(COMPONENT_ACTIVITY_URI))
			return DEFAULT_ICON + 1;
		return NO_ICON;
	}

	@Override
	public Icon getIcon(URI activityType) {
		return getIcon();
	}
}
