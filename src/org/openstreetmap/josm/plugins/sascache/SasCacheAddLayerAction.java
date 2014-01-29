package org.openstreetmap.josm.plugins.sascache;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;

import java.io.File;

import javax.swing.Action;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction; 
import org.openstreetmap.josm.gui.layer.Layer;

@SuppressWarnings("serial")
public class SasCacheAddLayerAction extends JosmAction 
{
	public SasCacheAddLayerAction() {
		super(tr("SASPlanet cache"), "sasplanet24", null, null, false);
	} 
	
	public void actionPerformed(ActionEvent arg0) 
	{   
		String cPath = SasCachePlugin.getSasCachePath();
		if (!(new File(cPath).exists()))
		{ 
			SasCachePathDialog dlg = new SasCachePathDialog("Укажите путь к папке с кешем SASPlanet");
			dlg.showDialog();
			if (dlg.getValue() == 1)
				dlg.saveSettings();
		}

		cPath = SasCachePlugin.getSasCachePath();
		if (new File(cPath).exists()) 
		{
			Layer layer = new SasCacheLayer();        
			Main.main.addLayer(layer);    

			/*Action[] actions = layer.getMenuEntries();

			for(Action a : actions) 
			{
			    String item = a.toString();
				System.out.println(item);
			}*/
		}
	}


}