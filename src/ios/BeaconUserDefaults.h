@interface BeaconUserDefaults : NSObject {
    NSUserDefaults *userDefaults;
}

- (id) init;
- (NSString *) get:(NSString *) preferenceName;
- (NSArray *) getArray:(NSString *) preferenceName;

- (void) set:(NSString *) preferenceName withString:(NSString *) preferenceValue;
- (void) push:(NSString *) preferenceName withBeacon:(BeaconBehavior *) preferenceValue;

- (void) remove:(NSString *) preferenceName;

@end
