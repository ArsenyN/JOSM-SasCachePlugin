package org.openstreetmap.josm.plugins.sascache;

import static org.openstreetmap.josm.tools.I18n.tr;
import static org.openstreetmap.josm.tools.I18n.trc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map.*;


import java.awt.Component;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;

import java.awt.event.ActionEvent;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.Layer.LayerAction;



class LayersListAction extends AbstractAction implements LayerAction {

	private HashMap<String, String> folderList;
	private SasCacheLayer layer;

	LayersListAction(HashMap<String, String> folderList, SasCacheLayer layer)
	{
		super();
		this.folderList = folderList;
		this.layer = layer;
	}

	public void actionPerformed(ActionEvent e) {
	}

	public Component createMenuComponent() {
		JMenu menu = new JMenu("Cлой данных");

		for(Entry<String, String> entry : folderList.entrySet())
		{
			String folder = entry.getKey();
			String name = entry.getValue();

			JCheckBoxMenuItem item = new JCheckBoxMenuItem(new ApplyLayersListAction(name, folder, this.layer));		
			if (folder == this.layer.layerFolder)	
				item.setSelected(true);
			menu.add(item);			
		}		

		return menu;
	}

	public boolean supportLayers(List<Layer> layers) {
		return false;
	}

}
	

class ApplyLayersListAction extends AbstractAction
{
	private String layerName;
	private String layerFolder;

	private SasCacheLayer layer;

	ApplyLayersListAction(String layerName, String layerFolder, SasCacheLayer layer)
	{
		super(layerName);
		this.layerName = layerName;
		this.layerFolder = layerFolder;
		this.layer = layer;
	}

	public void actionPerformed(ActionEvent e) {
		this.layer.SetLayerFolder(this.layerFolder);
	}
}