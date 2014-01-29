package org.openstreetmap.josm.plugins.sascache;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.util.*;
import java.io.*;

import javax.swing.Action;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.imagery.ImageryInfo;
import org.openstreetmap.josm.data.imagery.ImageryInfo.ImageryBounds;
import org.openstreetmap.josm.data.imagery.ImageryInfo.ImageryType;
import org.openstreetmap.josm.gui.layer.TMSLayer;

import org.openstreetmap.josm.gui.PleaseWaitRunnable;
import org.openstreetmap.josm.gui.progress.ProgressMonitor;
import org.xml.sax.SAXException;
import org.openstreetmap.josm.io.OsmTransferException;

import org.openstreetmap.josm.actions.RenameLayerAction;

public class SasCacheLayer extends TMSLayer 
{
	public String layerFolder = "";
	private String[] folderList;
	
	public SasCacheLayer()
	{
		super(buildImageryInfo());

		super.tileLoader = new SasCacheTileLoader(this);


		String cachePath = SasCachePlugin.getSasCachePath();

		File file = new File(cachePath);
		this.folderList = file.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return new File(dir, name).isDirectory();
			}
		});

		if (folderList.length > 0)
			this.SetLayerFolder(folderList[0]);
	}
	
	private static ImageryInfo buildImageryInfo()
	{
		ImageryInfo info = new ImageryInfo(tr("SasCache"));
		
		ImageryBounds bounds = new ImageryBounds("-90,-180,90,180", ",");
		
		info.setBounds(bounds);
		info.setDefaultMaxZoom(19);
		info.setDefaultMinZoom(10);
		info.setImageryType(ImageryType.TMS);
		// Hack around the TMSLayer's URL check
		info.setUrl("tms:http://example.com_");
		
		
		return info;
	}


	public Action[] getMenuEntries() 
	{
		Action[] actionsSuper = super.getMenuEntries();  

		ArrayList<Action> actions = new ArrayList<Action>();

		actions.add(new LayersListAction(this.folderList, this));
		actions.add(SeparatorLayerAction.INSTANCE);
		for (Action act : actionsSuper)
		{
			actions.add(act);
		}
		actions.add(SeparatorLayerAction.INSTANCE);

		actions.add(new SasCachePathDialogAction());

		Action[] actionArr = actions.toArray(new Action[actions.size()]);

		return actionArr;
	}

	public void clearTileCacheWrapper() {
		
		//this.clearTileCache(monitor);
	}

	public void SetLayerFolder(String layerFolder)
	{
		Boolean needClear = (layerFolder != this.layerFolder && this.layerFolder != "");

		this.layerFolder = layerFolder;

		if (needClear)
		{
			this.tileCache.clear();
			Main.map.repaint();
		}
	}

	

	/*return new Action[] { 
		LayerListDialog.getInstance().createShowHideLayerAction(),
		LayerListDialog.getInstance().createDeleteLayerAction(),
		SeparatorLayerAction.INSTANCE,
		new OffsetAction(),
		new RenameLayerAction(this.getAssociatedFile(), this),
		SeparatorLayerAction.INSTANCE,
		new RenameLayerAction(null,this), 
	};*/
	

}