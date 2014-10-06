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

	private ActivityIconManager aim;//FIXME beaninject
	private ServiceDescriptionRegistry sdr;//FIXME beaninject
	private EditManager em;//FIXME beaninject
	private FileManager fm;//FIXME beaninject
	private ServiceRegistry sr;//FIXME beaninject
	private ComponentFactory factory;//FIXME beaninject

	@Override
	protected Action createAction() {
		Action result = new ComponentConfigureAction(findActivity(),
				getParentFrame(), factory, aim, sdr, em, fm, sr);
		result.putValue(NAME, "Configure component");
		addMenuDots(result);
		return result;
	}
}