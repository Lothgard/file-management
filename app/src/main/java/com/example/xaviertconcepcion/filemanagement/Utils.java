package com.example.xaviertconcepcion.filemanagement;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by xavier.t.concepcion on 15/09/2017.
 */

public class Utils {

    public static final boolean isObbMounted(Context context) {
        SharedPreferences sp = context.getSharedPreferences(C.SHARED_PREFERENCE_FILENAME, Context.MODE_PRIVATE);
        return sp.contains(C.MOUNTED_OBB_FILEPATH) && !sp.getString(C.MOUNTED_OBB_FILEPATH, "").equals("");
    }

}
