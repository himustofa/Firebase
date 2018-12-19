package com.apps.firebase.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "REG_TOKEN";

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, ""+token);
        storeToken(token);
    }

    private void storeToken(String token) {
        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(token);
    }
}
