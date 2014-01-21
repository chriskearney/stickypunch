StickyPunch is a Safari Push Notifcations Server.

https://developer.apple.com/library/mac/documentation/NetworkingInternet/Conceptual/NotificationProgrammingGuideForWebsites/PushNotifications/PushNotifications.html#//apple_ref/doc/uid/TP40013225-CH3-SW1

StickyPunch implements the requirements outlined in the Apple developer documentation listed above.  It will also connect to APNS periodically and ask for feedback for deviceTokens that are no longer active. Feedback is saved to the database so that you aren't ever sending pushes to users who don't want them.  Apple tends not to like this.

EndPoints that Safari Uses
/log
  -Safari will POST to this address when a log message is generated during push notification setup.

/devices/{deviceToken}/registrations/{websitePushId}
  -Safari will POST to this address when push access is granted
  -Safari will DELETE to this adress when push access is removed

/pushPackages/{websitePushId}
  -Safari will POST to this address when checking for Push to Safari support.  We respond with a PushPackage (really just a zip file) that has all the details Safari needs to know in order to enable push.

EndPoints for testing
/send/{deviceToken}/{msg}
  -GET will send a push to a deviceToken, if its listed as active in the StickyPunch database.

/devices
  -GET will list all known deviceTokens in the StickyPunch database
