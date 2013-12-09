package cl.gob.datos.farmacias.helpers;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.junar.searchpharma.CacheControl;
import com.junar.searchpharma.Commune;
import com.junar.searchpharma.Pharmacy;
import com.junar.searchpharma.Region;
import com.junar.searchpharma.dao.CacheControlDao;
import com.junar.searchpharma.dao.CommuneDao;
import com.junar.searchpharma.dao.CommuneDao.Properties;
import com.junar.searchpharma.dao.DaoMaster;
import com.junar.searchpharma.dao.DaoMaster.DevOpenHelper;
import com.junar.searchpharma.dao.DaoSession;
import com.junar.searchpharma.dao.PharmacyDao;
import com.junar.searchpharma.dao.RegionDao;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition.StringCondition;

public class LocalDao {
    private DevOpenHelper helper;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;

    private RegionDao regionDao;
    private CommuneDao communeDao;
    private CacheControlDao cacheControlDao;
    private PharmacyDao pharmaDao;

    public LocalDao(Context context) {
        this.initDatabase(context);
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
        Log.i("cache_pharma",
                "total farmacias en cache:" + this.getCachePharmaCount());
    }

    public DaoSession getDaoSession() {
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
        List<Commune> communeList = communeDao
                .queryBuilder()
                .where(new StringCondition(
                        "_ID IN (SELECT COMMUNE FROM PHARMACY WHERE REGION = "
                                + region.getId() + " GROUP BY COMMUNE)"))
                .orderAsc(Properties.Name).list();
        return communeList;
    }

    public List<Pharmacy> getPharmaListByRegionAndComune(int region, int commune) {
        List<Pharmacy> pharmacyList = pharmaDao
                .queryBuilder()
                .where(PharmacyDao.Properties.Region.eq(region),
                        PharmacyDao.Properties.Commune.eq(commune)).list();

        return pharmacyList;
    }

    public List<Region> getRegionList() {

        List<Region> regionList = regionDao
                .queryBuilder()
                .where(new StringCondition(
                        "_ID IN (SELECT REGION FROM PHARMACY GROUP BY REGION)"))
                .orderAsc(com.junar.searchpharma.dao.RegionDao.Properties.Name)
                .list();
        return regionList;
    }

    public void addDatasetCacheControl() {
        CacheControl cacheControl = new CacheControl();
        Calendar c = Calendar.getInstance();
        cacheControl.setLastUpdate(c.getTime());

        this.cacheControlDao.insert(cacheControl);
    }

    public Boolean isFirstPopulate() {
        long count = this.cacheControlDao.count();
        return (count == 0) ? true : false;
    }

    public void cacheRegionList(List<Region> list) {
        this.regionDao.insertInTx(list);
    }

    public void cacheCommuneList(List<Commune> list) {
        this.communeDao.insertInTx(list);
    }

    public void cachePharmaList(List<Pharmacy> list) {
        this.pharmaDao.insertInTx(list);
    }

    public void cleanCachePharmaList() {
        this.pharmaDao.deleteAll();
    }

    public long getCachePharmaCount() {
        return this.pharmaDao.count();
    }

    public List<Pharmacy> getPharmaList() {
        return this.pharmaDao.queryBuilder().list();
    }

    public List<Pharmacy> getPharmaByMonthDay(Integer month, Integer day) {
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        return this.pharmaDao
                .queryBuilder()
                .where(PharmacyDao.Properties.Month.eq(month),
                        PharmacyDao.Properties.Day.eq(day)).list();
    }

    public List<Pharmacy> getPharmaByCommune(String commune) {
        return this.pharmaDao.queryBuilder()
                .where(PharmacyDao.Properties.Commune.eq(commune)).list();
    }

    public Pharmacy getPharmaById(long id) {
        return pharmaDao.queryBuilder().where(PharmacyDao.Properties.Id.eq(id))
                .unique();
    }
}