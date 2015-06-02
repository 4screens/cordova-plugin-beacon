package net.nopattern.cordova.beacon;

public class BeaconPluginConstant {
  public static final String LOG_TAG = "CordovaBeaconPlugin";

  public static final String STATE_CHANGED_INTENT = "android.bluetooth.adapter.action.STATE_CHANGED";
  public static final String BOOT_COMPLETED_INTENT = "android.intent.action.BOOT_COMPLETED";
  public static final String BOOT_RANGING_SERVICE_INTENT = "net.nopattern.cordova.beacon.BOOT_RANGING_SERVICE";
  public static final String BOOT_MONITORING_SERVICE_INTENT = "net.nopattern.cordova.beacon.BOOT_MONITORING_SERVICE";
  public static final String REBOOT_MONITORING_SERVICE_INTENT = "net.nopattern.cordova.beacon.REBOOT_MONITORING_SERVICE";
  public static final String BEACON_APPEARED_INTENT = "net.nopattern.cordova.beacon.BEACON_APPEARED";
  public static final String BEACON_BEHAVIOR_INTENT = "net.nopattern.cordova.beacon.BEACON_BEHAVIOR";
  public static final String LOCAL_NOTIFICATION_INTENT = "net.nopattern.cordova.beacon.LOCAL_NOTIFICATION";

  public static final String PROXIMITY_UUID_EXTRA = "net.nopattern.cordova.beacon.extra.PROXIMITY_UUID";
  public static final String IDENTIFIER_EXTRA = "net.nopattern.cordova.beacon.extra.IDENTIFIER";
  public static final String MAJOR_EXTRA = "net.nopattern.cordova.beacon.extra.MAJOR";
  public static final String MINOR_EXTRA = "net.nopattern.cordova.beacon.extra.MINOR";

  public static final String BEAP_UUID_PREFERENCE = "BeaconBehaviorProximityUUID";
  public static final String BEAP_IDEN_PREFERENCE = "BeaconBehaviorIdentifier";
  public static final String BEAP_MAJOR_PREFERENCE = "BeaconBehaviorMajor";
  public static final String BEAP_MINOR_PREFERENCE = "BeaconBehaviorMinor";

  public static final String UUID_PREFERENCE = "BeaconProximityUUID";
  public static final String ERLN_PREFERENCE = "BeaconEnterRegionLocalNotification";
  public static final String ERLN_PREFERENCE_DEFAULT = "You did enter beacon region!";
  public static final String ETLN_PREFERENCE = "BeaconExpireTimeLocalNotification";
  public static final int ETLN_PREFERENCE_DEFAULT = 60 * 60 * 1000;
}
