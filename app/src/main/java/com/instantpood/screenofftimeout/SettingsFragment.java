package com.instantpood.screenofftimeout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref,rootKey);
        updateEnableState(getPreferenceManager().getSharedPreferences());
    }

    private void enableAllCheckBoxes() {
        for (int i=0; i < ScreenOffTimeOutService.optionCount; i++) {
            String optionKey = String.format("%s_checkbox", i);
            getPreferenceScreen().findPreference(optionKey).setEnabled(true);
        }
    }

    private void updateEnableState(SharedPreferences spref) {
        ArrayList<String> checkedOption = new ArrayList<>(2);
        for (int i=0; i < ScreenOffTimeOutService.optionCount; i++) {
            String optionKey = String.format("%s_checkbox", i);
            boolean optionValue = spref.getBoolean(optionKey, false);
            if (optionValue) checkedOption.add(optionKey);
        }
        if (checkedOption.size() == 1) {
            // 클릭후에 하나 남으면 남은거 비활성화
            getPreferenceScreen().findPreference(checkedOption.get(0)).setEnabled(false);
        } else {
            enableAllCheckBoxes();
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        updateEnableState(preference.getSharedPreferences());
        return super.onPreferenceTreeClick(preference);
    }
}
