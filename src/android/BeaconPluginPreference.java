package net.nopattern.cordova.beacon;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BeaconPluginPreference {

  private Context preferenceContext;

  public static final String UUID_PREFERENCE = "BeaconProximityUUID";
  public static final String ERLN_PREFERENCE = "BeaconEnterRegionLocalNotification";
  public static final String ERLN_PREFERENCE_DEFAULT = "You did enter beacon region!";
  public static final String ETLN_PREFERENCE = "BeaconExpireTimeLocalNotification";
  public static final int ETLN_PREFERENCE_DEFAULT = 60 * 60 * 1000;

  public BeaconPluginPreference(Context context) {
    preferenceContext = context;
  }

  public void setPreference(String key, String value) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preferenceContext);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(key, value);
    editor.commit();
  }

  public void setPreference(String key, long value) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preferenceContext);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putLong(key, value);
    editor.commit();
  }

  public String getPreference(String key, String defaultValue) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preferenceContext);
    return preferences.getString(key, defaultValue);
  }

  public long getPreference(String key, long defaultValue) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preferenceContext);
    return preferences.getLong(key, defaultValue);
  }
}
