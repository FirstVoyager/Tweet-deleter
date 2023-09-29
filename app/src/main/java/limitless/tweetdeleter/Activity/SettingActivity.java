package limitless.tweetdeleter.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

import limitless.tweetdeleter.Other.Constant;
import limitless.tweetdeleter.R;
import limitless.tweetdeleter.Utils.AdManager;
import limitless.tweetdeleter.Utils.SharePref;

public class SettingActivity extends BaseActivity  {

    private AppCompatEditText etApiKey, etApiSecret, etHost, etPort;
    private SharePref sharePref;
    private final CompoundButton.OnCheckedChangeListener proxyChangeCheck = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Toast.makeText(SettingActivity.this, "Update Setting", Toast.LENGTH_SHORT).show();
            sharePref.putBoolean(Constant.useProxy, isChecked);
            sharePref.putString(Constant.ProxyHost, Objects.requireNonNull(etHost.getText()).toString());
            sharePref.putString(Constant.ProxyPort, Objects.requireNonNull(etPort.getText()).toString());
        }
    };
    private TextWatcher textWatcherHost = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            sharePref.putString(Constant.ProxyHost, s.toString().trim().replace(" ", ""));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private final TextWatcher textWatcherPort = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            sharePref.putString(Constant.ProxyPort, s.toString().trim().replace(" ", ""));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public String getName() {
        return "SettingActivity";
    }

    @Override
    public boolean changeTheme() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        AdView adView = findViewById(R.id.ad_view);
        AdManager.loadAds(adView);
        init();
    }

    private void init() {
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        sharePref = new SharePref(this);
        MaterialTextView tvTheme = findViewById(R.id.textView_themeName);
        etHost = findViewById(R.id.editText_host_activitySetting);
        etPort = findViewById(R.id.editText_port_activitySetting);
        etApiKey = findViewById(R.id.editText_apiKey_activitySetting);
        etApiSecret = findViewById(R.id.editText_secret_activitySetting);
        SwitchCompat scProxy = findViewById(R.id.switchCompat_proxy_activitySetting);
        SwitchCompat scApi = findViewById(R.id.switchCompat_customApiKey_activitySetting);

        etHost.setText(sharePref.getString(Constant.ProxyHost, null));
        etHost.addTextChangedListener(textWatcherHost);
        etPort.setText(sharePref.getString(Constant.ProxyPort, null));
        etPort.addTextChangedListener(textWatcherPort);
        scProxy.setChecked(sharePref.getBoolean(Constant.useProxy, false));
        scProxy.setOnCheckedChangeListener(proxyChangeCheck);
        etApiKey.addTextChangedListener(textWatcherKey);
        etApiSecret.addTextChangedListener(textWatcherSecret);
        scApi.setOnCheckedChangeListener(listenerCustom);
        scApi.setChecked(sharePref.getBoolean(Constant.userCustomApiKey, false));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private final SwitchCompat.OnCheckedChangeListener listenerCustom = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            sharePref.putBoolean(Constant.userCustomApiKey, isChecked);
            if (isChecked){
                etApiKey.setEnabled(true);
                etApiSecret.setEnabled(true);
//                sharePref.putString(Constant.oAuthConsumerKey_pref);
            }else {
                etApiKey.setEnabled(false);
                etApiSecret.setEnabled(false);
                etApiKey.setText("");
                etApiSecret.setText("");
            }
        }
    };

    private final TextWatcher textWatcherKey = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private final TextWatcher textWatcherSecret = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}
