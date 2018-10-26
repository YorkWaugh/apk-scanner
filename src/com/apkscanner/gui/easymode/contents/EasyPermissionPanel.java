package com.apkscanner.gui.easymode.contents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.apkscanner.core.scanner.PermissionGroupManager;
import com.apkscanner.data.apkinfo.ApkInfo;
import com.apkscanner.data.apkinfo.PermissionGroup;
import com.apkscanner.data.apkinfo.PermissionGroupInfo;
import com.apkscanner.data.apkinfo.PermissionInfo;
import com.apkscanner.data.apkinfo.UsesPermissionInfo;
import com.apkscanner.gui.easymode.util.EasyButton;
import com.apkscanner.gui.easymode.util.FlatPanel;
import com.apkscanner.gui.easymode.util.ImageUtils;
import com.apkscanner.resource.Resource;
import com.apkscanner.util.Log;


public class EasyPermissionPanel extends FlatPanel implements ActionListener{
	
	static private Color bordercolor = new Color(242, 242, 242);
	static private Color dangerouscolor = new Color(181,107,105); 
	static private Color permissionbackgroundcolor = new Color(217,217,217);
	
	static private int HEIGHT = 50;
	static private int SHADOWSIZE = 3;
	static private int PERMISSIONICONSIZE = 43;
	
	public EasyPermissionPanel() {
		// TODO Auto-generated constructor stub
		setBackground(bordercolor);
		//permissionpanel = getContentPanel(); 
		//add(permissionpanel, BorderLayout.CENTER);
		setLayout(new FlowLayout(FlowLayout.LEFT, 2, 1));
		setshadowlen(SHADOWSIZE);
		setPreferredSize(new Dimension(0, HEIGHT));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Log.d("click permission");
	}

	public void setPermission(ApkInfo apkInfo) {
		// TODO Auto-generated method stub		
		if(apkInfo.manifest.usesPermission.length < 0) return;
		Log.d(apkInfo.manifest.usesPermission.length+ "");
		PermissionGroupManager permissionGroupManager = new PermissionGroupManager(apkInfo.manifest.usesPermission);
		Set<String> keys = permissionGroupManager.getPermGroupMap().keySet();
		int cnt = 0;
		for(String key: keys) {			
			PermissionGroup g = permissionGroupManager.getPermGroupMap().get(key);
			//permGroup.append(makeHyperLink("@event", g.icon, g.permSummary, g.name, g.hasDangerous?"color:red;":null));			
			FlatPanel permissionicon = new FlatPanel();			
			try {
				ImageIcon imageIcon = new ImageIcon(new URL(g.icon));				
				if(g.hasDangerous)ImageUtils.setcolorImage(imageIcon, dangerouscolor);				
				EasyButton btn = new EasyButton(imageIcon);
				permissionicon.setPreferredSize(new Dimension(PERMISSIONICONSIZE, PERMISSIONICONSIZE));
				permissionicon.setshadowlen(SHADOWSIZE);
				permissionicon.setBackground(permissionbackgroundcolor);
				permissionicon.add(btn);
				btn.addActionListener(this);
				add(permissionicon);				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		validate();		
	}

	public void clear() {
		// TODO Auto-generated method stub
		removeAll();
	}
}
