package com.junar.searchpharma.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.junar.api.JunarAPI;
import com.junar.searchpharma.Commune;
import com.junar.searchpharma.Region;
import com.junar.searchpharma.SearchPharmaController;
import com.junar.searchpharma.dao.DaoMaster.DevOpenHelper;

import de.greenrobot.dao.AbstractDao;


public class JunarPharmacyDao {
	private String DATA_GUID = "LISTA-FARMA-DE-TURNO-2";
	private SearchPharmaController spController;
	
	private DevOpenHelper helper;
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;	
	
	private RegionDao regionDao;
	private CommuneDao communeDao;
	
	public JunarPharmacyDao(Context context) {		
		this.initDatabase(context);
	}
	
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
	
	public List<String> getCommuneForSpinner() {
		List<Commune> communeList = communeDao.queryBuilder().list();		
		Iterator<Commune> it = communeList.iterator();
		List<String> nameList = new ArrayList<String>();
		
		while (it.hasNext()) {
			Commune commune = it.next();
			nameList.add(commune.getName());
		}
		
		return nameList;
	}		
	
	public List<String> getRegionForSpinner() {
		List<Region> regionList = regionDao.queryBuilder().list();		
		Iterator<Region> it = regionList.iterator();
		List<String> nameList = new ArrayList<String>();
		
		while (it.hasNext()) {
			Region region = it.next();
			nameList.add(region.getName());
			Log.i("localdao", "name: ".concat(region.getName()).concat(" code:").concat(region.getCode().toString()));
		}
		
		return nameList;
	}

	public void queryByCommune() {
		
	}
	
	public void getDatastreamInfo() {
		JunarAPI junar = new JunarAPI();		
		junar.info(DATA_GUID);
	}
	
	public void invokeDatastream() {
		JunarAPI junar = new JunarAPI();
		junar.invoke(DATA_GUID, null);
	}
}