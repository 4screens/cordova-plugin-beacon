#import "BeaconBehavior.h"

@implementation BeaconBehavior

@synthesize proximityUUID;
@synthesize identifier;
@synthesize major;
@synthesize minor;
@synthesize behavior;
@synthesize date;

- (id)initWithCoder:(NSCoder *)coder;
{
    self = [super init];
    if (self != nil)
    {
        self.proximityUUID = [coder decodeObjectForKey:@"proximityUUID"];
        self.identifier = [coder decodeObjectForKey:@"identifier"];
        self.major = [coder decodeObjectForKey:@"major"];
        self.minor = [coder decodeObjectForKey:@"minor"];
        self.behavior = [coder decodeObjectForKey:@"behavior"];
        self.date = [coder decodeObjectForKey:@"date"];
    }
    return self;
}

- (void)encodeWithCoder:(NSCoder *)coder;
{
    [coder encodeObject:self.proximityUUID forKey:@"proximityUUID"];
    [coder encodeObject:self.identifier forKey:@"identifier"];
    [coder encodeObject:self.major forKey:@"major"];
    [coder encodeObject:self.minor forKey:@"minor"];
    [coder encodeObject:self.behavior forKey:@"behavior"];
    [coder encodeObject:self.date forKey:@"date"];
}

- (NSMutableDictionary *)toNSDictionary
{
    NSTimeInterval datetimeinterval = [self.date timeIntervalSince1970];
    NSTimeInterval dateinteger = round(datetimeinterval * 1000);
    
    NSMutableDictionary *dictionary = [[NSMutableDictionary alloc] init];
    [dictionary setValue:[self.proximityUUID lowercaseString] forKey:@"proximityUUID"];
    [dictionary setValue:self.identifier forKey:@"identifier"];
    [dictionary setValue:self. major forKey:@"major"];
    [dictionary setValue:self.minor forKey:@"minor"];
    [dictionary setValue:self.behavior forKey:@"behavior"];
    [dictionary setValue:[NSString stringWithFormat:@"%.0f", dateinteger] forKey:@"date"];
    
    return dictionary;
}

@end