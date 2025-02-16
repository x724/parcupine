//
//  MKShape+Color.m
//  Parq
//
//  Created by Mark Yen on 4/6/12.
//  Copyright (c) 2012 Massachusetts Institute of Technology. All rights reserved.
//

#import "MKShape+Color.h"

@implementation MKShape (Color)
static char colorKey;
static char nameKey;
static char objIdKey;

- (void) setColor:(int)color {
    objc_setAssociatedObject(self, &colorKey, [NSNumber numberWithInt:color], OBJC_ASSOCIATION_RETAIN);
}

- (int) color {
    return [objc_getAssociatedObject( self, &colorKey ) intValue];
}
- (void) setName:(int)name {
    objc_setAssociatedObject(self, &nameKey, [NSNumber numberWithInt:name], OBJC_ASSOCIATION_RETAIN);
}
- (int) name {
    return [objc_getAssociatedObject( self, &nameKey ) intValue];
}

-(void) setObjId:(long)objId{
    objc_setAssociatedObject(self, &objIdKey, [NSNumber numberWithLong:objId], OBJC_ASSOCIATION_RETAIN);
}
-(long) objId{
    return [objc_getAssociatedObject( self, &objIdKey ) longValue];
}
@end
