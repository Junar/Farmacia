package cl.gob.datos.farmacias.helpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import cl.gob.datos.farmacias.R;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.junar.searchpharma.Pharmacy;

public class Utils {

    private final static String TAG = Utils.class.getSimpleName();

    public static void openFragment(Fragment srcFrg, Fragment dstFragment,
            Bundle args, int idContainer, boolean addToBack) {
        openFragment(srcFrg, dstFragment, args, idContainer, addToBack, null);
    }

    public static void openFragment(Fragment srcFrg, Fragment dstFragment,
            Bundle args, int idContainer, boolean addToBack, String tag) {
        FragmentManager fragmentManager = srcFrg.getFragmentManager();
        if (args != null) {
            dstFragment.setArguments(args);
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (tag == null) {
            ft.replace(idContainer, dstFragment);
        } else {
            ft.replace(idContainer, dstFragment, tag);
        }
        if (addToBack) {
            ft.addToBackStack(tag);
        }
        ft.commit();
    }

    public static Intent createShareIntent(Context context, Pharmacy pharma) {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Farmacia: ");
        localStringBuilder.append(pharma.getName());
        localStringBuilder.append("\n");
        localStringBuilder.append("Dirección: ");
        localStringBuilder.append(pharma.getAddress());
        localStringBuilder.append("\n");
        if (pharma.getPhone().length() > 0) {
            localStringBuilder.append("Tel: ");
            localStringBuilder.append(pharma.getPhone());
            localStringBuilder.append("\n");
        }
        if (pharma.getLatitude() != 0 && pharma.getLongitude() != 0) {
            localStringBuilder.append("Ubicación: ");
            localStringBuilder.append("http://maps.google.com/?q="
                    + pharma.getLatitude() + "," + pharma.getLongitude());
            localStringBuilder.append("\n");
        }
        localStringBuilder.append(context.getString(R.string.more_pharmas)
                + context.getPackageName());
        Intent localIntent = new Intent("android.intent.action.SEND");
        localIntent.setType("text/plain");
        localIntent.putExtra("android.intent.extra.SUBJECT",
                "Farmacia recomendada");
        localIntent.putExtra("android.intent.extra.TEXT",
                localStringBuilder.toString());
        if (pharma.getPhone().length() > 0) {
            localIntent.putExtra("android.intent.extra.PHONE_NUMBER",
                    pharma.getPhone());
        }
        // localIntent.putExtra("android.intent.extra.EMAIL",
        // pharma.getEmail());
        return localIntent;

    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static boolean isGooglePlayAvailable(Context context) {
        try {
            int resultCode = GooglePlayServicesUtil
                    .isGooglePlayServicesAvailable(context);
            if (ConnectionResult.SUCCESS == resultCode) {
                Log.i(TAG, "Google Play services is available.");
                return true;
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        Log.e(TAG, "Google Play services is NOT available.");
        return false;
    }

    public static String getDatePhone() {
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formatteDate = df.format(date);
        return formatteDate;
    }
}