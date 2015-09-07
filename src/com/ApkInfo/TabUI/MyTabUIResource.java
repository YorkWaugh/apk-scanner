package com.ApkInfo.TabUI;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.util.ArrayList;

import com.ApkInfo.Core.CoreApkTool;

public class MyTabUIResource extends JPanel{
	private static final long serialVersionUID = -934921813626224616L;

	private Map<String, ImageIcon> imageMap = new HashMap<>();
    
	private JLabel photographLabel;
	ArrayList<String> nameList = new ArrayList<String>();
	ArrayList<String> ShownameList = new ArrayList<String>();
	String path = null;
	
	JList<Object> list = null;
    
	public MyTabUIResource()
	{

	}
	
	public void initialize()
	{
		list = new JList<Object>();
		list.setCellRenderer(new MarioListRenderer());
		list.addListSelectionListener(new JListHandler());

		JScrollPane scroll = new JScrollPane(list);
		scroll.setPreferredSize(new Dimension(300, 400));
		scroll.repaint();
		        
		photographLabel = new JLabel();
		photographLabel.setVerticalTextPosition(JLabel.BOTTOM);
		photographLabel.setHorizontalTextPosition(JLabel.CENTER);
		photographLabel.setHorizontalAlignment(JLabel.CENTER);
		photographLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		        
		this.setLayout(new GridLayout(1, 2));        
		this.add(scroll);
		this.add(photographLabel);
	}

	public void setData(String path, ArrayList<String> data)
	{
		if(list == null)
			initialize();
		
		nameList.clear();
		ShownameList.clear();
		imageMap.clear();
		list.clearSelection();
		
		this.path = path; 
		
		if(data == null) return;
		
		nameList.addAll(data);
		for(int i=0; i < nameList.size(); i++) {
			ShownameList.add(nameList.get(i).substring(path.length()));
		}
		createImageMap(nameList);
		
		list.setListData(ShownameList.toArray());
	}
    
	public class MarioListRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 2674069622264059360L;
		//Font font = new Font("helvitica", Font.BOLD, 10);

		@Override
		public Component getListCellRendererComponent(
				JList<?> list, Object value, int index,
				boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(
			        list, value, index, isSelected, cellHasFocus);
			
			label.setIcon(imageMap.get(path+(String)value));
			label.setHorizontalTextPosition(JLabel.RIGHT);
			//label.setFont(font);            
			return label;
        }
    }

	private Map<String, ImageIcon> createImageMap(ArrayList<String> list) {
		//Map<String, ImageIcon> map = new HashMap<>();
		try {        	
			for(int i=0; i< list.size(); i++) {
				imageMap.put(list.get(i), new ImageIcon(CoreApkTool.getScaledImage(new ImageIcon(list.get(i)),32,32)));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return imageMap;
	}


    private class JListHandler implements ListSelectionListener {
    	// 리스트의 항목이 선택이 되면
    	public void valueChanged(ListSelectionEvent event) {
    		//Log.i("valueChanged : " + list.getSelectedIndex() + " event : "+ event.getSource());
    		
    		photographLabel.setIcon(new ImageIcon(CoreApkTool.getMaxScaledImage(
    				new ImageIcon(nameList.get(list.getSelectedIndex())),photographLabel.getWidth(),photographLabel.getHeight())));
    		
    	}
    }
}