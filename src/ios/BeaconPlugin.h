/********* BeaconPlugin.h Cordova Plugin Implementation *******/

#import "AppDelegate.h"
#import <Cordova/CDV.h>

#import <CoreBluetooth/CoreBluetooth.h>
#import <CoreLocation/CoreLocation.h>
#import <Foundation/Foundation.h>

#import "KontaktSDK.h"
#import "BeaconBehavior.h"
#import "BeaconUserDefaults.h"

@interface BeaconPlugin : CDVPlugin <KTKLocationManagerDelegate> {
  NSSet *lastKnownBeacons;
  NSString *proximityUUID;
  NSString *enterRegionLocalNotification;
  KTKLocationManager *locationManager;
  BeaconUserDefaults *userDefaults;
}

- (void) didFinishLaunchingWithOptions: (NSNotification *)notification;
- (void)locationManager:(KTKLocationManager *)locationManager didEnterRegion:(KTKRegion *)region;
- (void)locationManager:(KTKLocationManager *)locationManager didExitRegion:(KTKRegion *)region;
- (void)locationManager:(KTKLocationManager *)locationManager didRangeBeacons:(NSArray *)beacons;

@end
