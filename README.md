## Alarming_System_Mobile_App
### SETUP:
  #### INTRODUCTION:
          This app is helpful when someone feels vulnerable or threatened or in any emergency situations.\User can just press a 
          button to send messages to the emergency contacts which are selected by the user.
          
          The message contains the following information:
                          1.Latitude and Longitude of the current Location of the user.
                          2.Address of the current location
                          
          The text messages sent, along with the content, also have the last known location of the user. This is very helpful in 
          tracking the whereabouts of the person.
         
   #### About App And Its Usage:
           WELCOME PAGE:
          The Splash Screen of the app waits for 2 seconds and directly navigates to the login page
           LOGIN ACTIVITY:
          - Phone Authentication is used for signing In
          - User can  select the country code from the drop down menu and has to enter his/her phone number.
          - OTP is sent to the users device after submitting the mobile number where the OTP is verified and navigates to the
            Profile Page
           PROFILE:
          - User has to enter his/her personal details like Name, email, Alternate number, age, gender,state,district
          - Clicking on the Save button ,Stores the user's Personal Information in the firestore
           HOME PAGE:
          - Here the application asks for the permission to access the Locaion
          - It finds the current location of the user and stores the location details in firestore
          - 4 option are present on the Home Page:
                              1.Personal Information
                              2.Emergency Contacts
                              3.Location
                              4.SOS
           - Logout option is present on the top right corner of the activity.
            PERSONAL INFO:
           - User's personal Info will be present here
           - user can update the info whenever required using the update button present at the bottom.
            EMERGENCY CONTACTS:
           - 2 buttons are present in this activity
           - One to select the contacts (user has to allow the access to contacts), then it navigates to the contacts Activity of the device 
              where he can select one contact at a time 
           - user is allowed to select maximum of 5 contacts
           - and the second button to view and delete the selected contacts
            LOCATION:
           - this activity is to view the current Location details (Latitude, Longitude, Address of current Location)
            SOS:
           - when the user clicks on this ,it send sms to the emergency contacts selected by the user
           - this sms contains details of users current location at that time.
           
   - Adobe XD is used to Design the UI for the application
   - Firebase used to store the data 
  # References:
   - Android Documentation
   - Firebase Documentation
