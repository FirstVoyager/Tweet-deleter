package limitless.tweetdeleter.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.graphics.drawable.DrawableCompat;
import limitless.tweetdeleter.R;
import limitless.tweetdeleter.Utils.Utils;
import limitless.tweetdeleter.databinding.ActivityVideoPlayerBinding;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

public class VideoPlayerActivity extends BaseActivity implements View.OnClickListener {

    public static final String TWEET_URL = "tweet_url";

    private ActivityVideoPlayerBinding binding;
    private String videoUrl;
    private String tweetUrl = "";
    private int delayDuration = 200;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (binding.videoView.isPlaying()){
                binding.textViewCurrentTime.setText(Utils.convertLongToTime(binding.videoView.getCurrentPosition(), true, true, false));
                binding.progressbar.setProgress(binding.videoView.getCurrentPosition());
                handler.postDelayed(this, delayDuration);
            }
        }
    };

    @Override
    public String getName() {
        return "VideoPlayerActivity";
    }

    @Override
    public boolean changeTheme() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
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
        }else if (item.getTitle() != null && item.getTitle().equals(getString(R.string.share))) {
            Utils.shareText(this, videoUrl);
        }else if (item.getTitle() != null && item.getTitle().equals(getString(R.string.download))){
            download();
        }else if (item.getTitle() != null && item.getTitle().equals(getString(R.string.copy_link))){
            Utils.copyText(this, videoUrl);
            Utils.toast(this, getString(R.string.copied));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (binding.videoView.isPlaying()){
            binding.videoView.pause();
        }
    }

    private void init() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.imageButtonPlay.setOnClickListener(this);
        binding.progressbar.setOnSeekBarChangeListener(seekBarChangeListener);
        videoUrl = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        tweetUrl = getIntent().getStringExtra(TWEET_URL);
        if (videoUrl == null)
            finish();
        binding.videoView.setOnPreparedListener(onPreparedListener);
        binding.videoView.setOnErrorListener(onErrorListener);
        binding.videoView.setOnCompletionListener(onCompletionListener);
        binding.videoView.setVideoURI(Uri.parse(videoUrl));
        binding.videoView.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageButton_play){
                play();
        }
    }

    private void play() {
        if (binding.videoView.isPlaying()){
            binding.imageButtonPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            binding.videoView.pause();
        }else {
            binding.imageButtonPlay.setImageResource(R.drawable.ic_pause_black_24dp);
            binding.videoView.start();
            handler.postDelayed(runnable, delayDuration);
        }
    }

    private void download() {
        Intent intent = getPackageManager().getLaunchIntentForPackage(getString(R.string.package_media_downloader));
        if (intent == null)
            Utils.openUrl(this, Utils.googlePlayUrl(getString(R.string.package_media_downloader)));
        else {
            intent.putExtra(Intent.EXTRA_TEXT, tweetUrl);
            Utils.startActivity(this, intent);
        }
    }

    private MediaPlayer.OnPreparedListener onPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            binding.progressbar.setMax(mp.getDuration());
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.textViewDuration.setText(Utils.convertLongToTime(mp.getDuration(), true, true, false));
            handler.postDelayed(runnable, delayDuration);
        }
    };
    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            String s = null;
            switch (what){
                case MediaPlayer.MEDIA_ERROR_IO:
                    s = "File or network related operation errors";
                    break;
                case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                    s = "Some operation takes too long to complete";
                    break;
                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                case MediaPlayer.MEDIA_ERROR_MALFORMED:
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                    s = "Unspecified media player error";
                    break;
                case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                    s = "The media framework does not support the feature";
                    break;
            }
            Utils.toast(VideoPlayerActivity.this, s);
            return false;
        }
    };
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            binding.imageButtonPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            binding.progressbar.setProgress(0);
            binding.textViewCurrentTime.setText("00:00");
        }
    };
    private AppCompatSeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                binding.videoView.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

}
