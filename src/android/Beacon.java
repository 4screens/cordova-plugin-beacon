package net.nopattern.cordova.beacon;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;

import net.nopattern.cordova.beacon.BeaconConstant;

/**
 * This class echoes a string called from JavaScript.
 */
public class Beacon extends CordovaPlugin {

  BeaconConstant bConstant;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);

    Intent intent = new Intent(bConstant.BOOT_MONITORING_INTENT);
    cordova.getActivity().sendBroadcast(intent);
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (action.equals("status")) {
      this.status(callbackContext);
      return true;
    }
    return false;
  }

  private void status(CallbackContext callbackContext) {
    Intent notificationIntent = cordova.getActivity().getIntent();
    String action = notificationIntent.getAction();

    callbackContext.success("{\"notification\": " + action.equals(bConstant.NOTIFICATION_INTENT) + "}");
  }
}
