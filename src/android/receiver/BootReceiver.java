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
import java.util.Date;

import net.nopattern.cordova.beacon.BeaconConstant;
import net.nopattern.cordova.beacon.BeaconPluginPreference;
import net.nopattern.cordova.beacon.service.MonitoringService;

public class BootReceiver extends BroadcastReceiver
{
  BeaconConstant bConstant;
  private BeaconPluginPreference beaconPluginPreference = null;

  public void onReceive(Context context, Intent intent)
  {
    Log.d(bConstant.LOG_TAG, "BootReceiver :: onReceive: " + intent.getAction());
    String action = intent.getAction();

    if( beaconPluginPreference == null ) {
      beaconPluginPreference = new BeaconPluginPreference(context);
    }

    if( action == bConstant.BOOT_COMPLETED_INTENT || action == bConstant.BOOT_MONITORING_INTENT ) {
      startMonitoringService(context);
    } else if( action.equals(bConstant.STATE_CHANGED_INTENT) ) {
      final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

      switch( state ) {
        case BluetoothAdapter.STATE_OFF:
          Log.d(bConstant.LOG_TAG, "Bluetooth off");
          break;
        case BluetoothAdapter.STATE_TURNING_OFF:
          Log.d(bConstant.LOG_TAG, "Turning Bluetooth off...");
          break;
        case BluetoothAdapter.STATE_ON:
          Log.d(bConstant.LOG_TAG, "Bluetooth on");
          break;
        case BluetoothAdapter.STATE_TURNING_ON:
          Log.d(bConstant.LOG_TAG, "Turning Bluetooth on...");
          break;
      }
    } else if( action.equals(bConstant.MONITORING_APPEARED_INTENT) ) {
      final Date date = new Date();
      final Date expireDate = new Date( date.getTime() + BeaconPluginPreference.ETLN_PREFERENCE_DEFAULT );
      final String enterRegionLocalNotification = beaconPluginPreference.getPreference("BeaconEnterRegionLocalNotification", "You did enter beacon region!");
      final long expireTimeLocalNotification = beaconPluginPreference.getPreference("BeaconExpireTimeLocalNotification", date.getTime());

      if( date.getTime() > expireTimeLocalNotification ) {
        try {
          sendNotification(context, "Beacon", enterRegionLocalNotification);
          beaconPluginPreference.setPreference("BeaconExpireTimeLocalNotification", expireDate.getTime());
        } catch (Exception e) {
        }
      }
    }
  }

  private void startMonitoringService(Context context)
  {
    Intent intent = new Intent(context,MonitoringService.class);
    context.startService(intent);
  }

  private void sendNotification(Context context, String title, String text) throws Exception {
    Class cls = Class.forName(context.getPackageName() + ".MainActivity");
    Activity act = (Activity) cls.getConstructor().newInstance();

    Intent notificationIntent = new Intent(context, act.getClass());
    notificationIntent.setAction(bConstant.NOTIFICATION_INTENT);

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
