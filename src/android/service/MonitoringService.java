package net.nopattern.cordova.beacon.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

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

public class MonitoringService extends Service {

  private BeaconManager beaconManager;

  @Override
  public void onCreate() {
    super.onCreate();

    beaconManager = BeaconManager.newInstance(this);
    beaconManager.setMonitorPeriod(MonitorPeriod.MINIMAL);
    beaconManager.setForceScanConfiguration(ForceScanConfiguration.DEFAULT);

    beaconManager.addFilter(new Filters.CustomFilter() {
      @Override
      public Boolean apply(AdvertisingPackage advertisingPackage) {
        final UUID proximityUUID = advertisingPackage.getProximityUUID();
        final double distance = advertisingPackage.getAccuracy();

        return proximityUUID.equals(BeaconManager.DEFAULT_KONTAKT_BEACON_PROXIMITY_UUID) && distance <= 1.5;
      }
    });

    beaconManager.registerMonitoringListener(new BeaconManager.MonitoringListener() {
      @Override
      public void onMonitorStart() {
      }

      @Override
      public void onMonitorStop() {
      }

      @Override
      public void onBeaconsUpdated(final Region region, final List<BeaconDevice> beaconDevices) {
      }

      @Override
      public void onBeaconAppeared(final Region region, final BeaconDevice beaconDevice) {
        Toast.makeText(getApplicationContext(), beaconDevice.getUniqueId() + ": " + beaconDevice.getAccuracy(), Toast.LENGTH_LONG).show();
      }

      @Override
      public void onRegionEntered(final Region region) {
      }

      @Override
      public void onRegionAbandoned(final Region region) {
      }
    });

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

  @Override
  public void onDestroy() {
    super.onDestroy();

    beaconManager.disconnect();
    beaconManager = null;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    // We want this service to continue running until it is explicitly
    // stopped, so return sticky.
    return START_STICKY;
  }

  public class LocalBinder extends Binder {
    public MonitoringService getService() {
      return MonitoringService.this;
    }
  }

  // This is the object that receives interactions from clients.
  private final IBinder binder = new LocalBinder();
}
