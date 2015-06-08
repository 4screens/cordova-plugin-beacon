#import "BeaconBehavior.h"
#import "BeaconUserDefaults.h"

@implementation BeaconUserDefaults : NSObject

- (id)init
{
    self = [super init];
    if (self != nil)
    {
        userDefaults = [NSUserDefaults standardUserDefaults];
    }
    return self;
}

- (NSString *) get:(NSString *) preferenceName
{
    return [userDefaults stringForKey:preferenceName];
}

- (NSArray *) getArray:(NSString *) preferenceName
{
    NSData* preferenceSaved = [userDefaults objectForKey:preferenceName];
    NSArray* preferenceSavedArray = [NSKeyedUnarchiver unarchiveObjectWithData:preferenceSaved];
    return preferenceSavedArray;
}

- (void) set:(NSString *) preferenceName withString:(NSString *) preferenceValue
{
    [userDefaults setObject:preferenceValue forKey:preferenceName];
    [userDefaults synchronize];
}

- (void) push:(NSString *) preferenceName withBeacon:(BeaconBehavior *) preferenceValue
{
    NSData* preferenceSaved = [userDefaults objectForKey:preferenceName];
    NSArray* preferenceSavedArray = [NSKeyedUnarchiver unarchiveObjectWithData:preferenceSaved];
    NSMutableArray* preferenceArray;
    
    if (preferenceSavedArray != nil) {
        preferenceArray = [[NSMutableArray alloc] initWithArray:preferenceSavedArray];
    } else {
        preferenceArray = [[NSMutableArray alloc] init];
    }
    
    [preferenceArray addObject:preferenceValue];
    
    [userDefaults setObject:[NSKeyedArchiver archivedDataWithRootObject:preferenceArray] forKey:preferenceName];
    [userDefaults synchronize];
}

- (void) remove:(NSString *) preferenceName
{
    [userDefaults removeObjectForKey:preferenceName];
}

@end
