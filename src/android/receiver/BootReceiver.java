package net.nopattern.cordova.beacon.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.nopattern.cordova.beacon.BeaconConstant;
import net.nopattern.cordova.beacon.service.RangingService;
import net.nopattern.cordova.beacon.service.MonitoringService;

public class BootReceiver extends BroadcastReceiver
{
  BeaconConstant bConstant;

  public void onReceive(Context context, Intent intent)
  {
    String action = intent.getAction();

    if( action == bConstant.BOOT_COMPLETED_INTENT || action == bConstant.BOOT_MONITORING_INTENT ) {
      startMonitoringService(context);
    } else if( action == bConstant.BOOT_RANGING_INTENT ) {
      startRangingService(context);
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
    }
  }

  private void startMonitoringService(Context context)
  {
    Intent intent = new Intent(context,MonitoringService.class);
    context.startService(intent);
  }

  private void startRangingService(Context context)
  {
    Intent intent = new Intent(context,RangingService.class);
    context.startService(intent);
  }
}
