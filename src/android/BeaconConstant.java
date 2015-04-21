package net.nopattern.cordova.beacon;

public class BeaconConstant {
  public static final String LOG_TAG = "Beacon";

  public static final String BOOT_COMPLETED_INTENT = "android.intent.action.BOOT_COMPLETED";
  public static final String BOOT_RANGING_INTENT = "net.nopattern.cordova.beacon.BOOT_RANGING";
  public static final String BOOT_MONITORING_INTENT = "net.nopattern.cordova.beacon.BOOT_MONITORING";
  public static final String MONITORING_APPEARED_INTENT = "net.nopattern.cordova.beacon.MONITORING_APPEARED";
  public static final String NOTIFICATION_INTENT = "net.nopattern.cordova.beacon.NOTIFICATION";
  public static final String STATE_CHANGED_INTENT = "android.bluetooth.adapter.action.STATE_CHANGED";

  public static final String EXTRA_DEVICE_ID = "net.nopattern.cordova.beacon.extra.DEVICE_ID";
  public static final String EXTRA_DEVICE_ACCURACY = "net.nopattern.cordova.beacon.extra.DEVICE_ACCURACY";
}
