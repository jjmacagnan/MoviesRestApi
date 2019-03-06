package com.jjmacagnan.moviesrestapi.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.jjmacagnan.moviesrestapi.R;

public class NetworkStateReceiver extends BroadcastReceiver {
    private static boolean online = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = manager.getActiveNetworkInfo();
        if (ni == null || ni.getState() != NetworkInfo.State.CONNECTED) {
            if (online)
                Toast.makeText(context, R.string.nao_conectado, Toast.LENGTH_SHORT).show();
            online = false;
        } else {
            if (!online)
                Toast.makeText(context, R.string.conectado, Toast.LENGTH_SHORT).show();
            online = true;
        }
    }

    public static boolean isNetworkAvailable() {
        return online;
    }
}
