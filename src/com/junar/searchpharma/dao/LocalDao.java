package com.junar.searchpharma.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.junar.searchpharma.Commune;
import com.junar.searchpharma.R;
import com.junar.searchpharma.Region;
import com.junar.searchpharma.dao.CommuneDao.Properties;
import com.junar.searchpharma.dao.DaoMaster.DevOpenHelper;

public class LocalDao {
	private Context context;
	private DevOpenHelper helper;
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	
	private RegionDao regionDao;
	private CommuneDao communeDao;
	
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
		
		//this.bulkLoad();		
	}
	
	protected DaoSession getDaoSession() {
		return this.daoSession;
	}
	
	protected RegionDao getRegionDao() {
		return this.daoSession.getRegionDao();
	}
	
	protected CommuneDao getCommuneDao() {
		return this.daoSession.getCommuneDao();
	}
	
	public List<Commune> getCommuneListByRegion(Region region) {
		List<Commune> communeList = communeDao.queryBuilder().where(Properties.RegionCode.eq(region.getCode())).list();		
		Iterator<Commune> it = communeList.iterator();
		
		List<Commune> returnList = new ArrayList<Commune>();		
		while (it.hasNext()) {
			Commune commune = it.next();
			returnList.add(commune);
		}
		
		return returnList;
	}
	
	public List<String> getCommuneSpinnerLabel() {
		List<String> nameList = new ArrayList<String>();
		nameList.add(context.getString(R.string.commune_spinner_label));
		return nameList;
	}
	
	public List<Region> getRegionList() {
		List<Region> regionList = regionDao.queryBuilder().list();		
		Iterator<Region> it = regionList.iterator();
		List<Region> returnList = new ArrayList<Region>();
		
		// TODO: add header to spinner
		// nameList.add(context.getString(R.string.region_spinner_label));
		
		while (it.hasNext()) {			
			Region region = it.next();
			returnList.add(region);
			Log.i("localdao", "name: ".concat(region.toString()).concat(" code:").concat(region.getCode().toString()));
		}
		
		return returnList;
	}	
}