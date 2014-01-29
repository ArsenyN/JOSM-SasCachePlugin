package org.openstreetmap.josm.plugins.sascache;

import java.util.*; 

public class SasCacheTileCoordsConvertor
{
	interface ConvertDelegate {
		public double[] convert(double lon, double lat);
	}

	class CoordsConverter implements ConvertDelegate
	{
		public double[] convert(double lon, double lat)
		{
			return new double[] {lon, lat};
		}	
	}

	private Map<String, double[]> projs = new HashMap<String, double[]>();
	
	SasCacheTileCoordsConvertor() 
	{
		projs.put("EPSG:4326", new double[] {-180.0, -90.0, 180.0, 90.0});
		projs.put("EPSG:3395", new double[] {-180.0, -85.0840591556, 180.0, 85.0840590501});
		projs.put("EPSG:3857", new double[] {-180.0, -85.0511287798, 180.0, 85.0511287798});
	}

	public double[] _c3857t4326(double lon, double lat)	
	{
		double xtile = lon / 111319.49079327358;
		double ytile = Math.toDegrees(Math.asin(Math.tanh(lat / 20037508.342789244 * Math.PI)));

		return new double[] { xtile, ytile };
	}

	public double[] _c4326t3395(double lon, double lat)
	{
		double E = 0.0818191908426;
		double A = 20037508.342789;
		double F = 53.5865938;

		double tmp = Math.tan(0.78539816339744830962 + Math.toRadians(lat) / 2.0);
		double pow_tmp = Math.pow(Math.tan(0.78539816339744830962 + Math.asin(E * Math.sin(Math.toRadians(lat))) / 2.0), E);

		double x = lon * 111319.49079327358;
		double y = 6378137.0 * Math.log(tmp / pow_tmp);

		return new double[] { x, y };
	}

	public double[] _c4326t3857(double lon, double lat)
	{
		double lat_rad = Math.toRadians(lat);
		double xtile = lon * 111319.49079327358;
		double ytile = Math.log(Math.tan(lat_rad) + (1 / Math.cos(lat_rad))) / Math.PI * 20037508.342789244;

		return new double[] { xtile, ytile };
	}

	public double[] _c3395t4326(double lon, double lat)
	{
		double r_major = 6378137.000;
		double temp = 6356752.3142 / 6378137.000;
		double es = 1.0 - (temp * temp);
		double eccent = Math.sqrt(es);
		double ts = Math.exp(-lat / r_major);
		double HALFPI = 1.5707963267948966;
		double eccnth = 0.5 * eccent;
		double Phi = HALFPI - 2.0 * Math.atan(ts);
		double N_ITER = 15;
		double TOL = 1e-7;
		double i = N_ITER;
		double dphi = 0.1;

		while ((Math.abs(dphi) > TOL) && (i > 0))
		{
			i -= 1;
			double con = eccent * Math.sin(Phi);
			dphi = HALFPI - 2.0 * Math.atan(ts * Math.pow((1.0 - con) / (1.0 + con), eccnth)) - Phi;
			Phi += dphi;
		}

		double x = lon / 111319.49079327358;
		return new double[]  {x, Math.toDegrees(Phi)};
	}

	public double[] _c3857t3395(double lon, double lat)
	{
		double[] c4326 = this._c3857t4326(lon, lat);
		return this._c4326t3395(c4326[0], c4326[1]);
	}

	public double[] transform(double[] coords, ConvertDelegate delegate) // конвертирует координаты из массива попарно
	{
		double[] result = new double[coords.length];

		for (int i = 0; i < coords.length; i += 2)
		{
			double[] nCoords = delegate.convert(coords[i], coords[i + 1]);
			result[i] = nCoords[0];
			result[i + 1] = nCoords[1];
		}		

		return result;
	}

	public double[] coords_by_tile3857(int z, int x, int y)
	{
		z -= 1;

		double[] normalized_tile = new double[] {(double)x / Math.pow(2, (double)z), 1 - ((double)y / Math.pow(2, (double)z))};
		double[] bounds = this.projs.get("EPSG:3857");
		double[] projected_bounds = transform(bounds, new ConvertDelegate() { 
			public double[] convert(double lon, double lat)
			{
				return SasCacheTileCoordsConvertor.this._c4326t3857(lon, lat);
			}
		});		
		
		double[] maxp = new double[] {projected_bounds[2] - projected_bounds[0], projected_bounds[3] - projected_bounds[1]};

		double[] projected_coords = new double[] { (normalized_tile[0] * maxp[0]) + projected_bounds[0], (normalized_tile[1] * maxp[1]) + projected_bounds[1]} ;

		return transform(projected_coords,new ConvertDelegate() { 
			public double[] convert(double lon, double lat)
			{
				return SasCacheTileCoordsConvertor.this._c3857t4326(lon, lat);
			}
		});						
	}

	public int[] tile_by_coords3395(double lon, double lat, int z)
	{
		z -= 1;
		double[] bounds = this.projs.get("EPSG:3395");
		double[] projected_bounds = transform(bounds, new ConvertDelegate() { 
			public double[] convert(double lon, double lat)
			{
				return SasCacheTileCoordsConvertor.this._c4326t3395(lon, lat);
			}
		});		
		
		double[] point = this._c4326t3395(lon, lat);
		point = new double[] {point[0] - projected_bounds[0], point[1] - projected_bounds[1]};
		double[] maxp = new double[] {projected_bounds[2] - projected_bounds[0], projected_bounds[3] - projected_bounds[1]};
		point = new double[] {1.0 * point[0] / maxp[0], 1.0 * point[1] / maxp[1]};

		return new int[] {(int)(point[0] * Math.pow(2, z)), (int)((1 - point[1]) * Math.pow(2, z))};
	}

	public int[] tile3857t3395(int z, int x, int y)
	{
		double[] coords = this.coords_by_tile3857(z, x, y);

		return this.tile_by_coords3395(coords[0], coords[1], z);
	}
}