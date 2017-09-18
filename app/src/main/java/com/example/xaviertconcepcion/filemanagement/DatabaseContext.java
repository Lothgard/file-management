package com.example.xaviertconcepcion.filemanagement;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
/**
 * Created by xavier.t.concepcion on 12/09/2017.
 */

public class DatabaseContext extends ContextWrapper {

    private static final String TAG = "DatabaseContext";

    public DatabaseContext(Context context) {
        super(context);
    }

    @Override
    public File getDatabasePath(String name) {
        SharedPreferences sp = getSharedPreferences(C.SHARED_PREFERENCE_FILENAME, Context.MODE_PRIVATE);
        String newDbFilePath = "";

        if (sp.contains(C.MOUNTED_OBB_FILEPATH)) {
            newDbFilePath = sp.getString(C.MOUNTED_OBB_FILEPATH, "");
        }

        return new File(newDbFilePath);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return super.openOrCreateDatabase(name, mode, factory, errorHandler);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
    }
}
