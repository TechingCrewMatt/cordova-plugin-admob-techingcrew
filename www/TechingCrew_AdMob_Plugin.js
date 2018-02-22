var exec = require('cordova/exec');

var admob = {
    settings: {
        overlapWebView: false,
        bannerAutoShow: false,
        bannerPosition: 'top',
        bannerSize: 'SMART_BANNER',
        bannerID: 'ca-app-pub-3940256099942544/6300978111',
        interstitialID: 'ca-app-pub-3940256099942544/1033173712',
        rewardID: 'ca-app-pub-3940256099942544/5224354917',
        appID: 'ca-app-pub-3940256099942544~3347511713',
        userID: 'xxx'
    },
    init: function (success, error) {
        cordova.exec(success, error, 'AdMob', 'init', [this.settings.appID]);
    },
    createBanner: function (success, error) {
        cordova.exec(success, error, 'AdMob', 'createBanner', [this.settings.bannerID, this.settings.bannerSize, this.settings.bannerAutoShow]);
    },
    showBanner: function (success, error) {
        cordova.exec(success, error, 'AdMob', 'showBanner', [this.settings.bannerPosition, this.settings.overlapWebView]);
    },
    destroyBanner: function (success, error) {
        cordova.exec(success, error, 'AdMob', 'destroyBanner', []);
    },
    createInterstitial: function (success, error) {
        cordova.exec(success, error, 'AdMob', 'createInterstitial', [this.settings.interstitialID]);
    },
    showInterstitial: function (success, error) {
        cordova.exec(success, error, 'AdMob', 'showInterstitial', []);
    },
    createRewardedVideo: function (success, error) {
        cordova.exec(success, error, 'AdMob', 'createRewarded', [this.settings.rewardID, this.settings.userID]);
    },
    showRewardedVideo: function (success, error) {
        cordova.exec(success, error, 'AdMob', 'showRewarded', []);
    }
}

module.exports = admob;
