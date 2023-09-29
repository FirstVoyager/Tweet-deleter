package limitless.tweetdeleter.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import limitless.tweetdeleter.R;
import limitless.tweetdeleter.Utils.Utils;
import limitless.tweetdeleter.databinding.BottomSheetVoiceTweetBinding;

public class MoreAppsBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetVoiceTweetBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetVoiceTweetBinding.inflate(inflater, container, false);
        getDialog().setContentView(binding.getRoot());
        init();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void init() {
        binding.buttonVoice.setOnClickListener(v -> {
            Utils.openUrl(getContext(), getString(R.string.twitter_voice_url));
            dismiss();
        });
        binding.buttonStatusSaver.setOnClickListener(v -> {
            Utils.openUrl(getContext(), R.string.app_url_status_saver);
            dismiss();
        });
        binding.buttonUnfollowTwitter.setOnClickListener(v -> {
            Utils.openUrl(getContext(), R.string.ufollow_twitter_url);
            dismiss();
        });
    }
}
