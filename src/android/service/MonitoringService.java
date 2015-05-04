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

import java.util.List;
import java.util.UUID;

import net.nopattern.cordova.beacon.BeaconConstant;
import net.nopattern.cordova.beacon.BeaconPluginPreference;

public class MonitoringService extends Service {

  private BeaconConstant bConstant;
  private BeaconManager beaconManager;
  private BeaconPluginPreference beaconPluginPreference;

  @Override
  public void onCreate() {
    Log.d(bConstant.LOG_TAG, "MonitoringService :: onCreate");

    beaconManager = BeaconManager.newInstance(this);
    beaconManager.setMonitorPeriod(MonitorPeriod.MINIMAL);
    beaconManager.setForceScanConfiguration(ForceScanConfiguration.DEFAULT);

    UUID proximityUUID = BeaconManager.DEFAULT_KONTAKT_BEACON_PROXIMITY_UUID;
    beaconPluginPreference = new BeaconPluginPreference(this);
    String proximityPreference = beaconPluginPreference.getPreference("BeaconProximityUUID", "");

    if( proximityPreference != "" ) {
      proximityUUID = UUID.fromString(proximityPreference);
    }

    beaconManager.addFilter(Filters.newProximityUUIDFilter(proximityUUID));
    beaconManager.registerMonitoringListener(new BeaconManager.MonitoringListener() {
      @Override
      public void onMonitorStart() {
        Log.d(bConstant.LOG_TAG, "MonitoringService :: beaconManager - onMonitorStart");
      }

      @Override
      public void onMonitorStop() {
        Log.d(bConstant.LOG_TAG, "MonitoringService :: beaconManager - onMonitorStop");
      }

      @Override
      public void onBeaconsUpdated(final Region region, final List<BeaconDevice> beaconDevices) {
        Log.d(bConstant.LOG_TAG, "MonitoringService :: beaconManager - onBeaconsUpdated");
      }

      @Override
      public void onBeaconAppeared(final Region region, final BeaconDevice beaconDevice) {
        Log.d(bConstant.LOG_TAG, "MonitoringService :: beaconManager - onBeaconAppeared");

        Intent intent = new Intent(bConstant.MONITORING_APPEARED_INTENT);
        intent.putExtra(bConstant.EXTRA_DEVICE_ID, beaconDevice.getUniqueId());
        intent.putExtra(bConstant.EXTRA_DEVICE_ACCURACY, beaconDevice.getAccuracy());
        getApplicationContext().sendBroadcast(intent);
      }

      @Override
      public void onRegionEntered(final Region region) {
        Log.d(bConstant.LOG_TAG, "MonitoringService :: beaconManager - onRegionEntered");
      }

      @Override
      public void onRegionAbandoned(final Region region) {
        Log.d(bConstant.LOG_TAG, "MonitoringService :: beaconManager - onRegionAbandoned");
      }
    });
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(bConstant.LOG_TAG, "MonitoringService :: onStartCommand");

    try {
      beaconManager.connect(new OnServiceBoundListener() {
        @Override
        public void onServiceBound() throws RemoteException {
          beaconManager.startMonitoring();
        }
      });
    } catch(RemoteException e) {
    }

    // We want this service to continue running until it is explicitly
    // stopped, so return sticky.
    return START_STICKY;
  }

  @Override
  public void onTaskRemoved(Intent rootIntent) {
    Log.d(bConstant.LOG_TAG, "MonitoringService :: onTaskRemoved");
  }

  @Override
  public void onDestroy() {
    Log.d(bConstant.LOG_TAG, "MonitoringService :: onDestroy");
    super.onDestroy();

    beaconManager.disconnect();
    beaconManager = null;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }

  public class LocalBinder extends Binder {
    public MonitoringService getService() {
      return MonitoringService.this;
    }
  }

  // This is the object that receives interactions from clients.
  private final IBinder binder = new LocalBinder();
}
