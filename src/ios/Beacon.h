/********* Beacon.h Cordova Plugin Implementation *******/

#import "AppDelegate.h"
#import <Cordova/CDV.h>

#import <CoreBluetooth/CoreBluetooth.h>
#import <CoreLocation/CoreLocation.h>
#import <Foundation/Foundation.h>

#import "KontaktSDK.h"

@interface Beacon : CDVPlugin <KTKLocationManagerDelegate> {
  NSString *proximityUUID;
  NSString *enterRegionLocalNotification;
  KTKLocationManager *locationManager;
}

@end
