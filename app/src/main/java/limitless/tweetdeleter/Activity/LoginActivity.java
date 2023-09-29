package limitless.tweetdeleter.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Random;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import limitless.tweetdeleter.Dialog.AccountsDialog;
import limitless.tweetdeleter.Other.Constant;
import limitless.tweetdeleter.Other.Listener;
import limitless.tweetdeleter.Other.Model.Account;
import limitless.tweetdeleter.R;
import limitless.tweetdeleter.Utils.Deleter;
import limitless.tweetdeleter.Utils.Utils;
import limitless.tweetdeleter.Utils.SharePref;
import limitless.tweetdeleter.databinding.ActivityLoginBinding;
import twitter4j.auth.RequestToken;

public class LoginActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ActivityLoginBinding binding;
    private SharePref sharePref;
    private String autherticalUrl;
    private Deleter deleter;
    private RequestToken requestToken;
    private String key, secret;

    private WebChromeClient webChromeClient = new WebChromeClient(){
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            binding.progressbar.setProgress(newProgress);
            if (newProgress >= 100){
                binding.progressbar.setVisibility(View.GONE);
            }else {
                binding.progressbar.setVisibility(View.VISIBLE);
            }
        }
    };
    private WebViewClient webViewClient = new WebViewClient(){
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                if (url.contains(Constant.twitter_back_url)){
                    Uri uri = Uri.parse(url);
                    String oauth_verifier = uri.getQueryParameter(Constant.oauth_verifier);
                    sharePref.putString(Constant.oauth_verifier, oauth_verifier);
                    deleter.saveTwitterLogin(
                            sharePref.getString(Constant.oauth_verifier, ""),
                            requestToken, new Listener<Account>() {
                                @Override
                                public void data(Account account) {
                                    super.data(account);
                                    if (account == null){
                                        setResult(RESULT_CANCELED);
                                    }else {
                                        account.consumerKey = key;
                                        account.consumerSecret = secret;
                                        Intent intent = new Intent();
                                        intent.putExtra(AccountsDialog.LOGIN_ACCOUNT, account);
                                        setResult(RESULT_OK, intent);
                                    }
                                    finish();
                                }
                            }
                    );
                    return false;
                }
            }catch (Exception e){
                Utils.error(e);
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

    };
    private DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            binding.progressbar.setVisibility(View.GONE);
        }
    };

    @Override
    public String getName() {
        return "LoginActivity";
    }

    @Override
    public boolean changeTheme() {
        return true;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.removeCookie();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharePref = new SharePref(this);

        setSupportActionBar(binding.appbarLayout.toolbar);
        binding.webView.setWebViewClient(webViewClient);
        binding.webView.setDownloadListener(downloadListener);
        binding.webView.setWebChromeClient(webChromeClient);
        binding.webView.getSettings().setJavaScriptEnabled(true);
        getData();
    }

    private void getData() {
        binding.progressbar.setProgress(0);

        key = Constant.oAuthConsumerKey;
        secret = Constant.oAuthConsumerSecret;

        deleter = new Deleter(this, new Listener<RequestToken>() {
            @Override
            public void data(RequestToken request) {
                super.data(request);
                binding.progressbar.setVisibility(View.VISIBLE);
                if (request == null){
                    Utils.toast(LoginActivity.this, R.string.check_your_connection);
                    binding.webView.loadUrl("file:///android_asset/error.html");
                    return;
                }
                requestToken = request;
                autherticalUrl = request.getAuthenticationURL();
                init();
            }
        }, key, secret);
    }

    private void init() {
        if (Utils.isOnline(this)){
            binding.webView.loadUrl(autherticalUrl);
        }else {
            Utils.showAlertDialogNoInternet(this, new Listener<Void>() {
                @Override
                public void data(Void aVoid) {
                    super.data(aVoid);
                    binding.webView.loadUrl(autherticalUrl);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem refresh = menu.add(R.string.refresh);
        refresh.setIcon(R.drawable.ic_refresh_white_24dp);
        refresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(R.string.exit);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle() == null && item.getItemId() == android.R.id.home){
            finish();
        }else if (item.getTitle() != null && item.getTitle().equals(getString(R.string.refresh))){
            binding.progressbar.setVisibility(View.VISIBLE);
            binding.progressbar.setProgress(0);
            getData();
        }else if (item.getTitle() != null && item.getTitle().equals(getString(R.string.exit))){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (binding.webView.canGoBack()){
            binding.webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onRefresh() {
        getData();
    }
}
