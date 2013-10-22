package com.junar.searchpharma.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.junar.api.JunarAPI;
import com.junar.searchpharma.Commune;
import com.junar.searchpharma.R;
import com.junar.searchpharma.Region;
import com.junar.searchpharma.SearchPharmaController;
import com.junar.searchpharma.dao.CommuneDao.Properties;
import com.junar.searchpharma.dao.DaoMaster.DevOpenHelper;


public class JunarPharmacyDao {
	private String DATA_GUID = "LISTA-FARMA-DE-TURNO-2";
	
	private Context context;
	
	private RegionDao regionDao;
	private CommuneDao communeDao;
	
	public JunarPharmacyDao(Context context) {		
		this.context = context;
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