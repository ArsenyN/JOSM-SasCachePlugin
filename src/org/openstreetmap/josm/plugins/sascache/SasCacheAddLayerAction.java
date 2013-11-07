package org.openstreetmap.josm.plugins.sascache;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction; 
import org.openstreetmap.josm.gui.layer.Layer;

@SuppressWarnings("serial")
public class SasCacheAddLayerAction extends JosmAction 
{
    public SasCacheAddLayerAction() {
        super(tr("SASPlanet cache"), "sasplanet24", null, null, false);
    } 
    
    public void actionPerformed(ActionEvent arg0) {    
        Layer layer = new SasCacheLayer();
        
        Main.main.addLayer( layer ); 
    }
}