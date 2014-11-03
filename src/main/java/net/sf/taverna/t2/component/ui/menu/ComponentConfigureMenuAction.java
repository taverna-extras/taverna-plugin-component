package net.sf.taverna.t2.component.ui.menu;

import static javax.swing.Action.NAME;
import static net.sf.taverna.t2.component.ui.ComponentConstants.ACTIVITY_URI;

import javax.swing.Action;

import uk.org.taverna.commons.services.ServiceRegistry;
import net.sf.taverna.t2.component.api.ComponentFactory;
import net.sf.taverna.t2.component.ui.config.ComponentConfigureAction;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;

public class ComponentConfigureMenuAction extends
		AbstractConfigureActivityMenuAction {
	public ComponentConfigureMenuAction() {
		super(ACTIVITY_URI);
	}

	private ActivityIconManager aim;
	private ServiceDescriptionRegistry sdr;
	private EditManager em;
	private FileManager fm;
	private ServiceRegistry str;
	private ComponentFactory factory;

	public void setActivityIconManager(ActivityIconManager aim) {
		this.aim = aim;
	}

	public void setServiceDescriptionRegistry(ServiceDescriptionRegistry sdr) {
		this.sdr = sdr;
	}

	public void setEditManager(EditManager em) {
		this.em = em;
	}

	public void setFileManager(FileManager fm) {
		this.fm = fm;
	}

	public void setServiceTypeRegistry(ServiceRegistry str) {
		this.str = str;
	}

	public void setComponentFactory(ComponentFactory factory) {
		this.factory = factory;
	}

	@Override
	protected Action createAction() {
		Action result = new ComponentConfigureAction(findActivity(),
				getParentFrame(), factory, aim, sdr, em, fm, str);
		result.putValue(NAME, "Configure component");
		addMenuDots(result);
		return result;
	}
}
