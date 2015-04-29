#import "Beacon.h"

@implementation Beacon

- (CDVPlugin*)initWithWebView:(UIWebView*)theWebView
{
  self = [super initWithWebView:theWebView];
  if (self)
  {
    locationManager = [[KTKLocationManager alloc] init];
    locationManager.delegate = self;

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didFinishLaunchingWithOptions:) name:@"UIApplicationDidFinishLaunchingNotification" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didReceiveLocalNotification:) name:CDVLocalNotification object:nil];
  }
  return self;
}

- (void) fireEvent:(NSString*)eventName
{
  NSString* js = [NSString stringWithFormat:@"window.cordova.plugins.Beacon.fireEvent('%@')", eventName];
  [self.commandDelegate evalJs:js];
}

- (void) didFinishLaunchingWithOptions: (NSNotification *)notification
{   // config.xml configuration...
    proximityUUID = [self.commandDelegate.settings objectForKey:[@"BeaconProximityUUID" lowercaseString]];
    if( proximityUUID == nil ) {
      // if value is missing, default kontakt.io proximity UUID
      proximityUUID = @"f7826da6-4fa2-4e98-8024-bc5b71e0893e";
    }
    enterRegionLocalNotification = [self.commandDelegate.settings objectForKey:[@"BeaconEnterRegionLocalNotification" lowercaseString]];
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
    NSLog(@"Ranged beacons count: %lu", (unsigned long)[beacons count]);
}

@end
