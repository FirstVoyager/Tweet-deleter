package limitless.tweetdeleter.Activity;

import android.os.Bundle;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import limitless.tweetdeleter.R;
import limitless.tweetdeleter.Utils.AdManager;
import limitless.tweetdeleter.Utils.Utils;

public abstract class BaseActivity extends AppCompatActivity {

    public abstract String getName();
    public abstract boolean changeTheme();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            if (changeTheme()){
                setTheme(Utils.getTheme(this));
            }
            MobileAds.initialize(this);
        } catch (Exception e) {
            Utils.error(e);
        }
        super.onCreate(savedInstanceState);
    }

    /**
     * Load banner ads
     * @param adView Your banner
     */
    public void loadBanner(AdView adView) {
        if (adView == null)
            return;
        AdManager.loadAds(adView);
    }

}
