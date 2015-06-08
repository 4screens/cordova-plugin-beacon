@interface BeaconBehavior : NSObject <NSCoding>

#pragma mark - properties

@property (strong, nonatomic, readwrite) NSString *proximityUUID;
@property (strong, nonatomic, readwrite) NSString *identifier;
@property (strong, nonatomic, readwrite) NSNumber *major;
@property (strong, nonatomic, readwrite) NSNumber *minor;
@property (strong, nonatomic, readwrite) NSString *behavior;
@property (strong, nonatomic, readwrite) NSDate *date;

- (id)initWithCoder:(NSCoder *)coder;
- (void)encodeWithCoder:(NSCoder *)coder;
- (NSMutableDictionary *)toNSDictionary;

@end