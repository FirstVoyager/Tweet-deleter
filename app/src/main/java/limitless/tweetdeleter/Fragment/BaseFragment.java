package limitless.tweetdeleter.Fragment;

import android.os.Bundle;

import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import limitless.tweetdeleter.Dialog.ActionDialog;
import limitless.tweetdeleter.Other.BaseActions;
import limitless.tweetdeleter.Other.Constant;
import limitless.tweetdeleter.Other.Listener;
import limitless.tweetdeleter.Other.Model.TextFilterModel;
import limitless.tweetdeleter.Other.Model.UserFilterModel;
import limitless.tweetdeleter.R;
import limitless.tweetdeleter.Utils.AdManager;
import limitless.tweetdeleter.Utils.Deleter;
import limitless.tweetdeleter.Utils.SQLiteDeleter;
import limitless.tweetdeleter.Utils.SharePref;
import limitless.tweetdeleter.Utils.Utils;
import twitter4j.Status;
import twitter4j.User;

public abstract class BaseFragment extends Fragment implements BaseActions {

    private SQLiteDeleter sqlite;

    public Deleter deleter;
    public SharePref sharePref;
    /**
     * Main account id
     */
    public long accountId = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deleter = new Deleter(getContext());
        sqlite = new SQLiteDeleter(getContext());
        sharePref = new SharePref(getContext());

        try {
            accountId = sqlite.getMainAccount().id;
        } catch (Exception e) {
            Utils.error(e);
            accountId = 0;
        }
    }

    @Override
    public void deleteTweets(List<Status> statuses) {
        if (statuses == null || statuses.size() <= 0) {
            Utils.toast(getContext(), R.string.no_tweet);
            return;
        }
        ActionDialog dialog = new ActionDialog(
                getString(R.string.delete),
                getString(R.string.t_tweets),
                statuses.size(),
                new Listener<Boolean>() {
                    @Override
                    public void data(Boolean aBoolean) {
                        super.data(aBoolean);
                        if (aBoolean){
                            AdManager.showInterstitialAds(getContext());
                            for (Status s : statuses) {
                                if (hasFilter(s))
                                    delete(s.getId());
                            }
                        }
                    }
                }
        );
        Utils.showDialog(dialog, getParentFragmentManager());
    }

    @Override
    public void unlikeTweets(List<Status> statuses) {
        if (statuses == null || statuses.size() <= 0) {
            Utils.toast(getContext(), R.string.no_tweet);
            return;
        }
        ActionDialog actionDialog = new ActionDialog(
                getString(R.string.unlike),
                getString(R.string.t_tweets),
                statuses.size(),
                new Listener<Boolean>() {
                    @Override
                    public void data(Boolean aBoolean) {
                        super.data(aBoolean);
                        if (aBoolean){
                            AdManager.showInterstitialAds(getContext());
                            for (Status s : statuses) {
                                if (hasFilter(s))
                                    unLike(s.getId());
                            }
                        }
                    }
                }
        );
        Utils.showDialog(actionDialog, getParentFragmentManager());
    }

    @Override
    public void unBlockUsers(List<User> users) {
        if (users == null || users.size() <= 0) {
            Utils.toast(getContext(), R.string.user_is_empty);
            return;
        }
        ActionDialog actionDialog = new ActionDialog(
                getString(R.string.unblock),
                getString(R.string.t_users),
                users.size(),
                new Listener<Boolean>() {
                    @Override
                    public void data(Boolean aBoolean) {
                        super.data(aBoolean);
                        if (aBoolean){
                            AdManager.showInterstitialAds(getContext());
                            for (User u : users)
                                unblock(u.getId());
                        }
                    }
                }
        );
        Utils.showDialog(actionDialog, getParentFragmentManager());
    }

    @Override
    public void unMuteUsers(List<User> users) {
        if (users == null || users.size() <= 0) {
            Utils.toast(getContext(), R.string.user_is_empty);
            return;
        }
        ActionDialog actionDialog = new ActionDialog(
                getString(R.string.unmute),
                getString(R.string.t_users),
                users.size(),
                new Listener<Boolean>() {
                    @Override
                    public void data(Boolean action) {
                        super.data(action);
                        if (action){
                            AdManager.showInterstitialAds(getContext());
                            for (User u : users)
                                unmute(u.getId());
                        }
                    }
                }
        );
        Utils.showDialog(actionDialog, getParentFragmentManager());
    }

    /**
     * @param s Tweet
     * @return Delete tweet or no
     */
    private boolean hasFilter(Status s){
        List<TextFilterModel> texts = sqlite.getTexts();
        List<UserFilterModel> users = sqlite.getUsers();
        if (sharePref.getBoolean(SharePref.userFilter, false)){
            if (texts != null){
                if (Utils.hasFilterText(s.getText(), texts)){
                    return false;
                }
            }
            if (users != null) {
                return ! Utils.hasUser(s, users);
            }
        }
        return true;
    }

    /**
     * Delete tweet
     * @param id Tweet id
     */
    private void delete(long id) {
        showAds();
        deleter.deleteTweet(id, new Listener<Status>() {
            @Override
            public void data(Status status) {
                super.data(status);
                if (status != null){
                    Utils.toast(getContext(), status.getText());
                    removeFromList(status.getId());
                }
            }
        });
    }

    /**
     * Unlike tweet
     * @param id Tweet id
     */
    private void unLike(long id) {
        showAds();
        deleter.unLike(id, new Listener<Status>() {
            @Override
            public void data(Status status) {
                super.data(status);
                if (status != null){
                    Utils.toast(getContext(), status.getText());
                    removeFromList(status.getId());
                }
            }
        });
    }

    /**
     * @param id User id
     */
    private void unmute(long id) {
        showAds();
        deleter.unMuteUser(id, new Listener<User>() {
            @Override
            public void data(User user) {
                super.data(user);
                if (user != null)
                    removeFromList(user.getId());
            }
        });
    }

    /**
     * @param id User id
     */
    private void unblock(long id){
        showAds();
        deleter.unBlockUser(id, new Listener<User>() {
            @Override
            public void data(User user) {
                super.data(user);
                if (user != null)
                    removeFromList(user.getId());
            }
        });
    }

    /**
     * Show ads after some action app doing
     */
    private boolean showAds() {
        // check premium version
        if (sharePref.getBoolean(SharePref.ProVersion, false))
            return false;

        // check last showed ads
        long lastAd = sharePref.getLong(SharePref.last_ad_show_time, 0);
        if ((new Date().getTime() - lastAd) >= Constant.showAdTime()) {
            AdManager.showInterstitialAds(getContext());
        }

        // check action counts
//        int actionCount = sharePref.getInt(SharePref.action_count_per_day, 0) + 1;
//        sharePref.putInt(SharePref.action_count_per_day, actionCount);
//        if (actionCount > Constant.freeActionCount()) {
//            Utils.toast(getContext(), R.string.upgrade_to_premium);
//            return false;
//        }
        return true;
    }

}
