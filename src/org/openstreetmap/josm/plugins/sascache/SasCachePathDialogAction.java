package org.openstreetmap.josm.plugins.sascache;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;

import java.io.File;

import javax.swing.Action;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction; 
import org.openstreetmap.josm.gui.layer.Layer;

@SuppressWarnings("serial")
public class SasCachePathDialogAction extends JosmAction 
{
	public SasCachePathDialogAction() {
		super(tr("Изменить путь к папке кеша"), "sasplanet24", null, null, false);
	} 
	
	public void actionPerformed(ActionEvent arg0) 
	{   
		//System.out.println("!");
		SasCachePathDialog dlg = new SasCachePathDialog("Укажите путь к папке с кешем SASPlanet");
		dlg.showDialog();
		if (dlg.getValue() == 1)
			dlg.saveSettings();
	}


}