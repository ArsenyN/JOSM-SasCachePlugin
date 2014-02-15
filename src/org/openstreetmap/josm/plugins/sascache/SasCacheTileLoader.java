package org.openstreetmap.josm.plugins.sascache;

import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.interfaces.TileJob;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener;

import java.io.*;

public class SasCacheTileLoader extends OsmTileLoader {
	
	private SasCacheLayer layer;
	public SasCacheTileLoader(TileLoaderListener listener) {
		super(listener);

		this.layer = (SasCacheLayer)listener;
	}
	
	public TileJob createTileLoaderJob(final Tile tile) {
		final String folder = this.layer.layerFolder;
		final String ext = this.layer.layerExt;

		return new TileJob() {

			public void run() {       
				tile.initLoading();

				// System.out.println("_" + folder + "_");				

				String basePath = SasCachePlugin.getSasCachePath() + "//" + folder + "//";

				int z = tile.getZoom() + 1;
				int x = tile.getXtile();
				int y = tile.getYtile();

				int xdiv = x / 1024;
				int ydiv = y / 1024;

				String fileName = "z" + z + "//" + xdiv + "//x" + x + "//" + ydiv + "//y" + y + ext;
				
				String filePath = basePath + fileName;

				byte fileContent[] = readFileToByteArray(filePath);
				if ((int)fileContent.length > 0)
				{
					try
					{
						tile.loadImage(new ByteArrayInputStream(fileContent));
					}
					catch(IOException  e)
					{
						System.out.println("Exception while reading the file " + e);
					}
					
					tile.finishLoading();
					listener.tileLoadingFinished(tile, true);
				}
				else
				{
					tile.finishLoading();
					listener.tileLoadingFinished(tile, false);
				}
				
			}
			
			public Tile getTile() {
				return tile;
			}
		};
	}
	
	private static byte[] readFileToByteArray(String filePath) {
		File file = new File(filePath);
		
		byte fileContent[] = new byte[0];
		
		try
		{
			FileInputStream fin = new FileInputStream(file);
			fileContent = new byte[(int)file.length()];
			fin.read(fileContent);
		}
		catch(IOException  e)
		{
			System.out.println("Exception while reading the file " + e);
		}
		
		return fileContent;
	}
}