//
//  PQParkingViewController.h
//  Parq
//
//  Created by Mark Yen on 4/26/12.
//  Copyright (c) 2012 Massachusetts Institute of Technology. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "PQMapViewController.h"
#import "SelfReportingViewController.h"

#define ALERTVIEW_EXTEND 12

@interface PQParkingViewController : UITableViewController <UIAlertViewDelegate, UITableViewDelegate, UIActionSheetDelegate, PQNetworkLayerDelegate>{
    DataLayer* dataLayer;
    NetworkLayer* networkLayer;
}
@property (nonatomic, readonly) double rate;
@property (nonatomic) int rateNumeratorCents;
@property (nonatomic) int rateDenominatorMinutes;
@property (nonatomic) int limit;
@property (nonatomic, copy) NSString *address;
@property (weak, nonatomic) IBOutlet UIButton *startButton;
@property (weak, nonatomic) IBOutlet UIButton *unparkButton;
@property (weak, nonatomic) IBOutlet UIButton *extendButton;
@property (weak, nonatomic) IBOutlet UIImageView *meter;
@property (weak, nonatomic) IBOutlet UIImageView *paygFlag;
@property (weak, nonatomic) IBOutlet UIImageView *prepaidFlag;
@property (weak, nonatomic) IBOutlet UILabel *hours;
@property (weak, nonatomic) IBOutlet UILabel *minutes;
@property (weak, nonatomic) IBOutlet UIImageView *colon;
@property (weak, nonatomic) IBOutlet UIImageView *paygCheck;
@property (weak, nonatomic) IBOutlet UIImageView *prepaidCheck;
@property (weak, nonatomic) IBOutlet UILabel *prepaidAmount;
@property (weak, nonatomic) IBOutlet UILabel *remainingAmount;
@property (weak, nonatomic) IBOutlet UILabel *extendAmount;
@property (weak, nonatomic) IBOutlet UIView *paygCellView;
@property (weak, nonatomic) IBOutlet UIView *addressCellView;
@property (weak, nonatomic) IBOutlet UIView *prepaidCellView;
@property (weak, nonatomic) IBOutlet UIView *seeMapCellView;
@property (weak, nonatomic) IBOutlet UIView *timeRemainingCellView;
@property (weak, nonatomic) IBOutlet UIView *timeToAddCellView;
@property (weak, nonatomic) IBOutlet UIDatePicker *datePicker;
@property (weak, nonatomic) IBOutlet UILabel *rateNumerator;
@property (weak, nonatomic) IBOutlet UILabel *rateDenominator;
@property (weak, nonatomic) IBOutlet UILabel *limitValue;
@property (weak, nonatomic) IBOutlet UILabel *limitUnit;
@property (weak, nonatomic) IBOutlet UILabel *addressLabel;
@property (weak, nonatomic) IBOutlet UILabel *expiresAtTime;
@property (strong, nonatomic) IBOutlet UIBarButtonItem *cancelButton;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *doneButton;
@property (weak, nonatomic) PQMapViewController *parent;
@property (nonatomic, retain) SpotInfo* spotInfo;
@end
