package org.openstreetmap.josm.plugins.sascache;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.preferences.PreferenceSetting;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;


public class SasCachePlugin extends Plugin{
		
	JosmAction sasCacheAddLayerAction = new SasCacheAddLayerAction(); 
		
	public SasCachePlugin(PluginInformation info) {
			super(info);		           
			
			MainMenu.add(Main.main.menu.imagerySubMenu, sasCacheAddLayerAction);			
	}	

	public static String getSasCachePath()
	{
		return Main.pref.get("sascache.path");
	}

	public static String getSasCacheLastLayer()
	{
		return Main.pref.get("sascache.lastlayer");
	}

	public static void setSasCacheLastLayer(String layerName)
	{
		Main.pref.put("sascache.lastlayer", layerName);
	}
}
