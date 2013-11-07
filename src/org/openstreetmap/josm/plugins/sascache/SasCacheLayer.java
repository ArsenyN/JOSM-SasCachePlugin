package org.openstreetmap.josm.plugins.sascache;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import java.awt.Toolkit;
import java.awt.Graphics2D;

import org.openstreetmap.josm.Main; 
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.actions.RenameLayerAction;



public class SasCacheLayer extends Layer { 
    
    private Icon layerIcon = null; 
    
    public SasCacheLayer()
    {
        super("SasCache");
        
         layerIcon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/images/sasplanet16.png")));
    }
    
    public Object getInfoComponent() { return null; } 
    
    public Action[] getMenuEntries() {
        // Main menu
        return new Action[] {                
                new RenameLayerAction(null,this),
        };
    }
    
    public void visitBoundingBox(BoundingXYVisitor arg0) {
            return;
    }
 
    public boolean isMergable(Layer arg0) {
        return false;
    } 
    
    public void mergeFrom(Layer arg0) {}
    
    public String getToolTipText() {
        return "SasCache";
    }
    
    public Icon getIcon() { return layerIcon; } 
    
    public void paint(Graphics2D g2, MapView mv, Bounds bounds) { 
        System.out.println("blah");
    }
}