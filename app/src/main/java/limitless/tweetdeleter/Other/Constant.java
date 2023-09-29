package limitless.tweetdeleter.Other;

import android.os.Debug;

import androidx.fragment.app.Fragment;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import limitless.tweetdeleter.BuildConfig;
import limitless.tweetdeleter.Fragment.LikesFragment;
import limitless.tweetdeleter.Fragment.RepliesFragment;
import limitless.tweetdeleter.Fragment.ReTweetFragment;
import limitless.tweetdeleter.Fragment.TweetFragment;

public class Constant {

    public static boolean showAds = false;

    public static final String ProxyHost= "ProxyHost";
    public static final String ProxyPort = "ProxyPort";
    public static String share_pref_name = "limitless.tweetdeleter.share.pref";

    // back url
    public static String twitter_back_url = "https://limitless.linux";
    public static String oauth_verifier = "oauth_verifier";

    // app for linux, Delepmentfree
    public static String oAuthConsumerKey = "27tRTpDIAhevXtW6ciaFj8RNY";
    public static String oAuthConsumerSecret = "Up3OW9RKkVMeaaxqx19iRZmM77bPc8nsZP2WKgxIN8ZvdP22wX";

    public static String userCustomApiKey = "userCustomApiKey";
    public static String useProxy = "useProxy";

    public static int freeActionCount() {
        if (BuildConfig.DEBUG)
            return 5;
        return 200;
    }

    public static long showAdTime() {
        return ThreadLocalRandom.current().nextLong(1000, 5000);
    }
}
