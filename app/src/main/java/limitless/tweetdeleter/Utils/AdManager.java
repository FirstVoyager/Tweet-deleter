package limitless.tweetdeleter.Utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;

import limitless.tweetdeleter.ApplicationLoader;
import limitless.tweetdeleter.BuildConfig;
import limitless.tweetdeleter.R;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

/**
 * Manage your ads
 */
public class AdManager {

    public static void showInterstitialAds(Context context) {
        if (context == null)
            return;
        if (new SharePref(context).getBoolean(SharePref.ProVersion, false))
            return;
        final InterstitialAd interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(context.getString(R.string.ad_id_interstitial));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                interstitialAd.show();
                new SharePref(context).putLong(SharePref.last_ad_show_time, new Date().getTime());
            }
        });
    }

    public static void showInterstitialAds(final Context context, boolean b) {
        if (context == null)
            return;
        if (new SharePref(context).getBoolean(SharePref.ProVersion, false))
            return;
        final InterstitialAd interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(context.getString(R.string.ad_id_interstitial));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                interstitialAd.show();
                new SharePref(context).putLong(SharePref.last_ad_show_time, new Date().getTime());
                new SharePref(context).putBoolean(SharePref.showIntersitialStart, false);
            }
        });
    }

    public static void loadAds(AdView adView) {
        if (adView == null)
            return;
        if (new SharePref(adView.getContext()).getBoolean(SharePref.ProVersion, false)) {
            adView.setVisibility(View.GONE);
            return;
        }
        adView.loadAd(new AdRequest.Builder().build());
    }

}
