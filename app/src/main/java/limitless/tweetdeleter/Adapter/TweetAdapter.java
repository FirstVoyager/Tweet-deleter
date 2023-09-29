package limitless.tweetdeleter.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

import limitless.tweetdeleter.Activity.ImageActivity;
import limitless.tweetdeleter.Activity.VideoPlayerActivity;
import limitless.tweetdeleter.Dialog.BottomSheetTweetMore;
import limitless.tweetdeleter.Other.Listener;
import limitless.tweetdeleter.Other.Model.ModelSelect;
import limitless.tweetdeleter.R;
import limitless.tweetdeleter.Utils.Deleter;
import limitless.tweetdeleter.Utils.Utils;
import twitter4j.MediaEntity;
import twitter4j.Status;

public class TweetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Status> statusList;
    private Listener<Void> interfaceAlert;
    private boolean isLikeTweets;
    private FragmentManager fragmentManager;

    private Deleter deleter;

    private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.END | ItemTouchHelper.START) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//            if (viewHolder instanceof AdsViewHolder) {
//                return 0;
//            }
            return super.getSwipeDirs(recyclerView, viewHolder);
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int n = viewHolder.getAdapterPosition();
            statusList.remove(n);
            notifyItemRemoved(n);
        }
    };

    public TweetAdapter(Context context, List<Status> statusList, boolean isLikeTweets, RecyclerView recyclerView, FragmentManager fragmentManager) {
        this.context = context;
        this.statusList = statusList;
        this.deleter = new Deleter(context);
        this.isLikeTweets = isLikeTweets;
        this.fragmentManager = fragmentManager;
        ItemTouchHelper  itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new TweetViewHolder(LayoutInflater.from(context).inflate(R.layout.item_tweet, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TweetViewHolder) holder).bindView(statusList.get(position));
    }

    @Override
    public int getItemCount() {
        try {
            return statusList.size();
        }catch (Exception e){
            Utils.error(e);
            return 0;
        }
    }

    public void delete(long id){
        if (Utils.isOnline(context)){
            deleter.deleteTweet(id, new Listener<Status>() {
                @Override
                public void data(Status status) {
                    super.data(status);
                    if (status != null){
                        Utils.toast(context, status.getText());
                        remove(status.getId());
                    }else {
                        Utils.toast(context, "Cant't delete tweet");
                    }
                }
            });
        }else {
            Utils.toast(context, "Check your connection");
        }
    }

    /**
     * Remove tweet from list
     * @param id Tweet id
     */
    public void remove(long id) {
        int remove = -1;
        for (int i = 0; i < statusList.size(); i++) {
            if (statusList.get(i).getId() == id){
                remove = i;
                break;
            }
        }
        if (remove != -1){
            statusList.remove(remove);
            notifyItemRemoved(remove);
        }
    }

    public void removeAll() {
        statusList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void alertEndScroll(Listener<Void> alert) {
        interfaceAlert = alert;
    }

    public void insertNewData(List<Status> newS){
        if (newS == null)
            return;
        List<ModelSelect> newB = new ArrayList<>();
        for (int i = 0; i < newS.size(); i++) {
            newB.add(new ModelSelect(false));
        }
        statusList.addAll(newS);
        notifyDataSetChanged();
    }

    public List<Status> getAllData(){
        return statusList;
    }

    class TweetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView cvAvatar;
        private AppCompatImageView imageView;
        private MaterialTextView tvName, tvText, tvFavorite, tvMention, tvRetweet, tvScreenName;
        private View vMedia, ivVideo;
        public TweetViewHolder(@NonNull View v) {
            super(v);
            v.findViewById(R.id.imageButton_more).setOnClickListener(this);
            ivVideo = v.findViewById(R.id.imageView_video);
            cvAvatar = v.findViewById(R.id.imageView_avatar_recyclerTweet);
            imageView = v.findViewById(R.id.imageView_main);
            tvName = v.findViewById(R.id.textView_name_recyclerTweet);
            tvText = v.findViewById(R.id.textView_text_recyclerTweet);
            tvFavorite = v.findViewById(R.id.textView_favorite_recyclerTweet);
            tvRetweet = v.findViewById(R.id.textView_reTweet_recyclerTweet);
            tvMention = v.findViewById(R.id.textView_mentions_recyclerTweet);
            tvScreenName = v.findViewById(R.id.textView_screenName_recyclerTweet);
            vMedia = v.findViewById(R.id.cardView_media);
            imageView.setOnClickListener(this);
            AppCompatButton btnDelete = v.findViewById(R.id.button_delete);
            if (isLikeTweets){
                btnDelete.setText(context.getString(R.string.unlike));
            }else {
                btnDelete.setText(context.getString(R.string.delete));
            }
            btnDelete.setOnClickListener(this);
        }

        void bindView(Status status){
            if (status.isRetweet()){
                status = status.getRetweetedStatus();
            }
            Utils.loadImage(context, status.getUser().getBiggerProfileImageURL(), cvAvatar, true);
            tvName.setText(status.getUser().getName());
            tvFavorite.setText(String.valueOf(status.getFavoriteCount()));
            tvRetweet.setText(String.valueOf(status.getRetweetCount()));
            tvMention.setText(String.valueOf(status.getUserMentionEntities().length));
            tvText.setText(status.getText());
            tvScreenName.setText("@" + status.getUser().getScreenName());
            ivVideo.setVisibility(View.INVISIBLE);
            MediaEntity[] mediaEntities = status.getMediaEntities();
            if (Utils.hasMedia(mediaEntities)){
                vMedia.setVisibility(View.VISIBLE);
                Utils.loadImage(context, mediaEntities[0].getMediaURL(), imageView, false);
                if (Utils.hasVideo(mediaEntities)){
                    ivVideo.setVisibility(View.VISIBLE);
                }
            }else {
                vMedia.setVisibility(View.GONE);
            }
            if (getAdapterPosition() == statusList.size() - 1){
                    if (interfaceAlert != null)
                        interfaceAlert.data(null);
            }
        }

        @Override
        public void onClick(View v) {
            final Status status;
            try {
                status = statusList.get(getAdapterPosition());
                if (status == null)
                    return;
            } catch (Exception e) {
                Utils.error(e);
                return;
            }
            if (v.getId() == R.id.button_delete){
                if (isLikeTweets)
                    unLike(status.getId());
                else
                    delete(status.getId());
            }else if (v.getId() == R.id.imageButton_more){
                BottomSheetTweetMore more = new BottomSheetTweetMore(isLikeTweets, new Listener<String>() {

                    public void data(String s) {
                        if (s.equals(context.getString(R.string.copy_link))){
                            Utils.copyText(context, Deleter.tweetUrl(status));
                            Utils.toast(context, context.getString(R.string.copied));
                        }else if (s.equals(context.getString(R.string.share_tweet_via))){
                            Utils.shareText(context, Deleter.tweetUrl(status));
                        }else if (s.equals(context.getString(R.string.show_in_twitter))){
                            Utils.openUrl(context, Deleter.tweetUrl(status));
                        }else if (s.equals(context.getString(R.string.copy_the_tweet_text))){
                            Utils.copyText(context, status.getText());
                            Utils.toast(context, context.getString(R.string.copied));
                        }else if (s.equals(context.getString(R.string.delete))){
                            delete(status.getId());
                        }else if (s.equals(context.getString(R.string.unlike))){
                            unLike(status.getId());
                        }
                    }

                });
                more.show(fragmentManager, null);
            }else if (v.getId() == R.id.imageView_main){
                if (status.getMediaEntities() == null){
                    Utils.toast(context, context.getString(R.string.no_media));
                    return;
                }
                MediaEntity[] mediaEntities = status.getMediaEntities();
                if (Utils.hasVideo(mediaEntities)){
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra(VideoPlayerActivity.TWEET_URL, Deleter.tweetUrl(status));
                    intent.putExtra(Intent.EXTRA_TEXT, mediaEntities[0].getVideoVariants()[0].getUrl());
                    Utils.startActivity(context, intent);
                }else if (Utils.hasPhoto(mediaEntities)){
                    Intent intent = new Intent(context, ImageActivity.class);
                    intent.putExtra(VideoPlayerActivity.TWEET_URL, Deleter.tweetUrl(status));
                    intent.putExtra(Intent.EXTRA_TEXT, status.getMediaEntities()[0].getMediaURL());
                    Utils.startActivity(context, intent);
                }
            }
        }
    }

    public void unLike(long id) {
        deleter.unLike(id, new Listener<Status>(){
            @Override
            public void data(Status status) {
                super.data(status);
                if (status != null){
                    Utils.toast(context, status.getText());
                    remove(status.getId());
                }
            }
        });
    }
}

