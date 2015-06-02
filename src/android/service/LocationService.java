package net.nopattern.cordova.beacon.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.kontakt.sdk.android.manager.BeaconManager;
import com.kontakt.sdk.android.configuration.MonitorPeriod;
import com.kontakt.sdk.android.configuration.ForceScanConfiguration;
import com.kontakt.sdk.android.connection.OnServiceBoundListener;
import com.kontakt.sdk.android.device.BeaconDevice;
import com.kontakt.sdk.android.device.Region;
import com.kontakt.sdk.android.factory.AdvertisingPackage;
import com.kontakt.sdk.android.factory.Filters;

import java.lang.Exception;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.nopattern.cordova.beacon.BeaconPluginConstant;
import net.nopattern.cordova.beacon.BeaconPluginPreference;

public class LocationService extends Service {

  private BeaconManager beaconManager;
  private List<BeaconDevice> lastKnownBeacons = new ArrayList<BeaconDevice>();

  @Override
  public void onCreate() {
    Log.d(BeaconPluginConstant.LOG_TAG, "LocationService :: onCreate");

    beaconManager = BeaconManager.newInstance(this);
    beaconManager.setMonitorPeriod(MonitorPeriod.MINIMAL);
    beaconManager.setForceScanConfiguration(ForceScanConfiguration.DEFAULT);

    BeaconPluginPreference beaconPluginPreference = new BeaconPluginPreference(this);
    String proximityPreference = beaconPluginPreference.getPreference( BeaconPluginConstant.UUID_PREFERENCE, BeaconManager.DEFAULT_KONTAKT_BEACON_PROXIMITY_UUID.toString() );
    UUID proximityUUID = UUID.fromString(proximityPreference);

    beaconManager.addFilter(Filters.newProximityUUIDFilter(proximityUUID));
    beaconManager.registerMonitoringListener(new BeaconManager.MonitoringListener() {
      @Override
      public void onMonitorStart() {
        Log.d(BeaconPluginConstant.LOG_TAG, "MonitoringListener :: beaconManager - onMonitorStart");
      }

      @Override
      public void onMonitorStop() {
        Log.d(BeaconPluginConstant.LOG_TAG, "MonitoringListener :: beaconManager - onMonitorStop");
      }

      @Override
      public void onBeaconsUpdated(final Region region, final List<BeaconDevice> knownBeacons) {
        Log.d(BeaconPluginConstant.LOG_TAG, "MonitoringListener :: beaconManager - onBeaconsUpdated " + knownBeacons.size());
      }

      @Override
      public void onBeaconAppeared(final Region region, final BeaconDevice beaconDevice) {
        Log.i(BeaconPluginConstant.LOG_TAG, "MonitoringListener :: beaconManager - onBeaconAppeared");

        Intent intentBehavior = new Intent(BeaconPluginConstant.BEACON_BEHAVIOR_INTENT);
        intentBehavior.putExtra(BeaconPluginConstant.PROXIMITY_UUID_EXTRA, beaconDevice.getProximityUUID().toString());
        intentBehavior.putExtra(BeaconPluginConstant.IDENTIFIER_EXTRA, beaconDevice.getUniqueId());
        intentBehavior.putExtra(BeaconPluginConstant.MAJOR_EXTRA, beaconDevice.getMajor());
        intentBehavior.putExtra(BeaconPluginConstant.MINOR_EXTRA, beaconDevice.getMinor());
        getApplicationContext().sendBroadcast(intentBehavior);

        Intent intentAppeared = new Intent(BeaconPluginConstant.BEACON_APPEARED_INTENT);
        getApplicationContext().sendBroadcast(intentAppeared);
      }

      @Override
      public void onRegionEntered(final Region region) {
        Log.d(BeaconPluginConstant.LOG_TAG, "MonitoringListener :: beaconManager - onRegionEntered");
      }

      @Override
      public void onRegionAbandoned(final Region region) {
        Log.d(BeaconPluginConstant.LOG_TAG, "MonitoringListener :: beaconManager - onRegionAbandoned");
      }
    });
  }

  static boolean beaconEquals(List<BeaconDevice> lastKnownBeacons, BeaconDevice knownBeacon) {
    for (BeaconDevice lastKnownBeacon : lastKnownBeacons) {
      if (
        lastKnownBeacon.getProximityUUID().toString().equals(knownBeacon.getProximityUUID().toString()) &&
        lastKnownBeacon.getMajor() == knownBeacon.getMajor() &&
        lastKnownBeacon.getMinor() == knownBeacon.getMinor()
      ) {
        return true;
      }
    }

    return false;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(BeaconPluginConstant.LOG_TAG, "LocationService :: onStartCommand");

    if(beaconManager.isBluetoothEnabled()) {
      try {
        beaconManager.connect(new OnServiceBoundListener() {
          @Override
          public void onServiceBound() throws RemoteException {
            beaconManager.startMonitoring();
          }
        });
      } catch(RemoteException e) {
      }
    }

    // We want this service to continue running until it is explicitly
    // stopped, so return sticky.
    return START_STICKY;
  }

  @Override
  public void onTaskRemoved(Intent rootIntent) {
    Log.d(BeaconPluginConstant.LOG_TAG, "LocationService :: onTaskRemoved");
  }

  @Override
  public void onDestroy() {
    Log.d(BeaconPluginConstant.LOG_TAG, "LocationService :: onDestroy");
    super.onDestroy();

    beaconManager.disconnect();
    beaconManager = null;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
