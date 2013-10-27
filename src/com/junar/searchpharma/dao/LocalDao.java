package com.junar.searchpharma.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.junar.searchpharma.CacheControl;
import com.junar.searchpharma.Commune;
import com.junar.searchpharma.Pharmacy;
import com.junar.searchpharma.R;
import com.junar.searchpharma.Region;
import com.junar.searchpharma.dao.CommuneDao;
import com.junar.searchpharma.dao.DaoMaster.DevOpenHelper;

import de.greenrobot.dao.query.QueryBuilder;

public class LocalDao {
	private Context context;
	private DevOpenHelper helper;
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	
	private RegionDao regionDao;
	private CommuneDao communeDao;
	private CacheControlDao cacheControlDao;
	private PharmacyDao pharmaDao;
	
	public LocalDao(Context context) {
		this.context = context;
		this.initDatabase(context);
	}
	
	@SuppressWarnings("unused")
	private void bulkLoad() {
		Region region = new Region();
		region.setCode(5);
		region.setName("Valparaiso");
		regionDao.insertOrReplace(region);	
		
		region = new Region();
		region.setCode(2);
		region.setName("Antofagasta");
		regionDao.insertOrReplace(region);
		
		Commune commune = new Commune();
		commune.setCode(5801);
		commune.setRegionCode(5);
		commune.setName("Quilpué");				
		communeDao.insertOrReplace(commune);
		
		commune = new Commune();
		commune.setCode(5601);
		commune.setName("San Antonio");
		commune.setRegionCode(5);
		communeDao.insertOrReplace(commune);
		
		commune = new Commune();
		commune.setCode(2201);
		commune.setRegionCode(2);
		commune.setName("Calama");		
		communeDao.insertOrReplace(commune);
		
		commune = new Commune();
		commune.setCode(2203);
		commune.setRegionCode(2);
		commune.setName("San Pedro de Atacama");		
		communeDao.insertOrReplace(commune);		
	}
	
	private void initDatabase(Context context) {
		helper = new DaoMaster.DevOpenHelper(context, "searchpharma-db", null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		
		regionDao = getRegionDao();		
		communeDao = getCommuneDao();		
		cacheControlDao = getCacheControlDao();
		pharmaDao = getPharmaDao();
		//this.bulkLoad();				
		Log.i("cache_pharma", "total farmacias en cache:" + this.getCachePharmaCount());
	}
	
	protected DaoSession getDaoSession() {
		return this.daoSession;
	}
	
	protected CacheControlDao getCacheControlDao() {
		return this.daoSession.getCacheControlDao();
	}
	
	protected RegionDao getRegionDao() {
		return this.daoSession.getRegionDao();
	}
	
	protected CommuneDao getCommuneDao() {
		return this.daoSession.getCommuneDao();
	}
	
	protected PharmacyDao getPharmaDao() {
		return this.daoSession.getPharmacyDao();
	}
	
	public List<Commune> getCommuneListByRegion(Region region) {
		return communeDao.queryBuilder().where(CommuneDao.Properties.RegionCode.eq(region.getCode())).list();		

	}
	
	public List<String> getCommuneSpinnerLabel() {
		List<String> nameList = new ArrayList<String>();
		nameList.add(context.getString(R.string.commune_spinner_label));
		return nameList;
	}
	
	public List<Region> getRegionList() {
		return regionDao.queryBuilder().list();
	}	
	
	public void addDatasetCacheControl() {
		CacheControl cacheControl = new CacheControl();		
		Calendar c = Calendar.getInstance();
		cacheControl.setLastUpdate(c.getTime());
		
		this.cacheControlDao.insert(cacheControl);
	}
	
	public Boolean isFirstPopulate() {
		long count = this.cacheControlDao.count();		
		return (count==0)?true:false;
	}
	
	public void cachePharmaList(List<Pharmacy> list) {	
		this.pharmaDao.insertInTx(list);
	}
	
	public long getCachePharmaCount() {
		return this.pharmaDao.count();
	}
	    
    public List<Pharmacy> getPharmaByMonthDay(Integer month, Integer day) {
    	QueryBuilder.LOG_SQL = true;
    	QueryBuilder.LOG_VALUES = true;
    	return this.pharmaDao.queryBuilder().where(
    					PharmacyDao.Properties.Month.eq(month), 
    					PharmacyDao.Properties.Day.eq(day)
    			).list();    
    }
    
    public List<Pharmacy> getPharmaByCommune(String commune) {
    	return this.pharmaDao.queryBuilder().where(PharmacyDao.Properties.Commune.eq(commune)).list();
    }
}