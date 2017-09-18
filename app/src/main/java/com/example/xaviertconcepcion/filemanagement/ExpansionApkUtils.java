package com.example.xaviertconcepcion.filemanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Vector;

/**
 * Created by xavier.t.concepcion on 12/09/2017.
 */

public class ExpansionApkUtils {

    public static final String TAG = "MainActivity";

    public static final String EXP_PATH = "/Android/obb/";

    public static final int EXP_MAIN_INDEX = 0;
    public static final int EXP_MAIN_PATCH = 1;

    public static final String EXP_MAIN  = "main";
    public static final String EXP_PATCH = "patch";

    public ExpansionApkUtils() {

    }

    /**
     * @param context -- caller
     * @param mainVersion -- version of the main obb file
     * @param patchVersion -- version of the path obb file
     * @return an array containing the file paths to both of expansion files
     */
    public static String[] getExpansionFiles(Context context, int mainVersion, int patchVersion) {
        String packageName = context.getPackageName();
        Vector<String> ret = new Vector<String>();

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File root = Environment.getExternalStorageDirectory();
            File expPath = new File(root.toString() + EXP_PATH + packageName);

            Log.d(TAG, "exPath path: " + root.toString() + EXP_PATH + packageName);
            Log.d(TAG, "expPath exists?: " + expPath.exists());

            if (expPath.exists()) {
                if (mainVersion > 0) {
                    String strMainPath = expPath + File.separator + "main." + mainVersion + "." +
                            packageName + ".obb";
                    Log.d(TAG, strMainPath);
                    File main = new File(strMainPath);

                    if (main.isFile()) {
                        ret.add(strMainPath);
                    }
                }

                if (patchVersion > 0) {
                    String strPatchPath = expPath + File.separator + "patch." + patchVersion + "." +
                            packageName + ".obb";
                    Log.d(TAG, strPatchPath);
                    File patch = new File(strPatchPath);

                    if (patch.isFile()) {
                        ret.add(strPatchPath);
                    }
                }
            }
        }

        String[] retArray = new String[ret.size()];
        ret.toArray(retArray);

        return retArray;
    }

    public static void storeExpansionFileVersions(Context context) {
        // store into shared preferences the versions of the expansion files
        SharedPreferences sp = context.getSharedPreferences(C.SHARED_PREFERENCE_FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putInt(EXP_MAIN, 1);
        e.putInt(EXP_PATCH, 0);
        e.apply();
    }

    public static int getExpansionFileVersion(Context context, String patchType) {
        // get the version here through shared preferences
        SharedPreferences sp = context.getSharedPreferences(C.SHARED_PREFERENCE_FILENAME, Context.MODE_PRIVATE);
        return sp.getInt(patchType, 0);
    }

    public static int[] getExpansionFileVersion(Context context) {
        // get the version here through shared preferences
        return new int[] {};
    }

}
