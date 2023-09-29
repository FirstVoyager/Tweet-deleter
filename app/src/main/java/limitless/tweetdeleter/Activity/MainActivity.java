package limitless.tweetdeleter.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import limitless.tweetdeleter.Dialog.AccountsDialog;
import limitless.tweetdeleter.Dialog.FilterDialog;
import limitless.tweetdeleter.Dialog.MoreAppsBottomSheet;
import limitless.tweetdeleter.Fragment.BlocksFragment;
import limitless.tweetdeleter.Fragment.LikesFragment;
import limitless.tweetdeleter.Fragment.HomeFragment;
import limitless.tweetdeleter.Fragment.MutesFragment;
import limitless.tweetdeleter.Fragment.RepliesFragment;
import limitless.tweetdeleter.Fragment.ReTweetFragment;
import limitless.tweetdeleter.Fragment.TweetFragment;
import limitless.tweetdeleter.Other.Listener;
import limitless.tweetdeleter.Other.Model.Account;
import limitless.tweetdeleter.R;
import limitless.tweetdeleter.Utils.AdManager;
import limitless.tweetdeleter.Utils.Deleter;
import limitless.tweetdeleter.Utils.SQLiteDeleter;
import limitless.tweetdeleter.Utils.Utils;
import limitless.tweetdeleter.Utils.SharePref;
import limitless.tweetdeleter.databinding.ActivityMainBinding;
import limitless.tweetdeleter.databinding.HeaderMainBinding;
import twitter4j.User;

public class MainActivity extends BaseActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, MenuItem.OnMenuItemClickListener {

    private static final int loginResult = 1001;
    private static final int settingCode = 201;

    private ActivityMainBinding binding;
    private HeaderMainBinding header;
    private SharePref sharePref;
    private Deleter deleter;
    private int lastFragment = 0;
    private Fragment tweetFragment, retweetFragment, mentionFragment, likeFragment
            , muteFragment, mainFragment, blockedFragment;
    private Account mAccount;
    private SQLiteDeleter sqlite;


    @Override
    public String getName() {
        return "MainActivity";
    }

    @Override
    public boolean changeTheme() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        header = HeaderMainBinding.bind(binding.navigationView.getHeaderView(0));
        loadBanner(binding.adView);
        sharePref = new SharePref(this);
        header.cardViewAccount.setOnClickListener(this);
        sqlite = new SQLiteDeleter(this);
        binding.textViewPremium.setOnClickListener(this);
        initToolbar();

