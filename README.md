# cordova-plugin-admob-techingcrew
AdMob plugin for Cordova multi-platform apps. Exposes Banner Ads, Interstitial Ads, and Rewarded Videos.

This plugin uses Gradle/Maven to pull the latest Google Play Services - Ads SDK. Tested on Samsung Galaxy S6 using Android 7.0 and project built using CLI 6.1.1 -iOS version is in development.

1. Install the plugin:

```
cordova plugin add cordova-plugin-add-admob-techingcrew
```

2. Adjust the AdMob settings in JavaScript. The plugin comes pre-loaded with test credentials.

```
admob.settings = {
    overlapWebView: false,
    bannerAutoShow: false,
    bannerPosition: 'top', // or 'bottom'
    bannerSize: 'SMART_BANNER', // see sizes at https://developers.google.com/admob/android/banner
    bannerID: 'ca-app-pub-3940256099942544/6300978111',
    interstitialID: 'ca-app-pub-3940256099942544/1033173712',
    rewardID: 'ca-app-pub-3940256099942544/5224354917',
    appID: 'ca-app-pub-3940256099942544~3347511713',
    userID: 'xxx'
};
```

3. Initialize MobileAds:

```
admob.init(successCallback, errorCallback);
```

4. Build and show Banner Ads (Optional):

```
admob.createBanner(successCallback, errorCallback);
```
   After the first time "bannerLoaded" is fired, you can show the Banner:
```
admob.showBanner(successCallback, errorCallback);
```

5. Build and show Interstitial Ads (Optional):
```
admob.createInterstitial(successCallback, errorCallback);
```
   You only need to call this function once. After "interstitialLoaded" is fired, you can show the ad. After the ad is closed, a new ad    will be requested automatically.
```
admob.showInterstitial(successCallback, errorCallback);
```

6. Build and show Rewarded Videos (Optional):
```
admob.createRewardedVideo(successCallback, errorCallback);
```
You only need to call this function once. After "rewardedLoaded" is fired, you can show the video. After the video is closed, a new video will be requested automatically.
```
admob.showRewardedVideo(successCallback, errorCallback);
```


The following code can be used in a demo app to test the functionality:

```
<script>
    function setOptions() {
        admob.settings = {
            overlapWebView: false,
            bannerAutoShow: false,
            bannerPosition: 'top',
            bannerSize: 'SMART_BANNER',
            bannerID: 'ca-app-pub-3940256099942544/6300978111',
            interstitialID: 'ca-app-pub-3940256099942544/1033173712',
            rewardID: 'ca-app-pub-3940256099942544/5224354917',
            appID: 'ca-app-pub-3940256099942544~3347511713',
            userID: 'xxx'
        };
    }
    
    document.addEventListener('admobInit', function () { console.log('admobInit'); });
    document.addEventListener('bannerLoaded', function () { console.log('bannerLoaded'); });
    document.addEventListener('bannerFailedToLoad', function () { console.log('bannerFailedToLoad'); });
    document.addEventListener('bannerOpened', function () { console.log('bannerOpened'); });
    document.addEventListener('bannerLeftApplication', function () { console.log('bannerLeftApplication'); });
    document.addEventListener('interstitialLoaded', function () { console.log('interstitialLoaded'); });
    document.addEventListener('interstitialFailedToLoad', function () { console.log('interstitialFailedToLoad'); });
    document.addEventListener('interstitialOpened', function () { console.log('interstitialOpened'); });
    document.addEventListener('interstitialLeftApplication', function () { console.log('interstitialLeftApplication'); });
    document.addEventListener('interstitialClosed', function () { console.log('interstitialClosed'); });
    document.addEventListener('rewardEarned', function (event) { console.log('rewardEarned'); console.log(event); console.log('rewardType: ' + event.rewardType); console.log('rewardAmount: ' + event.rewardAmount); });
    document.addEventListener('rewardedLeftApplication', function () { console.log('rewardedLeftApplication'); });
    document.addEventListener('rewardedClosed', function () { console.log('rewardedClosed'); });
    document.addEventListener('rewardedFailedToLoad', function () { console.log('rewardedFailedToLoad'); });
    document.addEventListener('rewardedLoaded', function () { console.log('rewardedLoaded'); });
    document.addEventListener('rewardedOpened', function () { console.log('rewardedOpened'); });
    document.addEventListener('rewardedStarted', function () { console.log('rewardedStarted'); });
</script>

<button type="button" onclick="setOptions(); admob.init(function (response) { console.log(response); }, function (error) { console.log('Init Error: ' + error);});">Init</button>
<br /><br />
<br /><br />
<button type="button" onclick="admob.createBanner(function (response) { console.log('Create Banner Success:' + response); }, function (error) { console.log('Create Banner Error: ' + error);});">Create Banner</button>
<br /><br />
<button type="button" onclick="admob.settings.overlapWebView = false; admob.settings.bannerPosition = 'top';admob.showBanner(function (response) {console.log('Show Top Success:' + response); }, function (error) { console.log('Show Top Error: ' + error);});">Show Top Banner</button>
<br /><br />
<button type="button" onclick="admob.settings.overlapWebView = true; admob.settings.bannerPosition = 'bottom'; admob.showBanner(function (response) {console.log('Show Bottom Overlap Success:' + response); }, function (error) { console.log('Show Bottom Overlap Error: ' + error);});">Show Bottom Overlapping Banner</button>
<br /><br />
<button type="button" onclick="admob.destroyBanner(function (response) { console.log('Destroy Banner Success:' + response); }, function (error) { console.log('Destroy Banner Error: ' + error);});">Destroy Banner</button>
<br /><br />
<br /><br />
<button type="button" onclick="admob.createInterstitial(function (response) { console.log('Create Interstitial Success:' + response); }, function (error) { console.log('Create Interstitial Error: ' + error);});">Create Interstitial</button>
<br /><br />
<button type="button" onclick="admob.showInterstitial(function (response) { console.log('Show Interstitial Success:' + response); }, function (error) { console.log('Show Interstitial Error: ' + error);});">Show Interstitial</button>
<br /><br />
<br /><br />
<button type="button" onclick="admob.createRewardedVideo(function (response) { console.log('Create Rewarded Success:' + response); }, function (error) { console.log('Create Rewarded Error: ' + error);});">Create Rewarded Video</button>
<br /><br />
<button type="button" onclick="admob.showRewardedVideo(function (response) { console.log('Show Rewarded Success:' + response); }, function (error) { console.log('Show Rewarded Error: ' + error);});">Show Rewarded Video</button>
<br /><br />



```
