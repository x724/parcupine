//
//  ParqSpotViewController.m
//  Parq
//
//  Created by Mark Yen on 1/3/12.
//  Copyright (c) 2012 Massachusetts Institute of Technology. All rights reserved.
//

#import "ParqSpotViewController.h"
#define LOCATION_ACCURACY 30.0  //this double is meters, we should be fine within 30 meters.  

@implementation ParqSpotViewController
@synthesize scrollView = _scrollView;
@synthesize locationManager;
@synthesize spotNumField = _spotNumField;
@synthesize userLat;
@synthesize userLon;
@synthesize goodLocation;

-(IBAction)parqButton{
    //submit gps coordinates and spot to server.   
    if(goodLocation){
        RateObject* rateObj = [ServerCalls getRateLat:[NSNumber numberWithDouble:self.userLat] Lon: [NSNumber numberWithDouble:self.userLon] spotId:_spotNumField.text];
        //check response from server before allowing next view.  
    }else{
        //SHOW "GETTING GPS LOCATION" dialog like android app.  
    }
}

-(IBAction)scanButton{
    //launch scanner and grab results.  
    //create new view for scanning
    ZBarReaderViewController * reader = [ZBarReaderViewController new];
    
    //set the delegate to receive results
    reader.readerDelegate = self;
    reader.supportedOrientationsMask = ZBarOrientationMaskAll;
    //disable all barcode types
    [reader.scanner setSymbology:ZBAR_NONE config:ZBAR_CFG_ENABLE to:0];
    //re-enable qrcode, so we only scan for qr codes.  
    [reader.scanner setSymbology:ZBAR_QRCODE config:ZBAR_CFG_ENABLE to:1];
    
    //present the scanner
    [self presentModalViewController:reader animated:YES];
    //check resposne from serve before showing next view.  
}

//this method is essentially onActivityResult()
-(void) imagePickerController:(UIImagePickerController *)reader didFinishPickingMediaWithInfo:(NSDictionary *)info{
    id<NSFastEnumeration> allResults = [info objectForKey:ZBarReaderControllerResults];
    ZBarSymbol* firstResult;
    //apparently, this scanner can return multiple results.  i know lol.  
    for(firstResult in allResults)
        break; //this just grabs the first one, sigh weird stuff.  

    NSArray* splitUrl = [firstResult.data componentsSeparatedByString:@"/"];
    NSString* lotId = [splitUrl objectAtIndex:3];
    NSString* spotId = [splitUrl objectAtIndex:4];
    RateObject* rateObj = [ServerCalls getRateLotId:lotId spotId:spotId];
      
    if(rateObj!=nil){
        //stop getting gps, user successfully scanned a qr code.  
        [locationManager stopUpdatingLocation];
        //TODO: launch next screen using this rate object.
    }
    //once we display the result string, dismiss the scanner.  
    [reader dismissModalViewControllerAnimated:YES];
    
}

- (void)locationManager:(CLLocationManager *)manager
    didUpdateToLocation:(CLLocation *)newLocation
           fromLocation:(CLLocation *)oldLocation
{
    //if accuracy isn't close enough, don't allow park, keep getting location.  display dialog. 
    //these numbers represent radius, so higher = less accurate.  
    
    double newAccuracy = (newLocation.verticalAccuracy)+(newLocation.horizontalAccuracy);
    //these numbers are in meters.  
    if (newAccuracy < LOCATION_ACCURACY){
        //if accuracy is acceptable, location is good.  
        goodLocation = YES;
        [locationManager stopUpdatingLocation];
    }
    userLat = newLocation.coordinate.latitude;
    userLon = newLocation.coordinate.longitude;
    
}

-(void)startGettingLocation{
    if (nil == locationManager)
        locationManager = [[CLLocationManager alloc] init];
    locationManager.delegate = self;
    //setting accuracy to be 10 meters.  more powerful but uses battery more.  
    locationManager.desiredAccuracy = kCLLocationAccuracyNearestTenMeters;
    [locationManager startUpdatingLocation];
}

// Call this method somewhere in your view controller setup code.
- (void)registerForKeyboardNotifications
{
  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(keyboardWasShown:)
                                               name:UIKeyboardDidShowNotification object:nil];
  
  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(keyboardWillBeHidden:)
                                               name:UIKeyboardWillHideNotification object:nil];
  
}

// Called when the UIKeyboardDidShowNotification is sent.
- (void)keyboardWasShown:(NSNotification*)aNotification
{
  NSDictionary* info = [aNotification userInfo];
  CGSize kbSize = [[info objectForKey:UIKeyboardFrameBeginUserInfoKey] CGRectValue].size;
  
  UIEdgeInsets contentInsets = UIEdgeInsetsMake(0.0, 0.0, kbSize.height, 0.0);
  self.scrollView.contentInset = contentInsets;
  self.scrollView.scrollIndicatorInsets = contentInsets;
  
  // If active text field is hidden by keyboard, scroll it so it's visible
  // Your application might not need or want this behavior.
  CGRect aRect = self.view.frame;
  const int rectHeight = aRect.size.height;
  aRect.size.height -= kbSize.height;
  if (!CGRectContainsPoint(aRect, self.spotNumField.frame.origin) ) {
    const int y = self.spotNumField.frame.origin.y;
    CGPoint scrollPoint = CGPointMake(0.0, kbSize.height-(rectHeight-y));
    [self.scrollView setContentOffset:scrollPoint animated:YES];
  }
}

// Called when the UIKeyboardWillHideNotification is sent
- (void)keyboardWillBeHidden:(NSNotification*)aNotification
{
  UIEdgeInsets contentInsets = UIEdgeInsetsZero;
  self.scrollView.contentInset = contentInsets;
  self.scrollView.scrollIndicatorInsets = contentInsets;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Release any cached data, images, etc that aren't in use.
}

- (BOOL)isLoggedIn
{
  NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
  return [defaults stringForKey:@"email"] != nil;
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self registerForKeyboardNotifications];
    [self startGettingLocation];   //start getting gps coords
    goodLocation=NO;  //when view first loads, set location to false.  
}

- (void)viewDidUnload
{
  [self setScrollView:nil];
  [self setSpotNumField:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];

    if (![self isLoggedIn]) {
      UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle:nil];
      UIViewController *vc = [storyboard instantiateViewControllerWithIdentifier:@"LoginViewController"];
      [vc setModalPresentationStyle:UIModalPresentationFullScreen];

      [self presentModalViewController:vc animated:YES];
    }
    if(!goodLocation){
        //if gps didn't get a good location
        [self startGettingLocation];
    }
    
}

- (void)viewWillDisappear:(BOOL)animated
{
	[super viewWillDisappear:animated];
}

- (void)viewDidDisappear:(BOOL)animated
{
	[super viewDidDisappear:animated];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    // Return YES for supported orientations
  return (interfaceOrientation != UIInterfaceOrientationPortraitUpsideDown);
}

@end
