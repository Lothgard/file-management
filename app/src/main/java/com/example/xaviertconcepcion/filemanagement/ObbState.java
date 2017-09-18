package com.example.xaviertconcepcion.filemanagement;

import android.os.storage.StorageManager;

/**
 * Created by xavier.t.concepcion on 15/09/2017.
 */

public class ObbState {

    // The storage manager
    public StorageManager storageManager;
    public CharSequence status;
    public CharSequence path;

    public ObbState(StorageManager storageManager, CharSequence status, CharSequence path) {
        this.storageManager = storageManager;
        this.status = status;
        this.path = path;
    }

}
