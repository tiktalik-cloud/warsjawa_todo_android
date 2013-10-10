package com.tiktalik.todo;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by alek on 10/10/13.
 */
public class BackendService extends IntentService {
    public static final String ADD_ITEM = "BackendService/ADD_ITEM";
    public static final String GET_ITEMS = "BackendService/GET_ITEMS";
    public static final String ERROR = "BackendService/ERROR";

    BackendAPI backendAPI;
    LocalBroadcastManager localBroadcastManager;

    public BackendService() {
        super("BackendService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        String endpointAddress;

        if ((getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) == 0) {
            endpointAddress = "http://37.233.98.185:8000/";
        } else {
            endpointAddress = "http://192.168.33.10:8000/";
        }

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setServer(endpointAddress)
                .build();

        Log.d(getClass().getName(), "endpointAddress=" + endpointAddress);

        backendAPI = restAdapter.create(BackendAPI.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        try {
            if (GET_ITEMS.equals(action)) {
                ArrayList<Item> items = backendAPI.getItems();
                Intent in = new Intent(GET_ITEMS);
                in.putParcelableArrayListExtra("items", items);
                localBroadcastManager.sendBroadcast(in);
            } else if (ADD_ITEM.equals(action)) {
                Item item = intent.getParcelableExtra("item");
                backendAPI.addItem(item);
            }
        } catch (RetrofitError exc) {
            exc.printStackTrace();
            Intent in = new Intent(ERROR);
            in.putExtra("error", exc.getMessage());
            localBroadcastManager.sendBroadcast(in);
        }
    }
}
