package limitless.tweetdeleter.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import limitless.tweetdeleter.R;
import limitless.tweetdeleter.Utils.Utils;
import limitless.tweetdeleter.databinding.ActivityImageBinding;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

public class ImageActivity extends BaseActivity {

    private ActivityImageBinding binding;
    private String url;
    private String tweet_url;

    @Override
    public String getName() {
        return "ImageActivity";
    }

    @Override
    public boolean changeTheme() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        url = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        tweet_url = getIntent().getStringExtra(VideoPlayerActivity.TWEET_URL);
        if (url == null){
            finish();
            return;
        }
        Glide.with(this).load(url).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                binding.photoView.setImageDrawable(resource);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem download = menu.add(getString(R.string.download));
        download.setIcon(R.drawable.ic_file_download_black_24dp);
        download.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        DrawableCompat.setTint(download.getIcon(), Color.WHITE);

        menu.add(getString(R.string.share));
        menu.add(getString(R.string.copy_link));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getTitle() == null && item.getItemId() == android.R.id.home){
            finish();
        }else if (item.getTitle() != null && item.getTitle().equals(getString(R.string.share))){
            Utils.shareText(this, url);
        }else if (item.getTitle() != null && item.getTitle().equals(getString(R.string.download))){
            Intent intent = getPackageManager().getLaunchIntentForPackage(getString(R.string.package_media_downloader));
            if (intent == null)
                Utils.openUrl(this, Utils.googlePlayUrl(getString(R.string.package_media_downloader)));
            else {
                intent.putExtra(Intent.EXTRA_TEXT, tweet_url);
                Utils.startActivity(this, intent);
            }
        }else if (item.getTitle() != null && item.getTitle().equals(getString(R.string.copy_link))){
            Utils.copyText(this, url);
            Utils.toast(this, getString(R.string.copied));
        }
        return super.onOptionsItemSelected(item);
    }
}
