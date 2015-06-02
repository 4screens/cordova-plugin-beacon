package net.nopattern.cordova.beacon.receiver;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.lang.Exception;
import java.lang.Integer;
import java.lang.InternalError;
import java.lang.String;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import net.nopattern.cordova.beacon.BeaconPluginConstant;
import net.nopattern.cordova.beacon.BeaconPluginPreference;
import net.nopattern.cordova.beacon.service.LocationService;

public class BeaconReceiver extends BroadcastReceiver
{
  private BeaconPluginPreference beaconPluginPreference = null;

  public void onReceive(Context context, Intent intent)
  {
    Log.d(BeaconPluginConstant.LOG_TAG, "BootReceiver :: onReceive: " + intent.getAction());
    String action = intent.getAction();

    if( beaconPluginPreference == null ) {
      beaconPluginPreference = new BeaconPluginPreference(context);
    }

    if( action == BeaconPluginConstant.BOOT_COMPLETED_INTENT || action == BeaconPluginConstant.BOOT_MONITORING_SERVICE_INTENT ) {
      startMonitoringService(context);
    } else if( action.equals(BeaconPluginConstant.STATE_CHANGED_INTENT) ) {
      final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

      switch( state ) {
        case BluetoothAdapter.STATE_OFF:
          Log.d(BeaconPluginConstant.LOG_TAG, "BootReceiver :: Bluetooth OFF");
          break;
        case BluetoothAdapter.STATE_TURNING_OFF:
          Log.d(BeaconPluginConstant.LOG_TAG, "BootReceiver :: Bluetooth off...");
          stopMonitoringService(context);
          break;
        case BluetoothAdapter.STATE_ON:
          Log.d(BeaconPluginConstant.LOG_TAG, "BootReceiver :: Bluetooth ON");
          startMonitoringService(context);
          break;
        case BluetoothAdapter.STATE_TURNING_ON:
          Log.d(BeaconPluginConstant.LOG_TAG, "BootReceiver :: Bluetooth on...");
          break;
      }
    } else if(action.equals(BeaconPluginConstant.BEACON_APPEARED_INTENT) ) {
      sendNotification(context);
    } else if(action.equals(BeaconPluginConstant.BEACON_BEHAVIOR_INTENT) ) {
      saveBeaconBehavior(intent);
    } else if(action.equals(BeaconPluginConstant.REBOOT_MONITORING_SERVICE_INTENT) ) {
      stopMonitoringService(context);
      startMonitoringService(context);
    }
  }

  private void startMonitoringService(Context context)
  {
    Intent intent = new Intent(context,LocationService.class);
    context.startService(intent);
  }

  private void stopMonitoringService(Context context)
  {
    Intent intent = new Intent(context,LocationService.class);
    context.stopService(intent);
  }

  private void saveBeaconBehavior(Intent intent) {
    Log.i(BeaconPluginConstant.LOG_TAG, "BootReceiver :: saveBeaconBehavior");

    final Date date = new Date();
    final String prefix = Long.toString(date.getTime()) + ":";

    Set<String> proximityUUID = beaconPluginPreference.getPreference(BeaconPluginConstant.BEAP_UUID_PREFERENCE, new HashSet<String>());
    Set<String> identifier = beaconPluginPreference.getPreference(BeaconPluginConstant.BEAP_IDEN_PREFERENCE, new HashSet<String>());
    Set<String> major = beaconPluginPreference.getPreference(BeaconPluginConstant.BEAP_MAJOR_PREFERENCE, new HashSet<String>());
    Set<String> minor = beaconPluginPreference.getPreference(BeaconPluginConstant.BEAP_MINOR_PREFERENCE, new HashSet<String>());

    proximityUUID.add(prefix + intent.getStringExtra(BeaconPluginConstant.PROXIMITY_UUID_EXTRA));
    identifier.add(prefix + intent.getStringExtra(BeaconPluginConstant.IDENTIFIER_EXTRA));
    major.add(prefix + Integer.toString(intent.getIntExtra(BeaconPluginConstant.MAJOR_EXTRA, 0)));
    minor.add(prefix + Integer.toString(intent.getIntExtra(BeaconPluginConstant.MINOR_EXTRA, 0)));

    beaconPluginPreference.setPreference(BeaconPluginConstant.BEAP_UUID_PREFERENCE, proximityUUID);
    beaconPluginPreference.setPreference(BeaconPluginConstant.BEAP_IDEN_PREFERENCE, identifier);
    beaconPluginPreference.setPreference(BeaconPluginConstant.BEAP_MAJOR_PREFERENCE, major);
    beaconPluginPreference.setPreference(BeaconPluginConstant.BEAP_MINOR_PREFERENCE, minor);
  }

  private void sendNotification(Context context) {
    Log.i(BeaconPluginConstant.LOG_TAG, "BootReceiver :: sendNotification");

    final Date date = new Date();
    final Date expireDate = new Date(date.getTime() + BeaconPluginConstant.ETLN_PREFERENCE_DEFAULT);
    final String enterRegionLocalNotification = beaconPluginPreference.getPreference(BeaconPluginConstant.ERLN_PREFERENCE , BeaconPluginConstant.ERLN_PREFERENCE_DEFAULT);
    final long expireTimeLocalNotification = beaconPluginPreference.getPreference(BeaconPluginConstant.ETLN_PREFERENCE, date.getTime());

    if( date.getTime() >= expireTimeLocalNotification ) {
      try {
        buildNotification(context, "Beacon", enterRegionLocalNotification);
        beaconPluginPreference.setPreference( BeaconPluginConstant.ETLN_PREFERENCE, expireDate.getTime());
      } catch (Exception e) {
      }
    }
  }

  private void buildNotification(Context context, String title, String text) throws Exception {
    Log.i(BeaconPluginConstant.LOG_TAG, "BootReceiver :: buildNotification");

    Class cls = Class.forName(context.getPackageName() + ".MainActivity");
    Activity act = (Activity) cls.getConstructor().newInstance();

    Intent notificationIntent = new Intent(context, act.getClass());
    notificationIntent.setAction(BeaconPluginConstant.LOCAL_NOTIFICATION_INTENT);

    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
      .setSmallIcon(context.getResources().getIdentifier("icon", "drawable", context.getPackageName()))
      .setContentTitle(title)
      .setContentText(text)
      .setOnlyAlertOnce(true)
      .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
      .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
      .setContentIntent(contentIntent)
      .setAutoCancel(true);

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(0, mBuilder.build());
  }
}
