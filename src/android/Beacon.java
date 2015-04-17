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
import net.nopattern.cordova.beacon.service.RangingService;

/**
 * This class echoes a string called from JavaScript.
 */
public class Beacon extends CordovaPlugin {

  BeaconConstant bConstant;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);

    Intent intent = new Intent(bConstant.BOOT_RANGING_INTENT);
    cordova.getActivity().sendBroadcast(intent);
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (action.equals("coolMethod")) {
      String message = args.getString(0);
      this.coolMethod(message, callbackContext);
      return true;
    }
    return false;
  }

  private void coolMethod(String message, CallbackContext callbackContext) {
    if (message != null && message.length() > 0) {
      callbackContext.success(message);
    } else {
      callbackContext.error("Expected one non-empty string argument.");
    }
  }
}
