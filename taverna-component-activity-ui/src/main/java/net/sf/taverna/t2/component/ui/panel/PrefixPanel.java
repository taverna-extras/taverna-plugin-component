/**
 * 
 */
package net.sf.taverna.t2.component.ui.panel;

import static javax.swing.BorderFactory.createEtchedBorder;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.border.TitledBorder.CENTER;
import static javax.swing.border.TitledBorder.TOP;

import java.awt.BorderLayout;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.sf.taverna.t2.component.api.ComponentException;
import net.sf.taverna.t2.component.api.profile.Profile;
import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;

/**
 * @author alanrw
 */
@SuppressWarnings("serial")
public class PrefixPanel extends JPanel {
	private DefaultTableModel prefixModel = new DefaultTableModel(10, 2) {
		@Override
		public boolean isCellEditable(int row, int column) {
			// all cells false
			return false;
		};
	};

	private JTable prefixTable = new JTable(prefixModel);

	public PrefixPanel(ProfileChooserPanel profilePanel) {
		this();
		profilePanel.addObserver(new Observer<ProfileChoiceMessage>() {
			@Override
			public void notify(Observable<ProfileChoiceMessage> sender,
					ProfileChoiceMessage message) throws Exception {
				profileChanged(message.getChosenProfile());
			}
		});
	}

	public PrefixPanel() {
		super(new BorderLayout());
		prefixModel.setColumnIdentifiers(new String[] { "Prefix", "URL" });
		add(new JScrollPane(prefixTable), BorderLayout.CENTER);
		setBorder(createTitledBorder(createEtchedBorder(), "Prefixes", CENTER,
				TOP));
	}

	public TreeMap<String, String> getPrefixMap() {
		TreeMap<String, String> result = new TreeMap<>();
		for (int i = 0; i < prefixModel.getRowCount(); i++)
			result.put((String) prefixModel.getValueAt(i, 0),
					(String) prefixModel.getValueAt(i, 1));
		return result;
	}

	private void profileChanged(Profile newProfile) throws ComponentException {
		prefixModel.setRowCount(0);
		if (newProfile != null)
			for (Entry<String, String> entry : newProfile.getPrefixMap()
					.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (!value.endsWith("#"))
					value += "#";
				prefixModel.addRow(new String[] { key, value });
			}
		validate();
	}
}
