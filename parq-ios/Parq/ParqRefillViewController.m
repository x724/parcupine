//
//  ParqRefillViewController.m
//  Parq
//
//  Created by Mark Yen on 1/29/12.
//  Copyright (c) 2012 Massachusetts Institute of Technology. All rights reserved.
//

#import "ParqRefillViewController.h"
#import "SavedInfo.h"
#import "ServerCalls.h"

@interface ParqRefillViewController ()
@property (strong, nonatomic) RateObject *rateObj;
@end

@implementation ParqRefillViewController
@synthesize timePicker;
@synthesize rate;
@synthesize total;
@synthesize lotNameLabel;
@synthesize spotNumLabel;
@synthesize delegate;
@synthesize rateObj;


-(IBAction)navCancelButton:(id)sender{
    //do nothing, return to previous screen.  
    [self.navigationController popViewControllerAnimated:YES];
}


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

+ (NSString*) centsToString:(int)cents
{
    return [NSString stringWithFormat:@"$%d.%02d", cents/100, cents%100];
}

#pragma mark - User selection

- (int)durationSelectedInMinutes
{
    return ((int)(timePicker.countDownDuration))/60;
}

- (int)costSelectedInCents
{
    return [self durationSelectedInMinutes]*rateObj.rateCents.intValue/rateObj.minuteInterval.intValue;
}

- (void)updateTotal {
    total.text = [ParqRefillViewController centsToString:[self costSelectedInCents]];
}

- (IBAction)cancelButton:(id)sender {
    [delegate cancelRefill];
}
- (IBAction)saveButton:(id)sender {
    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Confirm payment"
                                                        message:[NSString stringWithFormat:@"Refilling for %d minutes will cost %@. Is this okay?",[self durationSelectedInMinutes],[ParqRefillViewController centsToString:[self costSelectedInCents]]]
                                                       delegate:self
                                              cancelButtonTitle:@"Cancel"
                                              otherButtonTitles:@"Refill", nil];
    [alertView show];
    
}
-(IBAction)navRefillButton:(id)sender{
    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Confirm payment"
                                                        message:[NSString stringWithFormat:@"Refilling for %d minutes will cost %@. Is this okay?",[self durationSelectedInMinutes],[ParqRefillViewController centsToString:[self costSelectedInCents]]]
                                                       delegate:self
                                              cancelButtonTitle:@"Cancel"
                                              otherButtonTitles:@"Refill", nil];
    [alertView show];
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    if (buttonIndex == alertView.firstOtherButtonIndex) {
       // [delegate saveRefillWithDuration:[self durationSelectedInMinutes] cost:[self costSelectedInCents]];
        
        //START NEW CODE
        ParkResponse *response = [ServerCalls refillUserWithSpotId:[SavedInfo spotId] Duration:[NSNumber numberWithInt:[self durationSelectedInMinutes]] ChargeAmount:[NSNumber numberWithInt:[self costSelectedInCents]] PaymentType:[NSNumber numberWithInt:0] ParkRefNum:[SavedInfo parkRefNum]];
        if ([response.resp isEqualToString:@"OK"]) {
            [SavedInfo refillWithParkResponse:response];
            [self.navigationController popViewControllerAnimated:YES];
            //update endtime for time remaining view.  
//            [delegate setEndTime:[NSDate dateWithTimeIntervalSince1970:[response.endTime doubleValue]/1000]];
            [SavedInfo setEndTime:response.endTime];
        } else {
            UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Error" message:@"There was an error while refilling. Please try again." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [alertView show];
        }
        
        
    }
}

- (void)didReceiveMemoryWarning
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

/*
// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView
{
}
*/

// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad
{
    [super viewDidLoad]; 
    rateObj = [SavedInfo rate];
    NSNumber *spotNumber = [SavedInfo spotNumber];

    timePicker.minuteInterval = rateObj.minuteInterval.intValue;
    rate.text = [NSString stringWithFormat:@"%@ per %d minutes", [ParqRefillViewController centsToString:rateObj.rateCents.intValue], rateObj.minuteInterval.intValue];
    lotNameLabel.text = rateObj.lotName;
    spotNumLabel.text = [NSString stringWithFormat:@"Spot #%d", spotNumber.intValue];
    [timePicker addTarget:self action:@selector(updateTotal) forControlEvents:UIControlEventValueChanged];
    timePicker.countDownDuration = rateObj.minuteInterval.doubleValue;
    [self updateTotal];
}

- (void)viewDidUnload
{
    [self setTimePicker:nil];
    [self setRate:nil];
    [self setTotal:nil];
    [self setLotNameLabel:nil];
    [self setSpotNumLabel:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

@end
