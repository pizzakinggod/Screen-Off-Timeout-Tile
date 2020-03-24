package com.instantpood.screenofftimeout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.service.quicksettings.TileService;
import android.widget.Toast;


public class ScreenOffTimeOutService extends TileService {

    private Integer[] images= {
            R.drawable.ic_screen15s,
            R.drawable.ic_screen30s,
            R.drawable.ic_screen1min,
            R.drawable.ic_screen2min,
            R.drawable.ic_screen5min,
            R.drawable.ic_screen10min,
            R.drawable.ic_screen30min,
            R.drawable.ic_screenindef
    };

    SharedPreferences spref;

    private int getOptionFromValues(int milisec){
        int ret;
        switch(milisec){
            case 15000: ret = 0; break;
            case 30000: ret = 1; break;
            case 60000: ret = 2; break;
            case 120000: ret = 3; break;
            case 300000: ret = 4; break;
            case 600000: ret = 5; break;
            case 1800000: ret = 6; break;
            default: ret = 7;
        }
        return ret;
    }

    private int getValuesFromOption(int option){
        int ret;
        switch(option){
            case 0: ret=15000; break;
            case 1: ret=30000; break;
            case 2: ret=60000; break;
            case 3: ret=120000; break;
            case 4: ret=300000; break;
            case 5: ret=600000; break;
            case 6: ret=1800000; break;
            default: ret = Integer.MAX_VALUE;
        }
        return ret;
    }

    private int getNextOption(int curOption){
        if (spref==null){
            spref = PreferenceManager.getDefaultSharedPreferences(this);
        }

        int nextOption = (curOption+1)%images.length;
        String optionKey = String.format("%s_checkbox", Integer.toString(nextOption));
        boolean optionValue = spref.getBoolean(optionKey, true);

        if (optionValue)
            return nextOption;
        else return getNextOption(nextOption);

    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        // 1. find current state
        int currentValue = -1;
        try {
            currentValue = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        int curOption = getOptionFromValues(currentValue);

        // 2. Change icon
        Icon icon  = Icon.createWithResource(getApplicationContext(),images[curOption]);
        getQsTile().setIcon(icon);
        getQsTile().updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();

        // 1. find current state
        int currentValue = -1;
        try {
            currentValue = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        int curOption = getOptionFromValues(currentValue);

        // 2. Change icon
        Icon icon  = Icon.createWithResource(getApplicationContext(),images[curOption]);
        getQsTile().setIcon(icon);
        getQsTile().updateTile();

    }

    @Override
    public void onClick()
    {

        if (Settings.System.canWrite(getApplicationContext())) {
            // 1. find current state
            int currentValue;
            try {
                currentValue = Settings.System.getInt(getContentResolver(),
                        Settings.System.SCREEN_OFF_TIMEOUT);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                currentValue = -1;
            }
            int curOption = getOptionFromValues(currentValue);

            // 2. get next state
            curOption = getNextOption(curOption);

            // 3. Update
            Icon icon  = Icon.createWithResource(getApplicationContext(),images[curOption]);
            getQsTile().setIcon(icon);
            getQsTile().updateTile();

            Settings.System.putInt(getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT, getValuesFromOption(curOption));
        } else {
            // if permission not granted
            // Open write permission menu
            Intent writeSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS); // Settings.ACTION_MANAGE_WRITE_SETTINGS
            writeSettingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            writeSettingsIntent.setData(Uri.parse("package:"+getPackageName()));
            startActivityAndCollapse(writeSettingsIntent);
            Toast.makeText(this, R.string.toast_ask_permission, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
}
