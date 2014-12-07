package db;

import java.util.ArrayList;
import java.util.List;

import model.City;
import model.County;
import model.Province;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	
	public static final String DB_NAME = "cool_weather";
	
	public static final int VERSION = 1;
	
	private static CoolWeatherDB coolWeatherDB;
	
	private SQLiteDatabase db;
	
	private CoolWeatherDB(Context context)
	{
		CoolWeahterOpenHelper dbHelper = new CoolWeahterOpenHelper(context, DB_NAME, null, VERSION);
		
		db = dbHelper.getWritableDatabase();
	}
	
	public synchronized static CoolWeatherDB getInstance(Context context)
	{
		if(coolWeatherDB ==null)
		{
			coolWeatherDB = new CoolWeatherDB(context);
		}
		
		return coolWeatherDB;
				
	}
	
	public void saveProvince(Province province)
	{
		if(province !=null)
		{
			ContentValues contentValues = new ContentValues();
			contentValues.put("province_name", province.getProvinceName());
			contentValues.put("province_code", province.getProvinceCode());
			db.insert("Province", null, contentValues);
		}			
		
	}
	
	/**
	 * 从数据库中读取全国省份的数据
	 * @return List<Province>
	 */
	public List<Province> loadProvinces()
	{
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst())
		{
			do{
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			}while(cursor.moveToNext());
		
		}
		
		return list;
	}

	
	/**
	 * 存储City的信息
	 * @param city
	 */
	public void saveCity(City city)
	{
		if(city!=null)
		{
			ContentValues contentValues = new ContentValues();
			contentValues.put("city_name", city.getCityName());
			contentValues.put("city_code", city.getCityCode());
			contentValues.put("province_id", city.getProvinceId());
			db.insert("City", null, contentValues);
			
		}
	}
	
	/**
	 * 获得所有City的信息
	 */
	public List<City> loadCity(int provinceId)
	{
		List<City> listCity = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
		if(cursor.moveToFirst())
		{
			do{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				listCity.add(city);
			}while(cursor.moveToNext());
		}
		
		return listCity;
	}
	
	/**
	 * 存储County
	 */
	public void saveCounty(County county)
	{
		if(county!=null)
		{
			ContentValues contentValues= new ContentValues();
			contentValues.put("county_name", county.getCountyName());
			contentValues.put("county_code", county.getCountyCode());
			contentValues.put("city_id", county.getCityId());
			db.insert("County", null, contentValues);
		}
		
	}
	
	/**
	 * 获得County列表
	 */
	public List<County> loadCounty(int cityId)
	{
		List<County> listCounty = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id=?", new String[]{String.valueOf(cityId)}, null, null, null);
		if(cursor.moveToFirst())
		{
			do{
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				listCounty.add(county);
			}while(cursor.moveToNext());
			
		}
		return listCounty;
	}
}
