package com.example.xaviertconcepcion.filemanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.storage.OnObbStateChangeListener;
import android.os.storage.StorageManager;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String TAG2 = "ObbMount";

    private static String mObbPath;
    private String mMountedObbPath;

    private StorageManager mSM;


    @BindView(R.id.lbl_mnt_status)
    TextView lblMntStatus;

    @BindView(R.id.lbl_status)
    TextView lblStatus;

    @BindView(R.id.lbl_path)
    TextView lblPath;

    @BindView(R.id.lbl_mount_time)
    TextView lblMountTime;

    @BindView(R.id.btn_read_files)
    Button btnReadFiles;

    @BindView(R.id.lbl_filelist_row)
    TextView lblFilelistRow;

    @BindView(R.id.btn_read_db)
    Button btnReadDb;

    @BindView(R.id.lbl_db_rows)
    TextView lblDbRows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ObbState state = (ObbState) getLastNonConfigurationInstance();

        if (state != null) {
            mSM = state.storageManager;
            lblStatus.setText(state.status);
            lblPath.setText(state.path);
        } else {
            mSM = (StorageManager) getApplicationContext().getSystemService(Context.STORAGE_SERVICE);
            Toast.makeText(this, "Storage Manager has been initialized", Toast.LENGTH_SHORT).show();
        }

        // set the obb path as suggested by Google
        // get the path from utilities?
        // main OBB
        String mainObbPath = null;

        try {
            mainObbPath = getMainObbPathFromLib();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mainObbPath == null) {
            Toast.makeText(this, "Obb File does exist. Must download", Toast.LENGTH_LONG).show();
            return;
        }

        mObbPath = mainObbPath;
        Log.d(TAG2, "obbpath: " + mainObbPath);

        btnReadFiles.setEnabled(false);
        btnReadDb.setEnabled(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkMntContents();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        logDatabase();
//        test();
    }

    OnObbStateChangeListener mEventListener = new OnObbStateChangeListener() {
        @Override
        public void onObbStateChange(String path, int state) {
            Log.d(TAG2, "Event Listener triggered: " + state + "\n" + path);
            lblStatus.setText(String.valueOf(state));
            if (state == OnObbStateChangeListener.MOUNTED) {
                mMountedObbPath = mSM.getMountedObbPath(mObbPath);
                lblPath.setText(mMountedObbPath);
                btnReadFiles.setEnabled(true);
                storeMountedUrlToPrefs(mMountedObbPath);
                btnReadDb.setEnabled(true);
            } else {
                lblPath.setText("");
//                btnReadFiles.setEnabled(false);
                resetFileListing();
                resetRowListing();
                mMountedObbPath = null;
                clearMountedUrlFromPrefs();
            }

            checkMntContents();
        }
    };

    private void checkMntContents() {
        String mntObbDirString = "/mnt/obb";
        File mntObbDir = new File(mntObbDirString);

        Log.d(TAG2, "mntObbDir: " + mntObbDir.getPath() );

        if (mntObbDir.exists()) {
            // show directory contents
            String tree = "";
            if (mntObbDir.listFiles().length > 0) {
                for (File file : mntObbDir.listFiles()) {
                    tree += file.getName() + "\n";
                }
            } else {
                tree = "No files found.";
            }

            lblMntStatus.setText(tree);
        } else {
            lblMntStatus.setText("Mnt folder does not exist");
        }
    }

    // This method grabs its database from the assets folder
    // TODO: This shall be replaced by accessing the database from the OBB
    private void logDatabase() {
        Log.d(TAG, "Log Database is running");
        MyDatabase mDb = new MyDatabase(this);
        SQLiteDatabase db = mDb.getReadableDatabase();
        Cursor cursor = db.query("tbl_directory", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Log.d(TAG, "name: " + cursor.getString(cursor.getColumnIndex("name")));
        }

        cursor.close();
    }

    private void test() {
        // get expansion files
        Log.d(TAG, "Test method is running");

        int mainVersion  = ExpansionApkUtils.getExpansionFileVersion(this, ExpansionApkUtils.EXP_MAIN);
        int patchVersion = ExpansionApkUtils.getExpansionFileVersion(this, ExpansionApkUtils.EXP_PATCH);
        String[] files = ExpansionApkUtils.getExpansionFiles(this, 1, patchVersion);

        Log.d(TAG, "Test files count: " + files.length);

        for (String name : files) {
            Log.d(TAG, "filename: " + name);
        }

        try {
//            ZipResourceFile expansionFiles = APKExpansionSupport.getAPKExpansionZipFile(this, 1, 0);
            ZipResourceFile expansionFiles = APKExpansionSupport.getResourceZipFile(files);
            Log.d(TAG, "expansionFiles: " + String.valueOf(expansionFiles != null));

            if (expansionFiles != null) {
                String mainExpPath = expansionFiles.getAllEntries()[0].getZipFileName();
                AssetFileDescriptor fd = expansionFiles.getAssetFileDescriptor(mainExpPath + "/db_sample.sql");
                FileInputStream fileInputStream = fd.createInputStream();
                Log.d(TAG, "File InputStream read: " + fileInputStream.read());
//                InputStream fileStream = expansionFiles.getInputStream(mainExpPath + "/db_sample.sql");

                // for compatibility
//                StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.btn_mount_obb)
    void mountClickListener() {
        Log.d(TAG2, "Mount clicked");
        long startTime = System.currentTimeMillis();
        try {
            boolean mountStatus = mSM.mountObb(mObbPath, null, mEventListener);
            Log.d(TAG2, "Mount Click Status: " + mountStatus);
            if (mountStatus) {
                lblStatus.setText("Attempting to mount.");
            } else {
                lblStatus.setText("Failed to start mount.");
            }
        } catch (IllegalArgumentException e) {
            lblStatus.setText("OBB already mounted");
            Log.d(TAG2, "OBB already mounted");
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        lblMountTime.setText(String.format("Took: %d ms", elapsedTime));
    }

    @OnClick(R.id.btn_unmount_obb)
    void unmountClickListener() {
        Log.d(TAG2, "Unmount clicked");
        try {
            boolean unmountStatus = mSM.unmountObb(mObbPath, false, mEventListener);
            Log.d(TAG2, "Unount Click Status: " + unmountStatus);
            if (unmountStatus) {
                lblStatus.setText("Attempting unmount");
            } else {
                lblStatus.setText("Failed to start unmount");
            }
        } catch (IllegalStateException e) {
            lblStatus.setText("OBB not mounted");
            Log.d(TAG2, "OBB not mounted");
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        ObbState obbState = new ObbState(mSM, lblStatus.getText(), lblPath.getText());
        return obbState;
    }

    // Utility Methods
    private String getMainObbPathFromLib() throws IOException {
//        String[] files = ExpansionApkUtils.getExpansionFiles(this, 1, 0);
//        ZipResourceFile expansionFiles = APKExpansionSupport.getResourceZipFile(files);
//        String mainFileName =  expansionFiles.getAllEntries()[0].getZipFileName();
//        Log.d(TAG2, String.valueOf(expansionFiles.getAllEntries()[0].mUncompressedLength));
//        Log.d(TAG2, String.valueOf(expansionFiles.getAllEntries()[1].mUncompressedLength));
//        return mainFileName;
        return ExpansionApkUtils.getExpansionFiles(this, 1, 0)[0];
    }

    @OnClick(R.id.btn_read_files)
    void readDatabaseListener() {
        if (mMountedObbPath == null) return;

        // need to read the directory here first
        Log.d(TAG2, "Mounted Obb Path: " + mMountedObbPath);

        File mountedObbFolder = new File(mMountedObbPath);

        if (mountedObbFolder.isDirectory()) {
            File[] listOfFiles = mountedObbFolder.listFiles();

            String output = "";

            for (File file : listOfFiles) {
                output += file.getName() + "\n";
            }

            lblFilelistRow.setText(output);
        } else {
            lblFilelistRow.setText("Mount OBB first");
        }
    }

    private void resetFileListing() {
        Log.d(TAG2, "Reseting file listing");
        btnReadFiles.setEnabled(false);
        lblFilelistRow.setText("");
    }

    // Accessing the database from the given path
    @OnClick(R.id.btn_read_db)
    void readDbListener() {
        // This is most likely getting the rows of a table
        // check first if the obb is mounted
        if (Utils.isObbMounted(this)) {
            MyDatabase mDb = new MyDatabase(this);
            SQLiteDatabase db = mDb.getReadableDatabase();
            String dbRows = "";

            Cursor cursor = db.query("tbl_directory", null, null, null, null, null, null);

            while (cursor.moveToNext()) {
                dbRows += cursor.getString(cursor.getColumnIndex("name")) + "\n";
            }

            cursor.close();

            lblDbRows.setText(dbRows);
        } else {
            Toast.makeText(this, "Mount OBB first", Toast.LENGTH_SHORT).show();
        }
    }

    void resetRowListing() {
        btnReadDb.setEnabled(false);
        lblDbRows.setText("");
    }

    // Shared Preferences
    private void storeMountedUrlToPrefs(String mountedObbPath) {
        SharedPreferences sp = getSharedPreferences(C.SHARED_PREFERENCE_FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putString(C.MOUNTED_OBB_FILEPATH, mountedObbPath);
        e.apply();
    }

    private void clearMountedUrlFromPrefs() {
        SharedPreferences sp = getSharedPreferences(C.SHARED_PREFERENCE_FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putString(C.MOUNTED_OBB_FILEPATH, "");
        e.apply();
    }

}
