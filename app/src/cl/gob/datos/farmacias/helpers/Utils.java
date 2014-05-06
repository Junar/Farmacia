package cl.gob.datos.farmacias.helpers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import cl.gob.datos.farmacias.R;
import cl.gob.datos.farmacias.controller.SyncController;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.junar.searchpharma.Pharmacy;

public class Utils {

    private final static String TAG = Utils.class.getSimpleName();
    public static final int DEFAULT_JPG_QUALITY = 70;
    private final static String KEY_DAY = "day";
    private final static String KEY_MONTH = "month";
    private final static String KEY_YEAR = "year";

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

    public static String getDatePhone(Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                SyncController.PREFS_NAME, 0);
        String formatteDate = pref.getString(KEY_DAY, "") + "/"
                + pref.getString(KEY_MONTH, "") + "/"
                + pref.getString(KEY_YEAR, "");
        return formatteDate;
    }

    public static boolean resizeImageByWidth(String imagePath, int width) {
        return resizeImageByWidth(imagePath, width, DEFAULT_JPG_QUALITY);
    }

    public static boolean resizeImageByWidth(String imagePath, int width,
            int quality) {
        try {
            Options options = new BitmapFactory.Options();
            options.inScaled = false;
            options.inDither = false;
            options.inPurgeable = true;

            Bitmap image = BitmapFactory.decodeFile(imagePath, options);
            Float imageWidth = new Float(image.getWidth());
            Float imageHeight = new Float(image.getHeight());
            Float ratio = imageHeight / imageWidth;
            compressJpgImage(Bitmap.createScaledBitmap(image, width,
                    (int) (width * ratio), false), quality, imagePath);
            image.recycle();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Compress a JPG image from a file and write it in the output file
     * 
     * This method compress a JPG image from a given path with the given quality
     * and write the compressed image to the output given path
     * 
     * @param imagePath
     *            the image to compress
     * @param quality
     *            the quality used to compress
     * @param output
     *            the output file path
     * @return boolean true if it could write the image
     * @throws FileNotFoundException
     */
    public static boolean compressJpgImage(String imagePath, int quality,
            String output) {
        Options options = new BitmapFactory.Options();
        options.inScaled = false;
        // options.inSampleSize = 8;
        options.inTempStorage = new byte[32 * 1024];
        Bitmap image = BitmapFactory.decodeFile(imagePath, options);
        return compressJpgImage(image, quality, output);
    }

    public static boolean compressJpgImage(Bitmap image, int quality,
            String output) {
        try {
            Boolean result = image.compress(Bitmap.CompressFormat.JPEG,
                    quality, new FileOutputStream(output));
            System.gc();
            image.recycle();
            return result;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }
}