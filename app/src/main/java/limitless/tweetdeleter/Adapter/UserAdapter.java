package limitless.tweetdeleter.Adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import limitless.tweetdeleter.Other.Listener;
import limitless.tweetdeleter.Other.Model.ModelSelect;
import limitless.tweetdeleter.R;
import limitless.tweetdeleter.Utils.Deleter;
import limitless.tweetdeleter.Utils.Utils;
import twitter4j.TwitterException;
import twitter4j.User;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<User> userList;
    private List<ModelSelect> modelSelectList;
    private Deleter deleter;
    private Listener<Void> onEndSrollListener;
    private boolean isBlock;

    public UserAdapter(Context context, List<User> userList, boolean isBlock) {
        this.context = context;
        this.userList = userList;
        this.deleter = new Deleter(context);
        this.isBlock = isBlock;
        for (int i = 0; i < userList.size(); i++) {
            modelSelectList.add(new ModelSelect(false));
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new UserViewHolder(LayoutInflater.from(context).inflate(R.layout.item_user, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((UserViewHolder) holder).bindView(userList.get(position));
    }


    @Override
    public int getItemCount() {
        try {
            return userList.size();
        }catch (Exception e){
            Utils.error(e);
            return 0;
        }
    }

    public void deleteUser(long id) {
        try {
            int n = 0;
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).getId() == id){
                    n = i;
                }
            }
            if (n != 0){
                modelSelectList.remove(n);
                userList.remove(n);
                notifyItemRemoved(n);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getSize() {
        try {
            return userList.size();
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public void insertNewData(List<User> users){
        if (users == null)
            return;
        List<ModelSelect> newS = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            newS.add(new ModelSelect(false));
        }
        if (modelSelectList == null){
            modelSelectList = newS;
        }else {
            modelSelectList.addAll(newS);
        }
        userList.addAll(users);
        notifyDataSetChanged();
    }

    public List<User> getAllUsers(){
        return userList;
    }

    public void endListener(Listener<Void> listener){
        this.onEndSrollListener = listener;
    }

    public void actionClick(long id) {
        Listener<User> userListener = new Listener<User>() {
            @Override
            public void data(User user) {
                super.data(user);
                if (user != null) {
                    deleteUser(user.getId());
                    String s;
                    if (isBlock)
                        s = user.getScreenName() + " unblock";
                    else
                        s = user.getScreenName() + " unmuted";
                    Utils.toast(context, s);
                }
            }

            @Override
            public void error(Exception e) {
                super.error(e);
                if (e != null) {
                    Utils.toast(context, e.getMessage());
                }
            }
        };
        if (isBlock){
            deleter.unBlockUser(id, userListener);
        }else {
            deleter.unMuteUser(id, userListener);
        }
    }

    class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MaterialButton btnAction;
        private View cardView;
        private ImageView ivAvatar;
        private MaterialTextView tvName, tvScreenName;
        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            btnAction = itemView.findViewById(R.id.textView_unMute);
            cardView = itemView;
            ivAvatar = itemView.findViewById(R.id.imageView_avatar_recyclerUser);
            tvName = itemView.findViewById(R.id.textView_name_recyclerUser);
            tvScreenName = itemView.findViewById(R.id.textView_screenName_recyclerUser);

            btnAction.setOnClickListener(this);
            cardView.setOnClickListener(this);
            if (isBlock)
                btnAction.setText(context.getString(R.string.unblock));
            else
                btnAction.setText(context.getString(R.string.unmute));
        }

        void bindView(User user){
            Glide
                    .with(context)
                    .load(user.getBiggerProfileImageURL())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivAvatar);
            tvName.setText(user.getName());
            tvScreenName.setText("@" + user.getScreenName());

            if (getAdapterPosition() == userList.size() - 1){
                if (onEndSrollListener != null){
                    onEndSrollListener.data(null);
                }
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.linearLayout_main_recyclerUser){
                if (modelSelectList.get(getAdapterPosition()).isSelected()){
                    modelSelectList.get(getAdapterPosition()).setSelected(false);
                    cardView.setBackgroundColor(Color.WHITE);
                }else {
                    modelSelectList.get(getAdapterPosition()).setSelected(true);
                }
            }else if (v.getId() == R.id.textView_unMute){
                actionClick(userList.get(getAdapterPosition()).getId());
            }
        }
    }

}
