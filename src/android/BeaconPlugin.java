package net.nopattern.cordova.beacon;

import android.content.Intent;

import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.Exception;
import java.lang.Object;
import java.lang.Runnable;
import java.lang.String;
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import net.nopattern.cordova.beacon.BeaconPluginPreference;
import net.nopattern.cordova.beacon.BeaconPluginConstant;

/**
 * This class echoes a string called from JavaScript.
 */
public class BeaconPlugin extends CordovaPlugin {

  // Reference to the web view for static access
  private static CordovaWebView webView = null;
  private BeaconPluginPreference beaconPluginPreference = null;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    BeaconPlugin.webView = super.webView;

    beaconPluginPreference = new BeaconPluginPreference(cordova.getActivity().getApplicationContext());

    Intent intent = new Intent(BeaconPluginConstant.BOOT_MONITORING_SERVICE_INTENT);
    cordova.getActivity().sendBroadcast(intent);
  }

  public void onDeviceReady() {
    intentHandler(cordova.getActivity().getIntent());
  }

  @Override
  public void onNewIntent(Intent intent) {
    intentHandler(intent);
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    Log.i(BeaconPluginConstant.LOG_TAG, "BeaconPlugin :: execute '" + action + "'");

    if (action.equals("deviceready")) {
      this.onDeviceReady();
      return true;
    } else if (action.equals("receiveBeaconBehavior")) {
      this.receiveBeaconBehavior(callbackContext);
      return true;
    } else if (action.equals("clearBeaconBehavior")) {
      this.clearBeaconBehavior(callbackContext);
      return true;
    } else if (action.equals("setBeaconProximityUUID")) {
      this.setBeaconProximityUUID(args.getString(0), callbackContext);
      return true;
    } else if (action.equals("setEnterRegionLocalNotification")) {
      this.setEnterRegionLocalNotification(args.getString(0), callbackContext);
      return true;
    }
    return false;
  }

  public void setBeaconProximityUUID(String proximityUUID, CallbackContext callbackContext) {
    Log.i(BeaconPluginConstant.LOG_TAG, "BeaconPlugin :: setBeaconProximityUUID '" + proximityUUID + "'");

    beaconPluginPreference.setPreference(BeaconPluginConstant.UUID_PREFERENCE, proximityUUID);

    Intent intent = new Intent(BeaconPluginConstant.REBOOT_MONITORING_SERVICE_INTENT);
    cordova.getActivity().sendBroadcast(intent);
  }

  public void setEnterRegionLocalNotification(String message, CallbackContext callbackContext) {
    Log.i(BeaconPluginConstant.LOG_TAG, "BeaconPlugin :: setEnterRegionLocalNotification '" + message + "'");

    beaconPluginPreference.setPreference(BeaconPluginConstant.ERLN_PREFERENCE, message);
  }

  private void intentHandler(Intent intent) {
    String action = intent.getAction();

    if( action.equals( BeaconPluginConstant.LOCAL_NOTIFICATION_INTENT ) ) {
      fireEvent("didReceiveLocalNotification");
    }
  }

  private void fireEvent( String eventName ) {
    final String js = "window.cordova.plugins.Beacon.fireEvent(" +
      "\"" + eventName + "\")";

    cordova.getActivity().runOnUiThread(new Runnable() {
      public void run() {
        webView.loadUrlIntoView("javascript:" + js, true);
      }
    });
  }

  private Set<String> hashBeaconBehavior() {
    Set<String> hash = new HashSet<String>();
    hash.add(BeaconPluginConstant.BEAP_UUID_PREFERENCE);
    hash.add(BeaconPluginConstant.BEAP_IDEN_PREFERENCE);
    hash.add(BeaconPluginConstant.BEAP_MAJOR_PREFERENCE);
    hash.add(BeaconPluginConstant.BEAP_MINOR_PREFERENCE);

    return hash;
  }

  private void receiveBeaconBehavior(CallbackContext callbackContext) {
    Log.i(BeaconPluginConstant.LOG_TAG, "BeaconPlugin :: receiveBeaconBehavior");

    Set<String> keys = this.hashBeaconBehavior();
    Iterator<String> iterator = keys.iterator();
    String preferenceName;
    Object[] preferenceData;
    String[] parts;
    JSONArray elements = new JSONArray();
    JSONObject element = new JSONObject();

    while (iterator.hasNext()) {
      preferenceName = iterator.next();

      preferenceData = beaconPluginPreference.getPreference(preferenceName, new HashSet<String>()).toArray();
      for (int i = 0; i < preferenceData.length; i++) {
        parts = preferenceData[i].toString().split(":");

        try {
          element = elements.optJSONObject(i);
          if (element == null) {
            element = new JSONObject();
            element.put("date", parts[0].substring(0, 10));
            elements.put(element);
          }

          preferenceName = preferenceName.replace("BeaconBehavior", "");
          preferenceName = Character.toLowerCase(preferenceName.charAt(0)) + preferenceName.substring(1);
          element.put(preferenceName, parts[1]);
        } catch(JSONException e) {
        }
      }
    }

    callbackContext.success(elements.toString());
  }

  private void clearBeaconBehavior(CallbackContext callbackContext) {
    Set<String> keys = this.hashBeaconBehavior();
    Iterator<String> iterator = keys.iterator();
    String preferenceName;

    while (iterator.hasNext()) {
      preferenceName = iterator.next();
      beaconPluginPreference.setPreference(preferenceName, new HashSet<String>());
    }

    callbackContext.success("success");
  }

}
