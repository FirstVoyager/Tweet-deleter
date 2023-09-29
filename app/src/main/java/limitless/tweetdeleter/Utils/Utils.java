package limitless.tweetdeleter.Utils;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentManager;
import limitless.tweetdeleter.BuildConfig;
import limitless.tweetdeleter.Dialog.ActionDialog;
import limitless.tweetdeleter.Other.Listener;
import limitless.tweetdeleter.Other.Model.TextFilterModel;
import limitless.tweetdeleter.Other.Model.UserFilterModel;
import limitless.tweetdeleter.R;
import twitter4j.MediaEntity;
import twitter4j.Status;

public class Utils {

    public static boolean isOnline(Context context){
        try {
            if (context == null)
                return false;
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo  networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }catch (Exception e){
            error(e);
            return false;
        }
    }

    public static void showAlertDialogNoInternet(Context context, final Listener<Void> listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("No Internet, please try again");
        builder.setPositiveButton("Try", (dialog, which) -> {
            listener.data(null);
            dialog.dismiss();
        });
        builder.setNegativeButton("Exit", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public static void shareText(Context context, String s) {
        Intent intent = new Intent();
        intent.setType("text/plain");
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, s);
        startActivity(context, intent);
    }

    public static void startActivity(Context context, Intent intent) {
        if (context == null || intent == null)
            return;
        try {
            context.startActivity(intent);
        }catch (Exception e){
            error(e);
        }
    }

    public static void openEmail(Context context, String email, String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SENDTO);
        intent.setData(Uri.fromParts("mailto", email, null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Email From Tweet Deleter");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(context, intent);
    }

    /**
     * Open url in other app
     * @param context
     * @param url Want to open
     */
    public static void openUrl(Context context, String url) {
        if (context == null || url == null)
            return;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(context, intent);
    }

    public static void openUrl(Context context, @StringRes int res) {
        if (context == null)
            return;
        openUrl(context, context.getString(res));
    }

    public static int selectedColor() {
        return Color.argb(100, 60, 20, 30);
    }

    public static void toast(@Nullable Context context, @Nullable String s) {
        if (context == null || s == null)
            return;
        try {
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            error(e);
        }
    }

    public static void toast(@Nullable Context context, @StringRes int resId){
        if (context == null)
            return;
        toast(context, context.getString(resId));
    }

    public static void error(Exception e) {
        if (e == null)
            return;
        e.printStackTrace();
    }

    public static void showAlertDialogMessage(Context context, final SharePref sharePref) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle("Tweet deleter message");
        builder.setMessage(context.getString(R.string.error_message));
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sharePref.putBoolean(SharePref.showDeleteMessage, false);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static boolean showAds(Context context) {
        return new SharePref(context).getBoolean(SharePref.showAds, true);
    }

    public static void copyText(Context context, String text) {
        if (context == null || text == null)
            return;
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData description = ClipData.newPlainText(null, text);
        if (clipboardManager != null)
            clipboardManager.setPrimaryClip(description);
    }

    public static void startActivity(Context context, Class<?> aClass) {
        if (context != null && aClass != null)
            startActivity(context, new Intent(context, aClass));
    }

    public static boolean hasVideo(MediaEntity[] mediaEntities) {
        if (mediaEntities == null || mediaEntities.length <= 0)
            return false;
        return mediaEntities[0].getType().equals("video");
    }

    public static boolean hasPhoto(MediaEntity[] mediaEntities) {
        if (mediaEntities == null || mediaEntities.length <= 0)
            return false;
        return mediaEntities[0].getType().equals("photo");
    }

    public static String convertLongToTime(long time, boolean m, boolean s, boolean ms) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
        long milliSecond = TimeUnit.MILLISECONDS.toMillis(time);
        String pattern = null;
        if (m){
            pattern = "%02d";
        }
        if (s){
            if (m)
                pattern += ":%02d";
            else
                pattern = "%02d";
        }
        if (ms){
            if (s)
                pattern += ".%03d";
            else
                pattern = "%03d";
        }
        if (pattern == null)
            return "";
        return String.format(pattern,
                m ? minutes : 0,
                s ? seconds : 0,
                ms ? milliSecond : 0);
    }

    public static boolean hasMedia(MediaEntity[] mediaEntities) {
        return mediaEntities != null && mediaEntities.length > 0;
    }

    public static String googlePlayUrl(String s) {
        return "https://play.google.com/store/apps/details?id=" + s;
    }

    public static boolean isEmpty(String s) {
        if (s == null)
            return true;
        return s.isEmpty();
    }

    public static void showDialog(ActionDialog dialog, FragmentManager fragmentManager) {
        if (dialog != null && fragmentManager != null){
            try {
                dialog.show(fragmentManager, null);
            }catch (Exception e){
                error(e);
            }
        }
    }

    public static boolean hasFilterText(String main, List<TextFilterModel> filters) {
        if (main == null || filters == null)
            return false;
        for (TextFilterModel tfm : filters)
            if (main.contains(tfm.text.trim()))
                return true;
        return false;
    }

    public static boolean hasUser(Status s, List<UserFilterModel> users) {
        if (s == null || users == null)
            return false;
        for (UserFilterModel u : users){
            if (s.isRetweet())
                if (s.getRetweetedStatus().getUser().getId() == u.idLong)
                    return true;
            if (s.getInReplyToUserId() > 0 && s.getInReplyToUserId() == u.idLong)
                return true;
            if (s.getUser().getId() == u.idLong)
                return true;
        }
        return false;
    }

    public static int getTheme(Context context) {
        switch (new SharePref(context).getInt(SharePref.themeIndex, 0)){
            case 0:
            default:
                return R.style.AppTheme_NoActionBar;
            case 1:
                return R.style.AppTheme_Red;
            case 2:
                return R.style.AppTheme_Pink;
            case 3:
                return R.style.AppTheme_Purple;
            case 4:
                return R.style.AppTheme_DeepPurple;
            case 5:
                return R.style.AppTheme_Indigo;
            case 6:
                return R.style.AppTheme_Blue;
            case 7:
                return R.style.AppTheme_LightBlue;
            case 8:
                return R.style.AppTheme_Cyan;
            case 9:
                return R.style.AppTheme_Teal;
            case 10:
                return R.style.AppTheme_Green;
            case 11:
                return R.style.AppTheme_LightGreen;
            case 12:
                return R.style.AppTheme_Lime;
            case 13:
                return R.style.AppTheme_Amber;
            case 14:
                return R.style.AppTheme_Orange;
            case 15:
                return R.style.AppTheme_DeepOrange;
            case 16:
                return R.style.AppTheme_Brown;
            case 17:
                return R.style.AppTheme_Gray;
            case 18:
                return R.style.AppTheme_BlueGray;
        }
    }

    public static boolean tweetUrl(long tweetId) {
        return false;
    }

    public static void loadImage(Context context, String data, ImageView iv, boolean circle) {
        if (circle){
            Glide
                    .with(context)
                    .load(data)
                    .apply(RequestOptions.circleCropTransform())
                    .into(iv);
        }else {
            Glide
                    .with(context)
                    .load(data)
                    .into(iv);
        }
    }

    public static void log(String log) {
        if (log != null){
            Log.i("Tweet_Log", log);
        }
    }

    public static void removeCookie() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
    }
}
