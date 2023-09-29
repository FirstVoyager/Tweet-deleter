package limitless.tweetdeleter;

import android.app.Application;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Collections;

import limitless.tweetdeleter.Utils.AdManager;
import limitless.tweetdeleter.Utils.SharePref;


public class ApplicationLoader extends Application implements BillingProcessor.IBillingHandler {

    public static final String PRO_VERSION_PRODUCT_ID = "product_1";

    private BillingProcessor billingProcessor;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG){ // Show test ads when is debug version
            RequestConfiguration rc = new RequestConfiguration
                    .Builder().setTestDeviceIds(Collections.singletonList("1959AA4068493046177228B46CB92270"))
                    .build();
            MobileAds.setRequestConfiguration(rc);
        }
        MobileAds.initialize(getApplicationContext(), initializationStatus -> {});

        billingProcessor = new BillingProcessor(this, getString(R.string.licens_key), this);
        billingProcessor.initialize();
//        appOpenAdManager = new AdManager.AppOpenAdManager(this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (billingProcessor != null)
            billingProcessor.release();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {
        restore();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {
        restore();
    }

    private void restore() {
        if (BuildConfig.DEBUG) {
            new SharePref(this).putBoolean(SharePref.ProVersion, true);
            return;
        }
        if (billingProcessor.loadOwnedPurchasesFromGoogle() && billingProcessor.isSubscribed(PRO_VERSION_PRODUCT_ID)) {
            new SharePref(this).putBoolean(SharePref.ProVersion, true);
        }else {
            new SharePref(this).putBoolean(SharePref.ProVersion, false);
        }
    }

}
