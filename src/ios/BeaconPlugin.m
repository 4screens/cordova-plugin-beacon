#import "BeaconPlugin.h"

@implementation BeaconPlugin

- (CDVPlugin*)initWithWebView:(UIWebView*)theWebView
{
  self = [super initWithWebView:theWebView];
  if (self)
  {
    userDefaults = [[BeaconUserDefaults alloc] init];
    locationManager = [[KTKLocationManager alloc] init];
    locationManager.delegate = self;

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didFinishLaunchingWithOptions:) name:@"UIApplicationDidFinishLaunchingNotification" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveLocalNotification:) name:CDVLocalNotification object:nil];
  }
  return self;
}

- (void) fireEvent:(NSString*)eventName
{
  NSString* js = [NSString stringWithFormat:@"window.cordova.plugins.BeaconPlugin.fireEvent('%@')", eventName];
  [self.commandDelegate evalJs:js];
}

#pragma mark - API

- (void)deviceready:(CDVInvokedUrlCommand*)command
{
}

- (void)setBeaconProximityUUID:(CDVInvokedUrlCommand*)command
{
    proximityUUID = [command.arguments objectAtIndex:0];
    [userDefaults set:@"BeaconProximityUUID" withString:proximityUUID];
    
    [self didFinishLaunchingWithOptions:nil];
}

- (void)setEnterRegionLocalNotification:(CDVInvokedUrlCommand*)command
{
    enterRegionLocalNotification = [command.arguments objectAtIndex:0];
    [userDefaults set:@"BeaconEnterRegionLocalNotification" withString:enterRegionLocalNotification];
}

- (void)receiveBeaconBehavior:(CDVInvokedUrlCommand*)command
{
    NSArray* beaconsBehavior = [userDefaults getArray:@"BeaconBehavior"];
    
    NSMutableArray *mutableArray = [[NSMutableArray alloc] init];
    for (BeaconBehavior *beaconBehavior in beaconsBehavior) {
        [mutableArray addObject:[beaconBehavior toNSDictionary]];
    }
    
    NSError* error;
    NSData *encodedData = [NSJSONSerialization dataWithJSONObject:mutableArray options:0 error:&error];
    NSString* jsonString = [[NSString alloc] initWithData:encodedData encoding:NSUTF8StringEncoding];

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:jsonString];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)clearBeaconBehavior:(CDVInvokedUrlCommand*)command
{
    [userDefaults remove:@"BeaconBehavior"];
    
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"success"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

#pragma mark - Notification

- (void) didFinishLaunchingWithOptions: (NSNotification *)notification
{   // config.xml configuration...
    proximityUUID = [userDefaults get:@"BeaconProximityUUID"];
    if( proximityUUID == nil ) {
      // if value is missing, default kontakt.io proximity UUID
      proximityUUID = @"f7826da6-4fa2-4e98-8024-bc5b71e0893e";
    }
    enterRegionLocalNotification = [userDefaults get:@"BeaconEnterRegionLocalNotification"];
    if( enterRegionLocalNotification == nil ) {
      // if value is missing, set default
      enterRegionLocalNotification = @"You did enter beacon region!";
    }

    if ([KTKLocationManager canMonitorBeacons])
    {
      // beacon
      KTKRegion *region =[[KTKRegion alloc] init];
      region.uuid = proximityUUID;
        
      [locationManager setRegions:@[region]];
      [locationManager startMonitoringBeacons];

      // notification
      if ([[UIApplication sharedApplication] respondsToSelector:@selector(registerUserNotificationSettings:)]) {
        // iOS8
        UIUserNotificationSettings *settings = [UIUserNotificationSettings settingsForTypes:(UIUserNotificationTypeBadge|UIUserNotificationTypeAlert|UIUserNotificationTypeSound) categories:nil];
        [[UIApplication sharedApplication] registerUserNotificationSettings:settings];
      }
    }
}

- (void) didReceiveLocalNotification:(NSNotification*)localNotification
{
  [[UIApplication sharedApplication] cancelAllLocalNotifications];
  [self fireEvent:@"didReceiveLocalNotification"];
}

#pragma mark - KTKLocationManagerDelegate

- (void)locationManager:(KTKLocationManager *)locationManager didChangeState:(KTKLocationManagerState)state withError:(NSError *)error
{
    if (state == KTKLocationManagerStateFailed)
    {
        NSLog(@"Something went wrong with your Location Services settings. Check OS settings.");
    }
}

- (void)locationManager:(KTKLocationManager *)locationManager didEnterRegion:(KTKRegion *)region
{
    NSLog(@"Enter region %@", region.uuid);

    UILocalNotification *notification = [[UILocalNotification alloc] init];
    notification.fireDate = [NSDate dateWithTimeIntervalSinceNow:1];
    notification.alertBody = enterRegionLocalNotification;
    notification.timeZone = [NSTimeZone defaultTimeZone];
    notification.soundName = UILocalNotificationDefaultSoundName;

    [[UIApplication sharedApplication] scheduleLocalNotification:notification];
}

- (void)locationManager:(KTKLocationManager *)locationManager didExitRegion:(KTKRegion *)region
{
    NSLog(@"Exit region %@", region.uuid);
}

- (void)locationManager:(KTKLocationManager *)locationManager didRangeBeacons:(NSArray *)beacons
{
    NSLog(@"didRangeBeacons...%lu", (unsigned long)[beacons count]);
    
    NSSet* knownBeacons = [NSSet setWithArray:beacons];
    BeaconBehavior *appeared =[[BeaconBehavior alloc] init];
    
    for(CLBeacon* beacon in knownBeacons) {
        if (![self hasCLBeacon:lastKnownBeacons containsBeacon:beacon]) {
            appeared.proximityUUID = beacon.proximityUUID.UUIDString;
            appeared.major = beacon.major;
            appeared.minor = beacon.minor;
            appeared.behavior = @"enter";
            appeared.date = [NSDate date];
            
            [userDefaults push:@"BeaconBehavior" withBeacon:appeared];
        }
    }
    
//    for(CLBeacon* beacon in lastKnownBeacons) {
//        if (![self hasCLBeacon:knownBeacons containsBeacon:beacon]) {
//            appeared.proximityUUID = beacon.proximityUUID.UUIDString;
//            appeared.major = beacon.major;
//            appeared.minor = beacon.minor;
//            appeared.behavior = @"leave";
//            appeared.date = [NSDate date];
//            
//            [userDefaults push:@"BeaconBehavior" withBeacon:appeared];
//        }
//    }
    
    lastKnownBeacons = knownBeacons;
}

- (BOOL)hasCLBeacon:(NSSet *)knownBeacons containsBeacon:(CLBeacon *)beacon {
    for(CLBeacon* knownBeacon in knownBeacons) {
        if ([beacon.proximityUUID.UUIDString isEqualToString:knownBeacon.proximityUUID.UUIDString] && [beacon.major intValue] == [knownBeacon.major intValue] && [beacon.minor intValue] == [knownBeacon.minor intValue]) {
            return true;
        }
    }
    return false;
}

@end
