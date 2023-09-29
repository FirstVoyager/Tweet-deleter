package limitless.tweetdeleter.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import limitless.tweetdeleter.Other.Listener;
import limitless.tweetdeleter.Other.Model.TextFilterModel;
import limitless.tweetdeleter.Other.Model.UserFilterModel;
import limitless.tweetdeleter.R;
import limitless.tweetdeleter.Utils.Deleter;
import limitless.tweetdeleter.Utils.SQLiteDeleter;
import limitless.tweetdeleter.Utils.SharePref;
import limitless.tweetdeleter.Utils.Utils;
import limitless.tweetdeleter.databinding.DialogFilterBinding;
import twitter4j.TwitterException;
import twitter4j.User;


public class FilterDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private DialogFilterBinding binding;
    private SharePref sharePref;
    private Deleter deleter;
    private SQLiteDeleter sqLiteDeleter;
    private List<TextFilterModel> textFilters;
    private List<UserFilterModel> userFilters;
    private AppCompatSeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                sharePref.putInt(SharePref.delayDelete, progress);
                binding.textViewDelayDelete.setText(progress + " s");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogFilterBinding.inflate(inflater, container, false);
        getDialog().setContentView(binding.getRoot());
        init();
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    private void init() {
        sharePref = new SharePref(getContext());
        deleter = new Deleter(getContext());
        sqLiteDeleter = new SQLiteDeleter(getContext());

        binding.textViewDelayDelete.setText(sharePref.getInt(SharePref.delayDelete, 1) + " s");
        binding.seekBarDelay.setProgress(sharePref.getInt(SharePref.delayDelete, 1));
        binding.seekBarDelay.setOnSeekBarChangeListener(seekBarChangeListener);
        binding.imageButtonAddText.setOnClickListener(this);
        binding.imageButtonAddUser.setOnClickListener(this);
        binding.buttonClose.setOnClickListener(this);

        // get text filters
        textFilters = sqLiteDeleter.getTexts();
        if (textFilters != null){
            for (TextFilterModel tfm : textFilters) {
                addTextFilter(tfm.text, tfm.id);
            }
        }else {
            textFilters = new ArrayList<>();
        }
        // get user filters
        userFilters = sqLiteDeleter.getUsers();
        if (userFilters != null){
            for (UserFilterModel ufm : userFilters) {
                addUserFilter(ufm.name, ufm.id);
            }
        }else {
            userFilters = new ArrayList<>();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_close) {
            dismiss();
        }else if (v.getId() == R.id.imageButton_addText){
            addText();
        }else if (v.getId() == R.id.imageButton_addUser){
            addUser();
        }
    }

    private void addText() {
        if (Utils.isEmpty(binding.editTextText.getText().toString())){
            binding.editTextText.setError(getString(R.string.t_empty));
        }else {
            TextFilterModel model = sqLiteDeleter.putTextFilter(binding.editTextText.getText().toString().trim());
            if (model != null){
                addTextFilter(model.text, model.id);
                textFilters.add(model);
            }
            binding.editTextText.setText(null);
        }
    }

    private void addUser() {
        if (Utils.isEmpty(binding.editTextUser.getText().toString())){
            binding.editTextUser.setError(getString(R.string.t_empty));
        }else {
            binding.progressBarUsers.setVisibility(View.VISIBLE);
            binding.imageButtonAddUser.setVisibility(View.INVISIBLE);
            deleter.showUser(binding.editTextUser.getText().toString().trim(), new Listener<User>() {
                @Override
                public void data(User user) {
                    super.data(user);
                    binding.progressBarUsers.setVisibility(View.INVISIBLE);
                    binding.imageButtonAddUser.setVisibility(View.VISIBLE);
                    if (user != null){
                        UserFilterModel model = sqLiteDeleter.putUser(new UserFilterModel(
                                -1,
                                user.getName(),
                                user.getScreenName(),
                                user.getId()
                        ));
                        if (model != null){
                            addUserFilter(user.getName(), model.id);
                            userFilters.add(model);
                            binding.editTextUser.setText(null);
                        }
                    }
                }

                @Override
                public void error(Exception e) {
                    super.error(e);
                    if (e != null)
                        Utils.toast(getContext(), e.getMessage());
                    binding.progressBarUsers.setVisibility(View.INVISIBLE);
                    binding.imageButtonAddUser.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void addUserFilter(String name, int id) {
        Chip chip = new Chip(getContext());
        chip.setText(name);
        chip.setId(id);
        chip.setOnClickListener(chipListenerUser);
        binding.chipGroupUsers.addView(chip);
    }

    private void addTextFilter(String name, int id) {
        Chip chip = new Chip(getContext());
        chip.setText(name);
        chip.setId(id);
        chip.setOnClickListener(chipListenerText);
        binding.chipGroupText.addView(chip);
    }

    private View.OnClickListener chipListenerText = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int item = -1;
            for (int i = 0; i < textFilters.size(); i++) {
                if (textFilters.get(i).id == v.getId()){
                    item = i;
                    break;
                }
            }
            if (item >= 0){
                sqLiteDeleter.deleteTextFilter(textFilters.get(item).text);
                binding.chipGroupText.removeViewAt(item);
                textFilters.remove(item);
            }
        }
    };
    private View.OnClickListener chipListenerUser = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int item = -1;
            for (int i = 0; i < userFilters.size(); i++) {
                if (userFilters.get(i).id == v.getId()){
                    item = i;
                    break;
                }
            }
            if (item >= 0){
                sqLiteDeleter.deleteUser(userFilters.get(item).idLong);
                binding.chipGroupUsers.removeViewAt(item);
                userFilters.remove(item);
            }
        }
    };

}
