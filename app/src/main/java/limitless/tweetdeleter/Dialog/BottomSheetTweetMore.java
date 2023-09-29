package limitless.tweetdeleter.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import limitless.tweetdeleter.Other.Listener;
import limitless.tweetdeleter.R;
import limitless.tweetdeleter.databinding.BottomSheetTweetMoreBinding;

public class BottomSheetTweetMore extends BottomSheetDialogFragment implements View.OnClickListener {

    private BottomSheetTweetMoreBinding binding;
    private BottomSheetBehavior bottomSheetBehavior;
    private Listener<String> listener;
    private boolean isLike;

    public BottomSheetTweetMore(boolean isLike, Listener<String> listener){
        this.isLike = isLike;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetTweetMoreBinding.inflate(inflater, container, false);
        getDialog().setContentView(binding.getRoot());
        init();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void init() {
        binding.buttonAddToWhiteList.setOnClickListener(this);
        binding.buttonCopyLink.setOnClickListener(this);
        binding.buttonShareTweetVia.setOnClickListener(this);
        binding.buttonCopyTweetText.setOnClickListener(this);
        binding.buttonShowInTwitter.setOnClickListener(this);
        if (isLike){
            binding.buttonUnlike.setOnClickListener(this);
            binding.buttonDelete.setVisibility(View.GONE);
        }else {
            binding.buttonUnlike.setVisibility(View.GONE);
            binding.buttonDelete.setOnClickListener(this);
        }

        bottomSheetBehavior = BottomSheetBehavior.from((View) binding.getRoot().getParent());
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);
    }

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN){
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

    @Override
    public void onClick(View v) {
        if (listener == null){
            dismiss();
            return;
        }
        switch (v.getId()){
            case R.id.button_addToWhiteList:
                listener.data(getString(R.string.add_to_white_list));
                break;
            case R.id.button_copyLink:
                listener.data(getString(R.string.copy_link));
                break;
            case R.id.button_share:
                listener.data(getString(R.string.share_tweet_via));
                break;
            case R.id.button_showInTwitter:
                listener.data(getString(R.string.show_in_twitter));
                break;
            case R.id.button_copyTweetText:
                listener.data(getString(R.string.copy_the_tweet_text));
                break;
            case R.id.button_delete:
                listener.data(getString(R.string.delete));
                break;
            case R.id.button_unlike:
                listener.data(getString(R.string.unlike));
                break;
        }
        dismiss();
    }
}
