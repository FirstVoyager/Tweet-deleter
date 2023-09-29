package limitless.tweetdeleter.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import limitless.tweetdeleter.Other.Listener;
import limitless.tweetdeleter.Other.Model.Account;
import limitless.tweetdeleter.R;
import limitless.tweetdeleter.Utils.Utils;
import limitless.tweetdeleter.databinding.ItemAccountBinding;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private Context context;
    private List<Account> accounts;
    private Listener<Account> accountListener;

    public AccountAdapter(Context context, List<Account> accounts, Listener<Account> accountListener) {
        this.context = context;
        this.accounts = accounts;
        this.accountListener = accountListener;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AccountViewHolder(LayoutInflater.from(context).inflate(R.layout.item_account, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        holder.bindView(accounts.get(position));
    }

    @Override
    public int getItemCount() {
        try {
            return accounts.size();
        }catch (Exception e){
            Utils.error(e);
            return 0;
        }
    }

    public void setData(List<Account> accounts){
        if (accounts == null)
            return;
        this.accounts = accounts;
        notifyDataSetChanged();
    }

    class AccountViewHolder extends RecyclerView.ViewHolder {
        private ItemAccountBinding binding;
        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemAccountBinding.bind(itemView);
            binding.main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (accountListener != null){
                        accountListener.data(accounts.get(getAdapterPosition()));
                    }
                }
            });
        }

        void bindView(Account account){
            Utils.loadImage(context, account.profileUrl, binding.imageViewAvatar, true);
            binding.textViewName.setText(account.name);
            binding.textViewScreenName.setText("@");
            binding.textViewScreenName.append(account.screenName);
            if (account.isMain)
                binding.viewMain.setVisibility(View.VISIBLE);
            else
                binding.viewMain.setVisibility(View.GONE);
        }

    }

}
