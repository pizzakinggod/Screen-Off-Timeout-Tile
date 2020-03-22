package com.instantpood.screenofftimeout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.os.IBinder;
import android.provider.Settings;
import android.service.quicksettings.TileService;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class ScreenOffTimeOutService extends TileService {
    private Integer[] images= {R.drawable.ic_screen_lock_portrait};
    private int time = 0;

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        // Change according to current settings
        Icon icon  = Icon.createWithResource(getApplicationContext(),images[0]);

        getQsTile().setIcon(icon);
        getQsTile().updateTile();

        try {
            int cur = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
            Toast.makeText(this, "Screen Timeout:"+Integer.toString(cur), Toast.LENGTH_SHORT).show();
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick()
    {
        Icon icon  = Icon.createWithResource(getApplicationContext(),images[0]);

        getQsTile().setIcon(icon);
        getQsTile().updateTile();

        if (Settings.System.canWrite(this)) {
            int time = 15000;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT, time);
            Toast.makeText(this, "Screen Timeout:"+Integer.toString(time), Toast.LENGTH_SHORT).show();
        } else {
            // if permission not granted
            // Open write permission menu
            startActivityAndCollapse(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS));
            Toast.makeText(this, "Please Grant Permission", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

}
