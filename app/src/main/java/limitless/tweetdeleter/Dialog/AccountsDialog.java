package limitless.tweetdeleter.Dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import limitless.tweetdeleter.Activity.LoginActivity;
import limitless.tweetdeleter.Activity.ProActivity;
import limitless.tweetdeleter.Adapter.AccountAdapter;
import limitless.tweetdeleter.Other.Listener;
import limitless.tweetdeleter.Other.Model.Account;
import limitless.tweetdeleter.R;
import limitless.tweetdeleter.Utils.Deleter;
import limitless.tweetdeleter.Utils.SQLiteDeleter;
import limitless.tweetdeleter.Utils.SharePref;
import limitless.tweetdeleter.Utils.Utils;
import limitless.tweetdeleter.databinding.DialogAccountsBinding;
import twitter4j.TwitterException;
import twitter4j.User;

public class AccountsDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    public static String LOGIN_ACCOUNT = "loginAccount";
    private static int loginCode = 201;

    private DialogAccountsBinding binding;
    private SQLiteDeleter sqlite;
    private AccountAdapter accountAdapter;
    private Listener<Account> listener;

    public AccountsDialog(@NonNull Listener<Account> listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAccountsBinding.inflate(inflater, container, false);
        getDialog().setContentView(binding.getRoot());
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().getAttributes().width = -2;
        init();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void init() {
        sqlite = new SQLiteDeleter(getContext());
        accountAdapter = new AccountAdapter(getContext(), sqlite.getAccounts(), accountListener);

        binding.buttonAdd.setOnClickListener(this);
        binding.recyclerView.setAdapter(accountAdapter);
        binding.textViewGetRealFollowers.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.textView_getRealFollowers){
            new Deleter(getContext()).followUser("unfollowTiTa", new Listener<User>() {
                @Override
                public void data(User user) {
                    super.data(user);
                    if (user != null)
                        Utils.toast(getContext(), R.string.thank_you);
                }
            });
        }else if (v.getId() == R.id.button_add){
            if (new SharePref(getContext()).getBoolean(SharePref.ProVersion, false)) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivityForResult(intent, loginCode);
            } else {
                Utils.startActivity(getContext(), ProActivity.class);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == loginCode){
            if (resultCode == Activity.RESULT_CANCELED || data == null){
                Utils.toast(getContext(), R.string.login_failed);
                dismiss();
            }else if (resultCode == Activity.RESULT_OK){
                Account account = data.getParcelableExtra(LOGIN_ACCOUNT);
                if (account == null)
                    return;
                if (sqlite.checkAccount(account.id)){
                    Utils.toast(getContext(), R.string.account_exist);
                }else {
                    sqlite.insertAccount(account);
                    sqlite.setMainAccount(account.id);
                    if (listener != null){
                        listener.data(account);
                    }
                    dismiss();
                }
            }
        }
    }

    private Listener<Account> accountListener = new Listener<Account>() {
        @Override
        public void data(Account account) {
            sqlite.setMainAccount(account.id);
            listener.data(account);
            dismiss();
        }
    };

}


