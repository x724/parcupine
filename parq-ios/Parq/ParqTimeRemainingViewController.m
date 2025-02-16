//
//  ParqTimeRemainingViewController.m
//  Parq
//
//  Created by Mark Yen on 1/27/12.
//  Copyright (c) 2012 Massachusetts Institute of Technology. All rights reserved.
//

#import "ParqTimeRemainingViewController.h"
#import "ParqRefillViewController.h"
#import "SavedInfo.h"
#import "ServerCalls.h"
#define LOW_TIME_REFILL 1
#define USER_TIME_OUT 2
@interface ParqTimeRemainingViewController ()
@property (strong, nonatomic) NSTimer *timer;
@property (strong, nonatomic) NSDate *endTime;
@end

@implementation ParqTimeRemainingViewController
@synthesize lotNameLabel;
@synthesize spotNumLabel;
@synthesize colonLabel;
@synthesize hours;
@synthesize minutes;
@synthesize timer;
@synthesize endTime;
@synthesize delegate;
@synthesize warnTime;
-(IBAction)navUnparkButton:(id)sender{
    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Unpark" message:@"Are you sure you want to unpark? If you need to repark you will have to pay again." delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"Unpark", nil];
    [alertView show];
}
-(IBAction)navRefillButton:(id)sender{
    [self performSegueWithIdentifier:@"showRefillScreen" sender:self];
}

- (IBAction)unparkButton:(id)sender {
    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Unpark" message:@"Are you sure you want to unpark? If you need to repark you will have to pay again." delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"Unpark", nil];
    [alertView show];
}

- (IBAction)refillButton:(id)sender {
    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle:nil];
    ParqRefillViewController *vc = [storyboard instantiateViewControllerWithIdentifier:@"refillController"];
    vc.delegate = self;
    [vc setModalPresentationStyle:UIModalPresentationFullScreen];
    [self presentModalViewController:vc animated:YES];
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
    if(alertView.tag == LOW_TIME_REFILL){
        [self performSegueWithIdentifier:@"showRefillScreen" sender:self];
        
    }else if(alertView.tag == USER_TIME_OUT){
        [self.navigationController popToRootViewControllerAnimated:YES];
        
    }if (buttonIndex == alertView.firstOtherButtonIndex) {
        if ([ServerCalls unparkUserWithSpotId:[SavedInfo spotId] ParkRefNum:[SavedInfo parkRefNum]]) {
            [SavedInfo unpark];
            [timer invalidate];
            [self.navigationController popViewControllerAnimated:YES];
        } else {
            UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Error" message:@"There was an error while parking. Please try again." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [alertView show];
        }
        
    }
}

- (void)flashColon {
    colonLabel.hidden = !colonLabel.hidden;
}

- (void)updateTimer {
    NSTimeInterval secondsRemaining = [endTime timeIntervalSinceNow];
    
    if (secondsRemaining > 0) {
        int hoursRemaining = secondsRemaining/3600;
        int minutesRemaining = ((int)secondsRemaining%3600)/60;
        if (minutesRemaining < 59) {
            minutesRemaining++;
        } else {
            minutesRemaining = 0;
            hoursRemaining++;
        }

        hours.text = [NSString stringWithFormat:@"%d", hoursRemaining];
        minutes.text = [NSString stringWithFormat:@"%02d", minutesRemaining];

        [self flashColon];
    }else if(secondsRemaining == self.warnTime){
        UIAlertView* warningAlertView = [[UIAlertView alloc] initWithTitle:@"Warning!" message:@"You are low on time" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:@"Refill" , nil];
        warningAlertView.tag = LOW_TIME_REFILL;
        [warningAlertView show];
        
    }else {
        [timer invalidate];
        //delegate methods don't work with segue, fix or replace.  
        
        if ([ServerCalls unparkUserWithSpotId:[SavedInfo spotId] ParkRefNum:[SavedInfo parkRefNum]]) {
            [SavedInfo unpark];
            UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Out of time" message:@"The time on your parking space has expired. Please park again if you need more time." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
            alertView.tag = USER_TIME_OUT;
            [alertView show];
        } else {
            
        }
    }
}

- (void)cancelRefill {
    [self dismissModalViewControllerAnimated:YES];
}

- (void)saveRefillWithDuration:(int)durationIn cost:(int)costIn {
    ParkResponse *response = [ServerCalls refillUserWithSpotId:[SavedInfo spotId] Duration:[NSNumber numberWithInt:durationIn] ChargeAmount:[NSNumber numberWithInt:costIn] PaymentType:[NSNumber numberWithInt:0] ParkRefNum:[SavedInfo parkRefNum]];
    if ([response.resp isEqualToString:@"OK"]) {
        [SavedInfo refillWithParkResponse:response];
        endTime = [NSDate dateWithTimeIntervalSince1970:[response.endTime doubleValue]/1000];

        [self dismissModalViewControllerAnimated:YES];
    } else {
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"Error" message:@"There was an error while refilling. Please try again." delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alertView show];
    }
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
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
    RateObject *rateObj = [SavedInfo rate];
    lotNameLabel.text = rateObj.lotName;
    NSNumber *spotNumber = [SavedInfo spotNumber];
    spotNumLabel.text = [NSString stringWithFormat:@"Spot #%d", [spotNumber intValue]];

    timer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(updateTimer) userInfo:nil repeats:YES];
    endTime = [NSDate dateWithTimeIntervalSince1970:[[SavedInfo endTime] doubleValue]/1000];
}

- (void)viewDidUnload
{
    [self setLotNameLabel:nil];
    [self setSpotNumLabel:nil];
    [self setHours:nil];
    [self setMinutes:nil];
    [self setLotNameLabel:nil];
    [self setSpotNumLabel:nil];
    [self setColonLabel:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

-(void) viewDidAppear:(BOOL)animated{
    endTime = [NSDate dateWithTimeIntervalSince1970:[[SavedInfo endTime] doubleValue]/1000];
    warnTime = [endTime timeIntervalSinceDate:[NSDate dateWithTimeIntervalSince1970:[SavedInfo endTime].doubleValue/1000 - 300]];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

@end
