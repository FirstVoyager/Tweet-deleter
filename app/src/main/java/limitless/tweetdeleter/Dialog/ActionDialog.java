package limitless.tweetdeleter.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.checkbox.MaterialCheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import limitless.tweetdeleter.Other.Listener;
import limitless.tweetdeleter.R;
import limitless.tweetdeleter.Utils.AdManager;
import limitless.tweetdeleter.Utils.SharePref;
import limitless.tweetdeleter.Utils.Utils;
import limitless.tweetdeleter.databinding.DialogActionBinding;

public class ActionDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private DialogActionBinding binding;

    private String action, type;
    private int count;
    private Listener<Boolean> actionListener;
    private SharePref sharePref;
    private MaterialCheckBox.OnCheckedChangeListener onCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (! isChecked) {
                sharePref.putBoolean(SharePref.userFilter, false);
            }
            if (sharePref.getBoolean(SharePref.ProVersion, false)) {
                sharePref.putBoolean(SharePref.userFilter, isChecked);
            }else {
                Utils.toast(getContext(), R.string.please_active_pro_version);
            }
        }
    };

    public ActionDialog(@NonNull String action, String type, int count, Listener<Boolean> actionListener){
        this.action = action;
        this.type = type;
        this.count = count;
        this.actionListener = actionListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogActionBinding.inflate(inflater, container, false);
        init();
        getDialog().setContentView(binding.getRoot());
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    private void init() {
        sharePref = new SharePref(getContext());

        binding.checkboxFilter.setChecked(sharePref.getBoolean(SharePref.userFilter, false));
        binding.checkboxFilter.setOnCheckedChangeListener(onCheckedListener);
        binding.buttonCancel.setOnClickListener(this);
        binding.buttonAction.setOnClickListener(this);
        binding.textViewMsg.setText(getString(R.string.msg_delete, action, count, type));
        binding.buttonAction.setText(action);
        AdManager.loadAds(binding.adView);

        if (action.equals(getString(R.string.unmute))){
            binding.checkboxFilter.setVisibility(View.GONE);
        }else if (action.equals(getString(R.string.unblock))) {
            binding.checkboxFilter.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (actionListener != null){
            if (v.getId() == R.id.button_cancel){
                actionListener.data(false);
            }else if (v.getId() == R.id.button_action){
                actionListener.data(true);
            }
        }
        dismiss();
    }
}
