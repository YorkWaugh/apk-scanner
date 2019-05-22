package com.apkscanner.plugin.gui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.apkscanner.gui.messagebox.MessageBoxPane;
import com.apkscanner.plugin.PlugInConfig;
import com.apkscanner.plugin.PlugInPackage;
import com.apkscanner.resource.Resource;
import com.apkscanner.util.Log;

public class ConfigurationsSettingPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -7112752391206173232L;

	private static final String ACT_CMD_ADD = "ACT_CMD_ADD";
	private static final String ACT_CMD_EDIT = "ACT_CMD_EDIT";
	private static final String ACT_CMD_REMOVE = "ACT_CMD_REMOVE";

	private JTable confList;
	private DefaultTableModel confListModel;
	private JPanel confSetPane;
	private JTextField keyField, valueField;

	private PlugInConfig pluginConfig;

	public ConfigurationsSettingPanel(PlugInPackage pluginPackage) {
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

		Border title = new TitledBorder(Resource.STR_LABEL_PLUGIN_PACKAGE_CONFIG.getString());
		Border padding = new EmptyBorder(5,5,5,5);
		setBorder(new CompoundBorder(title, padding));

		confListModel = new DefaultTableModel(new String[] { Resource.STR_LABEL_KEY_NAME.getString(), Resource.STR_LABEL_KEY_VALUE.getString() }, 0) {
			private static final long serialVersionUID = 3057965543770313319L;
			@Override
		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
		};
		confList = new JTable(confListModel);
		confList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//confList.getSelectionModel().addListSelectionListener(this);
		confList.getColumnModel().getColumn(0).setPreferredWidth(300);
		confList.getColumnModel().getColumn(1).setPreferredWidth(500);

		JPanel ctrPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton btn = new JButton(Resource.STR_BTN_ADD.getString());
		btn.setActionCommand(ACT_CMD_ADD);
		btn.addActionListener(this);
		ctrPanel.add(btn);

		btn = new JButton(Resource.STR_BTN_EDIT.getString());
		btn.setActionCommand(ACT_CMD_EDIT);
		btn.addActionListener(this);
		ctrPanel.add(btn);

		btn = new JButton(Resource.STR_BTN_DEL.getString());
		btn.setActionCommand(ACT_CMD_REMOVE);
		btn.addActionListener(this);
		ctrPanel.add(btn);
		ctrPanel.setAlignmentX(1);

		add(new JScrollPane(confList));
		add(ctrPanel);

		confSetPane = new JPanel(new GridBagLayout());

		//GridBagConstraints(int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty, int anchor, int fill, Insets insets, int ipadx, int ipady)
		GridBagConstraints gridHeadConst = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(5,5,5,5),0,0);
		GridBagConstraints gridDataConst = new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0);

		confSetPane.add(new JLabel(Resource.STR_LABEL_KEY_NAME.getString() + ": "), gridHeadConst);
		keyField = new JTextField(30);
        confSetPane.add(keyField, gridDataConst);

        gridHeadConst.gridy++;
        gridDataConst.gridy++;

        confSetPane.add(new JLabel(Resource.STR_LABEL_KEY_VALUE.getString() + ": "), gridHeadConst);
		valueField = new JTextField(30);
        confSetPane.add(valueField, gridDataConst);

        setPluginPackage(pluginPackage);
	}

	public void setPluginPackage(PlugInPackage pluginPackage) {
		setPluginPackage(new PlugInConfig(pluginPackage));
	}

	public void setPluginPackage(PlugInConfig pluginConfig) {
		if(pluginConfig == null) return;
		this.pluginConfig = pluginConfig;
		confListModel.setRowCount(0);
		for(Entry<String, String> entry: pluginConfig.getConfigurations().entrySet()) {
			confListModel.addRow(new String[] { entry.getKey(), entry.getValue() });
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand() == null) return;
		int row = confList.getSelectedRow();

        String key = "", value = "";
		switch(e.getActionCommand()) {
		case ACT_CMD_REMOVE:
			value = ACT_CMD_REMOVE;
		case ACT_CMD_EDIT:
			if(row == -1) break;
			key = ((String) confListModel.getValueAt(row, 0)).trim();
			if(!value.isEmpty()) {
				pluginConfig.clearConfiguration(key);
				confListModel.removeRow(row);
				break;
			}
			value = ((String) confListModel.getValueAt(row, 1)).trim();
		case ACT_CMD_ADD:
			keyField.setText(key);
			valueField.setText(value);
			boolean repeat = false;
			Component parent = SwingUtilities.getWindowAncestor(this);
			do {
				repeat = false;
				int ret = MessageBoxPane.showConfirmDialog(parent, confSetPane, Resource.STR_TITLE_EDIT_CONFIG.getString(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if(ret == JOptionPane.OK_OPTION) {
					String newKey = keyField.getText().trim();
					String newValue = valueField.getText().trim();
					if(newKey.isEmpty()) {
						Log.e("key empty");
						repeat = true;
						continue;
					} else if(key.equals(newKey) && value.equals(newValue)) {
						Log.e("Did not changed");
						repeat = true;
						continue;
					} else {
						if(!key.equals(newKey)) {
							pluginConfig.clearConfiguration(key);
						}
						pluginConfig.setConfiguration(newKey, newValue);

						if(key.isEmpty() || row == -1) {
							confListModel.addRow(new String[] { newKey, newValue });
						} else {
							confListModel.setValueAt(newKey, row, 0);
							confListModel.setValueAt(newValue, row, 1);
						}
					}
				}
			} while (repeat);
			break;
		}
	}

}