        if (sqlite.accountCount() <= 0) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, loginResult);
        } else {
            setup(sqlite.getMainAccount());

            if (! sharePref.getBoolean(SharePref.ProVersion, false)) {
                InterstitialAd interstitialAd = new InterstitialAd(this);
                interstitialAd.setAdUnitId(getString(R.string.ad_id_interstitial));
                interstitialAd.loadAd(new AdRequest.Builder().build());
                interstitialAd.setAdListener(new AdListener(){
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(interstitialAd::show, 3500);
                    }
                });

            }else {
                binding.textViewPremium.setVisibility(View.GONE);
            }
        }
    }


    private void setup(Account aa) {
        try {
            if (mainFragment != null){
                getSupportFragmentManager().beginTransaction().remove(mainFragment).commit();
                mainFragment = null;
            }
            if (tweetFragment != null){
                getSupportFragmentManager().beginTransaction().remove(tweetFragment).commit();
                tweetFragment = null;
            }
            if (retweetFragment != null){
                getSupportFragmentManager().beginTransaction().remove(retweetFragment).commit();
                retweetFragment = null;
            }
            if (mentionFragment != null){
                getSupportFragmentManager().beginTransaction().remove(mentionFragment).commit();
                mentionFragment = null;
            }
            if (likeFragment != null){
                getSupportFragmentManager().beginTransaction().remove(likeFragment).commit();
                likeFragment = null;
            }
            if (muteFragment != null){
                getSupportFragmentManager().beginTransaction().remove(muteFragment).commit();
                muteFragment = null;
            }
            if (blockedFragment != null){
                getSupportFragmentManager().beginTransaction().remove(blockedFragment).commit();
                blockedFragment = null;
            }
            lastFragment = 0;
        }catch (Exception e){
            Utils.error(e);
        }
        mainFragment = new HomeFragment();
        tweetFragment = new TweetFragment();
        retweetFragment = new ReTweetFragment();
        mentionFragment = new RepliesFragment();
        likeFragment = new LikesFragment();
        muteFragment = new MutesFragment();
        blockedFragment = new BlocksFragment();
        mAccount = aa;
        deleter = new Deleter(this);

        showUser();
        setTitle(getString(R.string.app_name));
        binding.navigationView.setCheckedItem(R.id.nav_main);
        setFragment(mainFragment, R.id.nav_main);
        if (sharePref.getBoolean(SharePref.showDeleteMessage, true)){
            Utils.showAlertDialogMessage(this, sharePref);
        }

    }

    private void showUser() {
        try {
            Utils.loadImage(this, mAccount.profileUrl, header.imageViewAvatar, true);
            header.textViewName.setText(mAccount.name);
            header.textViewScreen.setText("@");
            header.textViewScreen.append(mAccount.screenName);
            deleter.showUser(mAccount.id, new Listener<User>() {
                @Override
                public void data(User user) {
                    super.data(user);
                    if (user != null){
                        Utils.loadImage(MainActivity.this, user.getBiggerProfileImageURL(), header.imageViewAvatar, true);
                        header.textViewName.setText(user.getName());
                        header.textViewScreen.setText("@");
                        header.textViewScreen.append(user.getScreenName());
                    }
                }

                @Override
                public void error(Exception e) {
                    super.error(e);
                    Utils.toast(MainActivity.this, R.string.cant_load_user);
                }
            });
        }catch (Exception e){
            Utils.error(e);
        }
    }

    private void initToolbar() {
        setSupportActionBar(binding.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawer, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawer.addDrawerListener(toggle);
        toggle.syncState();
        binding.navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem filter = menu.add(getString(R.string.t_filter));
        filter.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(getString(R.string.logout));
        menu.add(getString(R.string.share));
        menu.add(R.string.rate);
        menu.add(getString(R.string.t_app_message));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getTitle() != null && item.getTitle().equals(getString(R.string.t_filter))){
            FilterDialog dialog = new FilterDialog();
            dialog.show(getSupportFragmentManager(), null);
        }else if (item.getTitle() != null && item.getTitle().equals(getString(R.string.logout))){
            logOut();
        }else if (item.getTitle() != null && item.getTitle().equals(getString(R.string.t_app_message))){
            Utils.showAlertDialogMessage(this, sharePref);
        }else if (item.getTitle() != null && item.getTitle().equals(getString(R.string.rate))){
            Utils.openUrl(MainActivity.this, getString(R.string.url_app));
        }else if (item.getTitle() != null && item.getTitle().equals(getString(R.string.share))){
            Utils.shareText(this, getString(R.string.url_app));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case loginResult:
                if (data != null) {
                    Account a = data.getParcelableExtra(AccountsDialog.LOGIN_ACCOUNT);
                    if (a != null && resultCode == RESULT_OK){
                        sqlite.insertAccount(a);
                        sqlite.setMainAccount(a.id);
                        AdManager.showInterstitialAds(this, true);
                        setup(a);
                    }else {
                        Utils.toast(this, getString(R.string.login_failed));
                        finish();
                    }
                }else {
                    Utils.toast(this, getString(R.string.login_failed));
                    finish();
                }
                break;
            case settingCode:
                if (sharePref.getBoolean(SharePref.changeTheme, false)){
                    sharePref.putBoolean(SharePref.changeTheme, false);
                    Intent intent = getIntent();
                    finish();
                    Utils.startActivity(this, intent);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cardView_account){
            binding.drawer.closeDrawer(GravityCompat.START);
            AccountsDialog dialog = new AccountsDialog(new Listener<Account>() {
                @Override
                public void data(Account account) {
                    super.data(account);
                    if (account.id != mAccount.id){
                        setup(account);
                    }
                    binding.drawer.closeDrawer(GravityCompat.START);
                }
            });
            dialog.show(getSupportFragmentManager(), null);
        }else if (v.getId() == R.id.textView_premium){
            Utils.startActivity(this, ProActivity.class);
        }
    }

    private void logOut() {
        if (mAccount == null)
            return;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want to logout from @" + mAccount.screenName + "(" + mAccount.name + ") ?");
        builder.setTitle(R.string.logout);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_exit_to_app_black_24dp, null);
        assert drawable != null;
        DrawableCompat.setTint(drawable, Color.BLACK);
        builder.setIcon(drawable);
        builder.setPositiveButton(R.string.logout, (dialog, which) -> {
            dialog.dismiss();
            if (sqlite.deleteAccount(mAccount.id)){
                Utils.toast(MainActivity.this, R.string.logout);
                if (sqlite.accountCount() <= 0){
                    finish();
                }else {
                    sqlite.setMainAccount(sqlite.getAccounts().get(0).id);
                    setup(sqlite.getMainAccount());
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void setFragment(Fragment fragment, int id) {
        if (id == lastFragment)
            return;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragment.isAdded()){
            ft.show(fragment);
        }else {
            ft.add(R.id.frameLayout, fragment);
        }
        if (lastFragment == R.id.nav_tweet) {
            hideFragment(tweetFragment);
        } else if (lastFragment == R.id.nav_retweets) {
            hideFragment(retweetFragment);
        } else if (lastFragment == R.id.nav_replies) {
            hideFragment(mentionFragment);
        } else if (lastFragment == R.id.nav_favorite) {
            hideFragment(likeFragment);
        } else if (lastFragment == R.id.nav_muter) {
            hideFragment(muteFragment);
        } else if (lastFragment == R.id.nav_blocked) {
            hideFragment(blockedFragment);
        }
        lastFragment = id;
        try {
            ft.commit();
        } catch (Exception ignored) {
            ft.commitNowAllowingStateLoss();
        }
    }

    private void hideFragment(Fragment fragment) {
        if (fragment.isAdded()){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(fragment);
            transaction.commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        binding.drawer.closeDrawer(GravityCompat.START);
        int id = item.getItemId();
        if (id == R.id.nav_main) {
            setTitle(getString(R.string.app_name));
            setFragment(mainFragment, id);
        } else if (id == R.id.nav_tweet) {
            setTitle(getString(R.string.text_tweets));
            setFragment(tweetFragment, id);
        } else if (id == R.id.nav_retweets) {
            setTitle(getString(R.string.text_retweets));
            setFragment(retweetFragment, id);
        } else if (id == R.id.nav_replies) {
            setTitle(getString(R.string.t_replies));
            setFragment(mentionFragment, id);
        } else if (id == R.id.nav_favorite) {
            setTitle(getString(R.string.text_likes));
            setFragment(likeFragment, id);
        } else if (id == R.id.nav_muter) {
            setTitle(getString(R.string.text_mutes));
            setFragment(muteFragment, id);
        } else if (id == R.id.nav_blocked) {
            setTitle(getString(R.string.blocked));
            setFragment(blockedFragment, id);
        } else if (id == R.id.nav_follow_us) {
            followUs();
        } else if (id == R.id.nav_setting) {
            startActivityForResult(new Intent(this, SettingActivity.class), settingCode);
        } else if (id == R.id.nav_status_downloader || id == R.id.nav_unfollowers_twitter || id == R.id.nav_twitter_voice) {
            new MoreAppsBottomSheet().show(getSupportFragmentManager(), null);
        }
        return true;
    }

    private void followUs() {
        deleter.followUser("unfollowTiTa", new Listener<User>() {
            @Override
            public void data(User user) {
                super.data(user);
                if (user != null)
                    Utils.toast(MainActivity.this, getString(R.string.thank_you));
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.text_upgrade))){
            startActivity(new Intent(this, ProActivity.class));
        }
        return false;
    }
}
