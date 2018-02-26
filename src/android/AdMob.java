package com.techingcrew.cordova.plugin.AdMob;

import org.apache.cordova.*; 
import org.apache.cordova.engine.SystemWebView;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.ads.*;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;

public class AdMob extends CordovaPlugin {
    private static final String TAG = "Admob-TechingCrew LLC";
    private String appID;
    private State AppState = new State();
    private final int _TOP = 0;
    private final int _BOTTOM = 2;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        AppState.cordovaView = webView;
        AppState.context = this.cordova.getActivity().getApplicationContext();
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        AppState.callbackContext = callbackContext;
        if(action.equals("init")){
            if(AppState.isInitialized == false){
                try{
                    ViewGroup cordovaParentGroup = (ViewGroup)((View)AppState.cordovaView.getView()).getParent();
                    AppState.cordovaLinear = (LinearLayout)cordovaParentGroup.getParent();
                    appID = args.getString(0);
                    MobileAds.initialize(cordova.getActivity(), appID);
                    AppState.isInitialized = true;
                    echo("AdMob Initialized");
                }
                catch(Exception e){
                    echoError("AdMob Initialization Error: " + e.getMessage());         
                }  
            }
            return true; 
        }
        else if (action.equals("createBanner")) {
            if(AppState.isInitialized == true && AppState.adView == null){
                try{
                    AppState.bannerAdID = args.getString(0);
                    setBannerSize(args.getString(1));
                    if(args.getString(2).equalsIgnoreCase("true")){
                        AppState.showBanner = true;
                    }
                    else if(args.getString(2).equalsIgnoreCase("false")){
                        AppState.showBanner = false;
                    }
                    cordova.getActivity().runOnUiThread(new Runnable(){
			            @Override
			            public void run() {
                            AppState.adView = new AdView(cordova.getActivity());
                            AppState.adView.setAdSize(AppState.bannerSize);
                            AppState.adView.setAdUnitId(AppState.bannerAdID);
                            AppState.adView.setAdListener(new AdListener() {
                                @Override
                                public void onAdLoaded() {
                                    // Code to be executed when an ad finishes loading.
                                    Log.d(TAG,"Banner Loaded");
                                    fireJavascriptEvent("bannerLoaded", "");
                                    if(AppState.showBanner == true){
                                        showBanner();
                                    }
                                }

                                @Override
                                public void onAdFailedToLoad(int errorCode) {
                                    // Code to be executed when an ad request fails.
                                    Log.d(TAG,"onAdFailedToLoad Error: " +  Integer.toString(errorCode));
                                    fireJavascriptEvent("bannerFailedToLoad", "");
                                }

                                @Override
                                public void onAdOpened() {
                                    // Code to be executed when an ad opens an overlay that
                                    // covers the screen.
                                    Log.d(TAG,"Banner Opened");
                                    fireJavascriptEvent("bannerOpened", "");
                                }

                                @Override
                                public void onAdLeftApplication() {
                                    // Code to be executed when the user has left the app.
                                    Log.d(TAG,"Banner Left Application");
                                    fireJavascriptEvent("bannerLeftApplication", "");
                                }

                                @Override
                                public void onAdClosed() {
                                    // Code to be executed when when the user is about to return
                                    // to the app after tapping on an ad.
                                    Log.d(TAG,"Banner Ad Closed");
                                    fireJavascriptEvent("bannerClosed", "");
                                }
                            });
                            AppState.adView.setVisibility(View.VISIBLE);
                            AppState.adView.loadAd(new AdRequest.Builder().build());
                        }
                    });
                }
                catch(Exception e){
                    echoError("Banner Error: " + e.getMessage());
                }
            }
            else if(!AppState.isInitialized){
                echoError("Use admob.init() before creating an ad unit.");
            }
            else{
                echoError("Banner already created.");
            }
            return true;
        }
        else if(action.equals("showBanner")){
            if(AppState.isBannerVisible == false){
                if(args.getString(0).equalsIgnoreCase("top")){
                    AppState.bannerIndex = _TOP;
                }
                else if(args.getString(0).equalsIgnoreCase("bottom")){
                    AppState.bannerIndex = _BOTTOM;
                }
                if(args.getString(1) != null){
                    if(args.getString(1).equalsIgnoreCase("true")){
                        AppState.overlapView = true;
                    }
                    else if(args.getString(1).equalsIgnoreCase("false")){
                        AppState.overlapView = false;
                    }
                }
                cordova.getActivity().runOnUiThread(new Runnable(){
			        @Override
			        public void run() {
                        showBanner();
                    }
                });
            }        
            return true;
        }
        else if(action.equals("destroyBanner")){
            if(AppState.isBannerVisible == true){
                cordova.getActivity().runOnUiThread(new Runnable(){
			        @Override
			        public void run() {
                        destroyBanner();
                    }
                });
            }        
            return true;
        }
        else if(action.equals("createInterstitial")){
            final String interstitialId = args.getString(0);
            if(AppState.isInitialized == true){
                cordova.getActivity().runOnUiThread(new Runnable(){
			        @Override
			        public void run() {
                        AppState.interstitialAd = new InterstitialAd(cordova.getActivity());
                        AppState.interstitialAd.setAdUnitId(interstitialId);
                        AppState.interstitialAd.setImmersiveMode(true);
                        loadInterstitial();
                    }
                });
            }
            else{
                echo("AdMob not initialized. Run admob.init()");
            }
            return true;
        }
        else if(action.equals("showInterstitial")){
                cordova.getActivity().runOnUiThread(new Runnable(){
			        @Override
			        public void run() {
                        if(AppState.interstitialAd.isLoaded()){
                            AppState.interstitialAd.show();
                        }
                        else{
                            echoError("Interstitial ad not loaded yet. Please wait for the 'interstitialLoaded' event to fire.");
                        }
                    }
                });
            return true;
        }
        else if(action.equals("createRewarded")){
            if(AppState.isInitialized == true){
                AppState.rewardedID = args.getString(0);
                AppState.userID = args.getString(1);
                loadRewarded();
            }
            return true;
        }
        else if(action.equals("showRewarded")){            
                cordova.getActivity().runOnUiThread(new Runnable(){
			        @Override
			        public void run() {
                        if(AppState.rewardAd.isLoaded()){
                            AppState.rewardAd.show();
                        }
                        else{
                            echoError("Rewarded ad not loaded yet. Please wait for the 'rewardedLoaded' event to fire.");
                        }
                    }
                });
            return true;
        }
        return false;
    }

    private void destroyBanner(){
        AppState.showBanner = false;
        if(AppState.isBannerVisible == true){
            if(AppState.overlapView == false){
                AppState.cordovaLinear.removeView(AppState.adView);
                AppState.isBannerVisible = false;
                echo("Banner is no longer visible.");
            }
            else if(AppState.overlapView == true){
                AppState.newRelative.removeView(AppState.adView);
                AppState.cordovaLinear.bringToFront();
                AppState.newRelative.removeView(AppState.cordovaLinear);
                AppState.cordovaLinear.bringToFront();
                AppState.isBannerVisible = false;
                echo("Banner is no longer visible.");
            }
            AppState.adView.destroy();
            AppState.adView = null;

        }
        else{
            echoError("Banner is not visible.");
        }
    }

    private void showBanner(){
        try{
            if(AppState.adView != null){
                if(AppState.isBannerVisible == false){
                    LinearLayout newLayout = new LinearLayout(AppState.cordovaView.getContext());
                    SystemWebView cordovaSuper = (SystemWebView)AppState.cordovaView.getView();
                    ViewGroup cordovaParentGroup = (ViewGroup)((View)AppState.cordovaView.getView()).getParent();
                    LinearLayout.LayoutParams frameParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
                    ((FrameLayout)(AppState.cordovaLinear.getChildAt(1))).setLayoutParams(frameParam);
                    if(AppState.overlapView == true){
                        RelativeLayout newRelative = new RelativeLayout(AppState.cordovaView.getContext());
                        RelativeLayout.LayoutParams relParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        if(AppState.bannerIndex == 0){
                            relParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                        }
                        else if(AppState.bannerIndex == 2){
                            relParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        }
                        AppState.adView.setLayoutParams(relParams);
                        ((ViewGroup)AppState.cordovaView.getView().getParent()).removeView(AppState.cordovaView.getView());
                        newRelative.addView((View)AppState.cordovaView.getView());
                        newRelative.addView(AppState.adView);
                        cordova.getActivity().setContentView(newRelative);
                        AppState.adView.bringToFront();
                        AppState.newRelative = newRelative;
                    }
                    else{
                        AppState.cordovaLinear.addView(AppState.adView, AppState.bannerIndex);
                    }
                    AppState.isBannerVisible = true;
                }
                else{
                    echoError("Banner Ad is already visible.");
                }
            }
            else{
                echoError("Banner Ad not created yet");
            }
        }
        catch(Exception e){
            Log.d(TAG, e.getMessage());
            echoError(e.getMessage());
        }
    }

    private void setBannerSize(String size){     
        if(size.equalsIgnoreCase("BANNER")){
            AppState.bannerSize = AdSize.BANNER;
        }
        else if(size.equalsIgnoreCase("LARGE_BANNER")){
            AppState.bannerSize = AdSize.LARGE_BANNER;
        }
        else if(size.equalsIgnoreCase("MEDIUM_RECTANGLE")){
            AppState.bannerSize = AdSize.MEDIUM_RECTANGLE;
        }
        else if(size.equalsIgnoreCase("FULL_BANNER")){
            AppState.bannerSize = AdSize.FULL_BANNER;
        }
        else if(size.equalsIgnoreCase("LEADERBOARD")){
            AppState.bannerSize = AdSize.LEADERBOARD;
        }
        else if(size.equalsIgnoreCase("SMART_BANNER")){
            AppState.bannerSize = AdSize.SMART_BANNER;
        }
        else{
            AppState.bannerSize = AdSize.SMART_BANNER;
        }
    }

    private void loadInterstitial(){
        AppState.interstitialAd.loadAd(new AdRequest.Builder().build());
        AppState.interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                fireJavascriptEvent("interstitialLoaded", "");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                echoError("Interstitial Failed to Load - Error Code: " + errorCode);
                fireJavascriptEvent("interstitialFailedToLoad", "");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
                fireJavascriptEvent("interstitialOpened", "");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                fireJavascriptEvent("interstitialLeftApplication", "");
                loadInterstitial();
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                fireJavascriptEvent("interstitialClosed", "");
                loadInterstitial();
            }
        });
    }

    private void loadRewarded(){
        cordova.getActivity().runOnUiThread(new Runnable(){
		    @Override
		    public void run() {
                AppState.rewardAd = MobileAds.getRewardedVideoAdInstance(cordova.getActivity());
                AppState.rewardAd.setUserId(AppState.userID);
                AppState.rewardAd.setRewardedVideoAdListener(new RewardedVideoAdListener(){
                    @Override
                    public void onRewarded(RewardItem reward) {
                        // Reward the user.
                        String data = "\"rewardType\":\"" + reward.getType() + "\",\"rewardAmount\":\"" + reward.getAmount() + "\"";
                        fireJavascriptEvent("rewardEarned", data);
                    }

                    @Override
                    public void onRewardedVideoAdLeftApplication() {
                        fireJavascriptEvent("rewardedLeftApplication", "");
                        loadRewarded();
                    }

                    @Override
                    public void onRewardedVideoAdClosed() {
                        fireJavascriptEvent("rewardedClosed", "");
                        loadRewarded();
                    }

                    @Override
                    public void onRewardedVideoAdFailedToLoad(int errorCode) {
                        fireJavascriptEvent("rewardedFailedToLoad", "\"errorCode\":\"" + errorCode + "\"");
                        
                    }

                    @Override
                    public void onRewardedVideoAdLoaded() {
                        fireJavascriptEvent("rewardedLoaded", "");
                                
                    }

                    @Override
                    public void onRewardedVideoAdOpened() {
                        fireJavascriptEvent("rewardedOpened", "");
                    }

                    @Override
                    public void onRewardedVideoStarted() {
                        fireJavascriptEvent("rewardedStarted", "");
                    }
                
                });
                AppState.rewardAd.loadAd(AppState.rewardedID, new AdRequest.Builder().build());
            }
        });
    }

    private void fireJavascriptEvent(String event, String data){
        AppState.cordovaView.loadUrl("javascript:cordova.fireDocumentEvent('" + event + "', {" + data + "});");
    }

    private void echo(String message){
        PluginResult pr = new PluginResult(PluginResult.Status.OK, message);
        pr.setKeepCallback(true);
        AppState.callbackContext.sendPluginResult(pr);
    }

    private void echoError(String message) {
        PluginResult pr2 = new PluginResult(PluginResult.Status.ERROR, message);
        pr2.setKeepCallback(true);
        AppState.callbackContext.sendPluginResult(pr2);
    }

    private void debugChildren(ViewGroup vg){
        for (int x = 0; x < vg.getChildCount(); x++) {
            Log.d(TAG, "Child " + x + ": " + vg.getChildAt(x).getClass().toString());
        }
    }

    private class State{
        private AdView adView;
        private InterstitialAd interstitialAd;
        private RewardedVideoAd rewardAd;
        private CordovaWebView cordovaView;
        private RelativeLayout newRelative;
        private LinearLayout cordovaLinear;
        private int bannerIndex;
        private AdSize bannerSize = AdSize.SMART_BANNER;
        private boolean isInitialized = false;
        private boolean showBanner = false;
        private boolean isBannerVisible = false;
        private boolean overlapView = false;
        private String bannerAdID;
        private String appID;
        private String rewardedID;
        private String interstitialId;
        private String userID;
        private CallbackContext callbackContext;
        private Context context;
    }

    @Override
    public void onResume(boolean multitasking) {
        if(AppState.adView != null){
            AppState.adView.resume();
        }
        if(AppState.rewardAd != null){
            AppState.rewardAd.resume(AppState.cordovaView.getContext());
        }
        super.onResume(multitasking);
    }

    @Override
    public void onPause(boolean multitasking) {
        if(AppState.adView != null){
            AppState.adView.pause();
        }
        if(AppState.rewardAd != null){
            AppState.rewardAd.pause(AppState.cordovaView.getContext());
        }
        
        super.onPause(multitasking);
    }

    @Override
    public void onDestroy() {
        destroyBanner();
        if(AppState.rewardAd != null){
            AppState.rewardAd.destroy(AppState.cordovaView.getContext());
        }
        super.onDestroy();
    }
}