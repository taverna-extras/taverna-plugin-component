/**
 * 
 */
package net.sf.taverna.t2.component.ui.panel;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.apache.taverna.component.api.Component;
import org.apache.taverna.component.api.Family;
import org.apache.taverna.component.api.Registry;
import org.apache.taverna.component.api.Version;

/**
 * @author alanrw
 */
public class ComponentListCellRenderer<T> implements ListCellRenderer<T> {
	private static DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

	@Override
	public java.awt.Component getListCellRendererComponent(
			JList<? extends T> list, T value, int index, boolean isSelected,
			boolean cellHasFocus) {
		return defaultRenderer.getListCellRendererComponent(list,
				convertValueToString(value), index, isSelected, cellHasFocus);
	}

	private static String convertValueToString(Object value) {
		if (value instanceof Registry)
			return ((Registry) value).getRegistryBase().toString();
		if (value instanceof Family)
			return ((Family) value).getName();
		if (value instanceof Component)
			return ((Component) value).getName();
		if (value instanceof Version)
			return ((Version) value).getVersionNumber().toString();
		if (value instanceof Integer)
			return ((Integer) value).toString();
		if (value instanceof String)
			return (String) value;
		if (value == null)
			return "null";
		return "Spaceholder for " + value.getClass().getName();
	}
}
