package com.junar.searchpharma.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.junar.searchpharma.Pharmacy;
import com.junar.searchpharma.Complaint;

import com.junar.searchpharma.dao.PharmacyDao;
import com.junar.searchpharma.dao.ComplaintDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig pharmacyDaoConfig;
    private final DaoConfig complaintDaoConfig;

    private final PharmacyDao pharmacyDao;
    private final ComplaintDao complaintDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        pharmacyDaoConfig = daoConfigMap.get(PharmacyDao.class).clone();
        pharmacyDaoConfig.initIdentityScope(type);

        complaintDaoConfig = daoConfigMap.get(ComplaintDao.class).clone();
        complaintDaoConfig.initIdentityScope(type);

        pharmacyDao = new PharmacyDao(pharmacyDaoConfig, this);
        complaintDao = new ComplaintDao(complaintDaoConfig, this);

        registerDao(Pharmacy.class, pharmacyDao);
        registerDao(Complaint.class, complaintDao);
    }
    
    public void clear() {
        pharmacyDaoConfig.getIdentityScope().clear();
        complaintDaoConfig.getIdentityScope().clear();
    }

    public PharmacyDao getPharmacyDao() {
        return pharmacyDao;
    }

    public ComplaintDao getComplaintDao() {
        return complaintDao;
    }

}
