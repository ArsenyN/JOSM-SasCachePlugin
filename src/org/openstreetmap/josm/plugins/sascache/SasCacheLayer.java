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
	private HashMap<String, String> existFolders;
	
	public SasCacheLayer()
	{
		super(buildImageryInfo());

		super.tileLoader = new SasCacheTileLoader(this);


		String cachePath = SasCachePlugin.getSasCachePath();

		String[] folderList;
		File file = new File(cachePath);
		folderList = file.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return new File(dir, name).isDirectory();
			}
		});

		this.existFolders = new HashMap<String, String>();
		
		String firstFolder = "";
		for(String folder : folderList)
		{
			if (this.layerList.containsKey(folder))
			{
				this.existFolders.put(folder, this.layerList.get(folder));
				if (firstFolder == "")
					firstFolder = folder;				
			}
		}

		if (firstFolder != "")
		{
			this.SetLayerFolder(firstFolder);			
		}
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

		actions.add(new LayersListAction(this.existFolders, this));
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

	// public void clearTileCacheWrapper() {
		
	// 	//this.clearTileCache(monitor);
	// }

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

	private HashMap<String, String> layerList = new HashMap<String, String>(){{
		put("bing_roads_en", "Bing Maps - дороги (en)");
		put("bing_roads_ru", "Bing Maps - дороги (ru)");
		put("vesat", "Bing Maps - спутник");
		put("vesatbird_N", "Bing Maps - Bird's Eye Север");
		put("GSHin7ane", "Генштаб (in7ane.com)");
		put("Genshtab10km", "Генштаб 10 км");
		put("genshtab1km", "Генштаб 1 км");
		put("genshtab250m", "Генштаб 250 м");
		put("Genshtab2km", "Генштаб 2km");
		put("genshtab500m", "Генштаб 500 м");
		put("Genshtab5km", "Генштаб 5 км");
		put("RoscosmosGeo", "Спутник (Геопортал Роскосмоса)");
		put("Both", "Гибрид (Google)");
		put("land", "Ландшафт (Google)");
		put("map", "Карта (Google)");
		put("sat", "Спутник (Google)");
		put("googletraf", "Пробки (Google Maps)");
		put("Pano_KML", "Panoramio KML");
		put("5v_kavkaz", "Кавказский край 5-верстка. 1926г.");
		put("AerialWWII", "Аэрофотосъемка ВОВ (sasgis.ru)");
		put("SVG_Mende2v_boxpis", "Менде 2-верстка., Тверская (boxpis.ru)");
		put("_SVG_SP_1866_1v_boxpis", "Санкт-Петербургская, 1-верстка., 1866, центр (boxpis.ru)");
		put("SVG_Mosk2v_boxpis", "Шуберт 2-верстка., Московская (boxpis.ru)");
		put("mametfull", "Эстония (Maaamet aero)");
		put("mapiaua", "Карта Украины (mapia.com.ua)");
		put("visicom_world_ru", "Карта Украины и Белоруссии (maps.visicom.ua)");
		put("mail_map", "Карта (Карты@mail)");
		put("osmosnimki_ru", "osmosnimki.ru");
		put("OSM_osmosnimki_pt", "Транспорт OSM (osmosnimki.ru)");
		put("osm_CycleMap", "OpenStreetMap Cycle Map");
		put("osmmapMapnik", "MAPNIK (OpenStreetMap)");
		put("OSM_mapsurfer", "Карта OSM (mapsurfer.net)");
		put("OSM_mapsurfer_hillshade", "OpenMapSurfer (рельеф)");
		put("OSM_mapsurfer_hybrid", "OpenMapSurfer (гибрид)");
		put("OSM_opnvkarte", "Карта OSM (opnvkarte.de)");
		put("gumap", "Карта (Gurtam)");
		put("landmff", "Рельеф (maps-for-free)");
		put("multimap", "Карта (multimap)");
		put("Navitel", "Карта Navitel");
		put("water", "Водные ресурсы (maps-for-free)");
		put("mars", "Марс (Google)");
		put("moon", "Луна (Google)");
		put("nasaMoonO", "Lunar Orbiter (NASA)");
		put("nasaMarsMOLA", "Mars MOLA (NASA)");
		put("sky", "Звёзды (Google)");
		put("Wiki", "Wikimapia");
		put("WikiMap", "Карта (WikiMapia)");
		put("WikiMapHyb", "Гибрид (Wikimapia)");
		put("yhhyb", "Гибрид (Yahoo!)");
		put("yhmap", "Карта (Yahoo!)");
		put("yhsat", "Спутник (Yahoo!)");
		put("2Gis", "2Gis.ru");
		put("43.geohub.houses", "Кировская область - недвижимость (43.geohub.net)");
		put("43.geohub.hybrid", "Кировская область - гибрид (43.geohub.net)");
		put("43.geohub.les", "Кировская область - лесничества (43.geohub.net)");
		put("43.geohub.orto", "Кировская область - ортофотоплан (43.geohub.net)");
		put("43.geohub.pl500", "Кировская область - план (43.geohub.net)");
		put("43.geohub.full", "Кировская область - карта (43.geohub.net)");
		put("geohub.hybrid", "Гибрид (geohub.net)");
		put("43.geohub.relief", "Рельеф (geohub.net)");
		put("geohub.full", "Карта (geohub.net)");
		put("43.geohub.sat", "Cпутник (geohub.net)");
		put("atyrau", "Атырау");
		put("balticmaps_eu_map", "Карта Латвии");
		put("balticmaps_eu_topo", "Латвия топо");
		put("cheboksary.new", "Чебоксары (cheboksary.ws)");
		put("cheboksary.old", "Чебоксары-old (cheboksary.ws/2010)");
		put("contur", "Высоты (heywhatsthat.com)");
		put("Eniro", "Карта (Eniro.com)");
		put("EniroA", "Аэрофото (Eniro.com)");
		put("etokarta", "Витебск спутник z18 (etokarta.com)");
		put("geocenter-consulting", "Карта (Геоцентр-Консалтинг)");
		put("geogarage_com_route", "marine.geogarage.com/routes (geogarage.com)");
		put("geoportal_md_cadastre", "Кадастровая карта Молдавии (geoportal.md)");
		put("geoportal_md", "Карта Молдавии (geoportal.md)");
		put("Kemerovo", "Кемеровская область");
		put("gis-gid.ru.borders", "Административные границы (gis-gid.ru)");
		put("gis-gid.ru.relief", "Рельеф (gis-gid.ru)");
		put("gis-gid_ru", "Карта городов России (gis-gid.ru)");
		put("gisogvrt_ru_map", "Татарстан карта (gisogvrt.ru)");
		put("gisogvrt_ru_sat", "Татарстан снимок (gisogvrt.ru)");
		put("gumap", "Карта Gurtam (maps.cnord.ru)");
		put("igis_izhevsk", "Ижевск (igis.ru)");
		put("in-sfera_ru", "Карта Краснодара (in-sfera.ru)");
		put("Irkmap", "Карта Иркутска");
		put("zoomify_kartaspb", "Kartaspb (zoomify)");
		put("magnitog_ru", "Магнитогорск (magnitog.ru)");
		put("mpoisk_ru", "МТС Поиск (wap.mpoisk.ru)");
		put("navici_com", "Карта Карелии (navici.com)");
		put("NavitelProbki", "Пробки Navitel");
		put("Point_md", "Карта Молдавии (point.md)");
		put("rugis_ru", "Новокузнецк (rugis.ru)");
		put("runwayfinder_com", "Лётные карты  (runwayfinder.com)");
		put("sibreg_org", "Карты по Сибирскому региону");
		put("teleatlas_com", "Карта (navigation.teleatlas.com)");
		put("vi-tel", "Карта Ви-Тел (vi-tel.ru)");
		put("waze", "Waze live map (world.waze.com/livemap/)");
		put("zoomify", "(zoomify)");
		put("ArcGIS.Hybrid", "ArcGIS.Hybrid");
		put("ArcGIS.Imagery", "ArcGIS.Imagery");
		put("ArcGIS.LightGrayCanvas", "ArcGIS.LightGrayCanvas");
		put("ArcGIS.NatGeo", "ArcGIS.NatGeo");
		put("ArcGIS.Streets", "ArcGIS.Streets");
		put("ArcGIS.Terrain", "ArcGIS.Terrain");
		put("ESRI_Topo_Maps", "ESRI_Topo_Maps");
		put("USA_Topo_Maps", "USA_Topo_Maps");
		put("Gsh_moscow", "Москва + МО Генштаб (school2.ru)");
		put("topomapper.com", "Генштаб (TopoMapper.com)");
		put("topo_marshruty", "ТопоКарта (Маршруты.ру)");
		put("gp_landsat", "Geoportal-Ландсат");
		put("gp_meteor", "Geoportal-Метеор");
		put("gp_ortho_region", "Geoportal-Alos");
		put("Google_land", "Ландшафт без названий (Google)");
		put("mapmaker", "Карта (GoogleMapMaker)");
		put("GoogleTransit", "Транспорт (Google)");
		put("Pano", "Panoramio Картинки");
		put("1902_kuban", "Карта Кубанской области 1902г.");
		put("1918_kuban_10v", "10-верстка Кубани (1918 г. Стрельбицкого)");
		put("1925_maykop", "Карта Майкопского округа 1925г.");
		put("1930_kuban", "Карта Кубанского округа 1930г.");
		put("5v_kavk_1877", "5 верстовка Кавказа 1877г");
		put("gallica_bnf_fr", "gallica.bnf.fr");
		put("mosday.1904", "Карта Москвы 1904 (mosday.ru)");
		put("mosday.1912", "Карта Москвы 1912 (mosday.ru)");
		put("mosday.1964", "Карта Москвы 1964 (mosday.ru)");
		put("retromap.1987", "Карта Москвы 1987 (retromap.ru)");
		put("strelb_10_1882", "10в. Стрельбицкого 1882г.");
		put("wwii-ug", "Немецкая Аэро-Фото съёмка (Краснодар)");
		put("giskaluga_ru", "Геопортал Калужской области (giskaluga.ru)");
		put("GoMap_map", "Азербайджан (GoMap.az)");
		put("ufakarta_ru", "Карта Уфы (ufakarta.ru)");
		put("yollar_az", "Азербайджан (Yollar.az)");
		put("mapsurfer_altitude_contour", "Контуры Высот (OpenMapSurfer)");
		put("Navteq.hyb.mq", "Гибрид Navteq (mapquest.com)");
		put("Navteq.hyb", "Гибрид Navteq (navteq.com)");
		put("Navteq.map.mq", "Карта Navteq (mapquest.com)");
		put("Navteq.map", "Карта Navteq (navteq.com)");
		put("Navteq.nn4d", "Карта Navteq (nn4d.com)");
		put("Navteq.sat", "Спутник DG (navteq.com)");
		put("Nokia.Live.View", "Карта Nokia (Live View)");
		put("Nokia.Satellite.Recent", "Спутник DG (Nokia Satellite Recent)");
		put("Nokia.Satellite.Standard", "Спутник DG (Nokia Satellite Standard)");
		put("ovi_com_Hyb", "Гибрид Nokia (maps.ovi.com)");
		put("ovi_com_Land", "Местность Nokia (maps.ovi.com)");
		put("ovi_com_map", "Карта Nokia (maps.ovi.com)");
		put("ovi_com_Sat", "Спутник DG (maps.ovi.com)");
		put("osmos_hyb_3d", "Трёхмерные здания");
		put("OSM_cloudmade", "Карта + знаки  OSM (cloudmade.com)");
		put("osm_gps_tile", "Карта GPS-треков OSM");
		put("OSM_mapsurfer_boundary", "Карта границ OSM (mapsurfer.net)");
		put("OSM_mapsurfer_Grayscale", "Карта OSM Серая (mapsurfer.net)");
		put("OSM_toolserver_org", "Надписи OSM (toolserver.org)");
		put("osm_ito_Administrative_boundaries", "Административное деление (ITO Map)");
		put("osm_ito_Barriers", "Шлагбаумы, заборы (ITO Map)");
		put("osm_ito_Buildings_and_addresses", "Здания и адресация (ITO Map)");
		put("osm_ito_Building_heights", "Выстоность зданий (ITO Map)");
		put("osm_ito_Canals", "Водные каналы (ITO Map)");
		put("osm_ito_Car_parks", "Стоянки, парковки (ITO Map)");
		put("osm_ito_Electricity_distribution", "Электрические линии (ITO Map)");
		put("osm_ito_Electricity_generation", "Electricity generation (ITO Map)");
		put("osm_ito_FIXME", "FIXME (ITO Map)");
		put("osm_ito_Former_railways", "Former railways (ITO Map)");
		put("osm_ito_Golf", "Golf (ITO Map)");
		put("osm_ito_Green_space_access", "Green space access (ITO Map)");
		put("osm_ito_Highway_lanes", "Highway lanes (ITO Map)");
		put("osm_ito_Highway_lighting", "Highway lighting (ITO Map)");
		put("osm_ito_Layers", "Уровни объектов (ITO Map)");
		put("osm_ito_Metro", "Метро (ITO Map)");
		put("osm_ito_Navigable_waterways", "Водная навигация (ITO Map)");
		put("osm_ito_notname_tag", "notname tag (ITO Map)");
		put("osm_ito_Railways", "Железные дороги (ITO Map)");
		put("osm_ito_Railway_electrification", "Railway electrification (ITO Map)");
		put("osm_ito_Railway_engineering", "Railway engineering (ITO Map)");
		put("osm_ito_Railway_freight", "Railway freight (ITO Map)");
		put("osm_ito_Railway_stations", "Railway stations (ITO Map)");
		put("osm_ito_Railway_tracks", "Railway tracks (ITO Map)");
		put("osm_ito_Recent_edits_by_type_last_7_days", "Recent edits by type last 7 days (ITO Map)");
		put("osm_ito_Recent_edits_by_type_last_90_days", "Recent edits by type last 90 days (ITO Map)");
		put("osm_ito_Recent_edits_last_7_days", "Recent edits last 7 days (ITO Map)");
		put("osm_ito_Recent_edits_last_90_days", "Recent edits last 90 days (ITO Map)");
		put("osm_ito_Schools", "Школы (ITO Map)");
		put("osm_ito_Sidewalks_and_footways", "Пешеходные дороги (ITO Map)");
		put("osm_ito_Speed_limits", "Ограничения скорости (ITO Map)");
		put("osm_ito_Speed_limits_fixme", "Speed limits fixme (ITO Map)");
		put("osm_ito_Speed_limits_kmh", "Speed limits kmh (ITO Map)");
		put("osm_ito_Speed_limits_kmh_major_roads", "Speed limits kmh major roads (ITO Map)");
		put("osm_ito_Speed_limits_major_roads", "Speed limits major roads (ITO Map)");
		put("osm_ito_Speed_limits_mph", "Speed limits mph (ITO Map)");
		put("osm_ito_Speed_limits_mph_major_roads", "Speed limits mph major roads (ITO Map)");
		put("osm_ito_Surfaces", "Дорожное покрытие (ITO Map)");
		put("osm_ito_Transport_construction", "Transport construction (ITO Map)");
		put("osm_ito_Unknown_roads", "Unknown roads (ITO Map)");
		put("osm_ito_Unrecognised_speed_limits_(UK_only)", "Unrecognised speed limits (UK only) (ITO Map)");
		put("osm_ito_Water", "Реки, притоки, вода (ITO Map)");
		put("rosreestr", "Карта (rosreestr.ru)");
		put("rosreestr_cadastr", "Кадастровые границы (rosreestr.ru)");
		put("Rosreestr_hyb", "undefined");
		put("rosreestr_zone", "Кадастровое землепользование (rosreestr.ru)");
		put("sky_map_org", "Звёзды (sky-map.org)");
		put("sky_chandracomp", "Звёзды Chandracomp (Google)");
		put("sky_sozv", "Слой созвездий (Google)");
		put("sky_Spitzer", "Звёзды Spitzer (Google)");
		put("sky_wikisky_org", "Звёзды (wikisky.org)");
		put("MARS_elevation", "Марс Высоты (Google)");
		put("mars_infrared", "Марс Инфракрасный (Google)");
		put("Moon_terrain", "Луна Высоты (Google)");
		put("djungarii", "Хребтовка Джунгарии");
		put("ergaki_aradan", "Ергаки - Арадан");
		put("arkhiz", "Архыз");
		put("damkhurts", "Дамхурц");
		put("dombay", "Теберда и Домбай");
		put("arkhiz_dombay", "Сводная");
		put("kb_zapovednik", "КБ высокогорный заповедник");
		put("kr_polyana", "Красная поляна");
		put("prielbrus", "Приэльбрусье");
		put("arkhiz1991", "Архыз Загедан Рица");
		put("dombai1991", "Теберда - Домбай");
		put("elbrus1991", "Приэльбрусье");
		put("tourism1991", "Сводная");
		put("hr_elbrus", "Хребтовка Приэльбрусье (Ляпин В.Г.)");
		put("lcarta", "Абишира-Ахуба (В.Ляпин)");
		put("Terskey", "Хребтовка Терскей-Алатау");
		put("TyanShan", "Горные перевалы северного Тянь-Шаня");
		put("Tyan_Shan", "Тянь-Шань + Памир");
		put("wkavkaz_kot", "Западный Кавказ (КОТ ОРИЕНТ)");
		put("betamap_meta_ua", "Карты Украины (map.meta.ua)");
		put("cadastre_ortofoto", "Кадастровая Карта Украины");
		put("crimea_ua", "Карта Крыма (crimea.ua)");
		put("gis-center_com_ukr", "Гибрид Украины (gis-center.com)");
		put("luxena", "Украина (luxena.com)");
		put("mapservices.com.ua.hybrid", "Гибрид Украины (mapservices.com.ua)");
		put("mapservices.com.ua.map", "Карта Украины (mapservices.com.ua)");
		put("nadoloni_com", "Дрогобыч и Трускавец (nadoloni.com)");
		put("odessa_ua", "Карта Одессы (odessa.ua)");
		put("sat_meta_ua", "Спутниковые снимки Украины (map.meta.ua)");
		put("Umap_17074", "Златоуст (Umap.ru)");
		put("Umap_23074", "Копейск (Umap.ru)");
		put("Umap_40066", "Красноуральск (Umap.ru)");
		put("Umap_44066", "Лобва (Umap.ru)");
		put("Umap_47074", "Троицк (Umap.ru)");
		put("Umap_53066", "Новоуральск (Umap.ru)");
		put("Umap_62066", "Серов (Umap.ru)");
	}};

}