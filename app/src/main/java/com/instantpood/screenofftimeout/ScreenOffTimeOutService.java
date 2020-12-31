package com.instantpood.screenofftimeout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import androidx.preference.Preference;

import static android.util.Log.v;


public class ScreenOffTimeOutService extends TileService {

    public static final int optionCount = 8;

    private Integer[] screenTimeoutIcons = {
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

    private int getOptionFromTimeValue(int milisec){
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

    private int getTimeValueFromOption(int option){
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

    // 현재 옵션을 기반으로 다음 옵션을 결정
    private int getNextOption(int curOption){
        if (spref==null){
            spref = PreferenceManager.getDefaultSharedPreferences(this);
        }

        int nextOption = (curOption+1)% screenTimeoutIcons.length;
        String optionKey = String.format("%s_checkbox", Integer.toString(nextOption));
        boolean optionValue = spref.getBoolean(optionKey, true);

        if (optionValue)
            return nextOption;
        else return getNextOption(nextOption);

    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        updateIcon(getOptionFromTimeValue(this.getCurrentValue()));
    }

    // 옵션에 맞는 아이콘으로 변경함
    private void updateIcon(int currentOption) {
        Icon icon  = Icon.createWithResource(getApplicationContext(), screenTimeoutIcons[currentOption]);
        getQsTile().setIcon(icon);
        getQsTile().updateTile();
    }

    // 현재 설정된 시간 값을 가져옴
    private int getCurrentValue() {
        int currentValue = -1;
        try {
            currentValue = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        int curOption = getOptionFromTimeValue(currentValue);
        return currentValue;
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateIcon(getOptionFromTimeValue(this.getCurrentValue()));
    }

    @Override
    public void onClick()
    {

        // 설정 쓰기 권한이 있으면
        if (Settings.System.canWrite(getApplicationContext())) {
            // 현재 옵션을 찾는다.
            int curOption = getOptionFromTimeValue(getCurrentValue());

            // 다음 옵션을 찾는다.
            curOption = getNextOption(curOption);

            // 아이콘 업데이트
            updateIcon(curOption);

            // 실제 설정 값도 변경한다.
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT, getTimeValueFromOption(curOption));
        } else {
            // if permission not granted
            // Open write permission menu
            Toast.makeText(getApplicationContext(), R.string.toast_ask_permission, Toast.LENGTH_LONG).show();
            (new Handler())
                    .postDelayed(
                            new Runnable() {
                                public void run() {
                                    // launch your activity here
                                    Intent writeSettingsIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS); // Settings.ACTION_MANAGE_WRITE_SETTINGS
                                    writeSettingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    writeSettingsIntent.setData(Uri.parse("package:"+getPackageName())); // give package name to open specific settings screen
                                    startActivityAndCollapse(writeSettingsIntent);
                                }
                            }, 1000);


        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
}
