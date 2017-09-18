package com.example.xaviertconcepcion.filemanagement;

import com.google.android.vending.expansion.downloader.impl.DownloaderService;

/**
 * Created by xavier.t.concepcion on 18/09/2017.
 */

public class SampleDownloaderService extends DownloaderService {

    private static final String PUBLIC_KEY = "base_64_public_key_from_play_store"; // From Play Store
    private static final byte[] SALT = new byte[] { };

    @Override
    public String getPublicKey() {
        return null;
    }

    @Override
    public byte[] getSALT() {
        return new byte[0];
    }

    @Override
    public String getAlarmReceiverClassName() {
        return null;
    }

}
