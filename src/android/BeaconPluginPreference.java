package net.nopattern.cordova.beacon;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.lang.Long;
import java.lang.String;

import net.nopattern.cordova.beacon.BeaconPluginConstant;

public class BeaconPluginPreference {

  private SharedPreferences preferences;
  private SharedPreferences.Editor preferencesEditor;

  public BeaconPluginPreference(Context context) {
    preferences = PreferenceManager.getDefaultSharedPreferences(context);
    preferencesEditor = preferences.edit();
  }

  public void setPreference(String key, String value) {
    Log.i( BeaconPluginConstant.LOG_TAG, "BeaconPluginPreference :: setString '" + key + "' = " + "'" + value + "'" );
    preferencesEditor.putString(key, value);
    preferencesEditor.commit();
  }

  public void setPreference(String key, long value) {
    Log.i( BeaconPluginConstant.LOG_TAG, "BeaconPluginPreference :: setLong '" + key + "' = " + "'" + Long.toString(value) + "'" );
    preferencesEditor.putLong(key, value);
    preferencesEditor.commit();
  }

  public String getPreference(String key, String defaultValue) {
    String result = preferences.getString(key, defaultValue);
    Log.i(BeaconPluginConstant.LOG_TAG, "BeaconPluginPreference :: getString '" + key + "' = " + "'" + result + "'");
    return result;
  }

  public long getPreference(String key, long defaultValue) {
    long result = preferences.getLong(key, defaultValue);
    Log.i( BeaconPluginConstant.LOG_TAG, "BeaconPluginPreference :: getLong '" + key + "' = " + "'" + Long.toString(result) + "'" );
    return result;
  }
}
