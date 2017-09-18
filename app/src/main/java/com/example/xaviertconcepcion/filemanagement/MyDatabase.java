package com.example.xaviertconcepcion.filemanagement;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by xavier.t.concepcion on 12/09/2017.
 */

public class MyDatabase extends SQLiteAssetHelper {

    // suggested format by the library documentation

    public static final String DATABASE_NAME = "db_sample.sqlite";
    public static final int DATABASE_VERSION = 1;

    public MyDatabase(Context context) {
        super(new DatabaseContext(context), DATABASE_NAME, null, DATABASE_VERSION);
    }

}
