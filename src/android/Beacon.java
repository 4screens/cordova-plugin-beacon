package net.nopattern.cordova.beacon;

import android.content.Intent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.Runnable;
import java.lang.String;
import java.util.UUID;

import net.nopattern.cordova.beacon.BeaconPluginPreference;
import net.nopattern.cordova.beacon.BeaconConstant;

/**
 * This class echoes a string called from JavaScript.
 */
public class Beacon extends CordovaPlugin {

  // Reference to the web view for static access
  private static CordovaWebView webView = null;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    Beacon.webView = super.webView;

    BeaconPluginPreference beaconPluginPreference = new BeaconPluginPreference(cordova.getActivity().getApplicationContext());
    beaconPluginPreference.setPreference( BeaconConstant.UUID_PREFERENCE , preferences.getString( BeaconConstant.UUID_PREFERENCE, null ));
    beaconPluginPreference.setPreference( BeaconConstant.ERLN_PREFERENCE , preferences.getString( BeaconConstant.ERLN_PREFERENCE, null ));

    Intent intent = new Intent( BeaconConstant.BOOT_MONITORING_INTENT);
    cordova.getActivity().sendBroadcast(intent);
  }

  public void onDeviceReady() {
    intentHandler(cordova.getActivity().getIntent());
  }

  @Override
  public void onNewIntent(Intent intent) {
    intentHandler(intent);
  }

  private void intentHandler(Intent intent) {
    String action = intent.getAction();

    if( action.equals( BeaconConstant.NOTIFICATION_INTENT ) ) {
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

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (action.equals("deviceready")) {
      this.onDeviceReady();
      return true;
    }
    return false;
  }
}
