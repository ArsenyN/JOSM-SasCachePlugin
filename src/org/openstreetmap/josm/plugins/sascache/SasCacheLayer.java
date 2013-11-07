package org.openstreetmap.josm.plugins.sascache;

import static org.openstreetmap.josm.tools.I18n.tr;

import org.openstreetmap.josm.data.imagery.ImageryInfo;
import org.openstreetmap.josm.data.imagery.ImageryInfo.ImageryBounds;
import org.openstreetmap.josm.data.imagery.ImageryInfo.ImageryType;
import org.openstreetmap.josm.gui.layer.TMSLayer;

public class SasCacheLayer extends TMSLayer 
{
    
    public SasCacheLayer()
    {
       super(buildImageryInfo());
       
       super.tileLoader = new SasCacheTileLoader(this);
    }
    
    private static ImageryInfo buildImageryInfo()
    {
        ImageryInfo info = new ImageryInfo(tr("SasCache"));
        
        ImageryBounds bounds = new ImageryBounds("-80,-180,80,180", ",");
        
        info.setBounds(bounds);
        info.setDefaultMaxZoom(16);
        info.setDefaultMinZoom(4);
        info.setImageryType(ImageryType.TMS);
        // Hack around the TMSLayer's URL check
        info.setUrl("tms:http://example.com");
        
        
        return info;
    }

}