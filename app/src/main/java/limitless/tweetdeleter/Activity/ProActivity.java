package limitless.tweetdeleter.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import limitless.tweetdeleter.ApplicationLoader;
import limitless.tweetdeleter.Other.Constant;
import limitless.tweetdeleter.R;
import limitless.tweetdeleter.Utils.SharePref;
import limitless.tweetdeleter.Utils.Utils;
import limitless.tweetdeleter.databinding.ActivityProBinding;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import static limitless.tweetdeleter.ApplicationLoader.PRO_VERSION_PRODUCT_ID;

public class ProActivity extends BaseActivity implements View.OnClickListener, BillingProcessor.IBillingHandler {

    // billing 3
    private BillingProcessor bp;
    private ActivityProBinding binding;

    @Override
    public String getName() {
        return "ProActivity";
    }

    @Override
    public boolean changeTheme() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        bp = new BillingProcessor(this, getString(R.string.licens_key), this);
        bp.initialize();

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.imageButtonBuyGold.setOnClickListener(this);
        binding.buttonRestore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageButton_buyGold) {
            bp.subscribe(this, PRO_VERSION_PRODUCT_ID);
        }else if (v.getId() == R.id.button_restore) {
            if (bp.loadOwnedPurchasesFromGoogle() && bp.isSubscribed(PRO_VERSION_PRODUCT_ID)) {
                new SharePref(this).putBoolean(SharePref.ProVersion, true);
                Utils.toast(this, R.string.successfully_restored);
            }else {
                new SharePref(this).putBoolean(SharePref.ProVersion, false);
                Utils.toast(this, R.string.error);
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        new SharePref(this).putBoolean(SharePref.ProVersion, true);
        Utils.toast(this, R.string.re_open_app);
    }

    @Override
    public void onPurchaseHistoryRestored() {
        new SharePref(this).putBoolean(SharePref.ProVersion, true);
        Utils.toast(this, R.string.re_open_app);
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Utils.toast(this, errorCode + " : ");
        if (error != null)
            Utils.toast(this, R.string.error);
    }

    @Override
    public void onBillingInitialized() {

    }
}
