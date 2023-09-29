package limitless.tweetdeleter.Fragment;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;

import limitless.tweetdeleter.Activity.ProActivity;
import limitless.tweetdeleter.Adapter.TweetAdapter;
import limitless.tweetdeleter.Other.Listener;
import limitless.tweetdeleter.R;
import limitless.tweetdeleter.Utils.Deleter;
import limitless.tweetdeleter.Utils.SQLiteDeleter;
import limitless.tweetdeleter.Utils.Utils;
import limitless.tweetdeleter.Utils.SharePref;
import limitless.tweetdeleter.databinding.FragmentHomeBinding;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;


public class LikesFragment extends BaseFragment {

    private FragmentHomeBinding binding;
    private TweetAdapter tweetAdapter;
    private Paging paging;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        tweetAdapter = new TweetAdapter(getContext(), new ArrayList<>(), true, binding.recyclerView, getActivity().getSupportFragmentManager());
        paging = new Paging(1, 200);

        binding.fab.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        binding.fab.setOnClickListener(v -> {
            if (sharePref.getBoolean(SharePref.ProVersion, false))
                unlikeTweets(tweetAdapter.getAllData());
            else {
                Utils.toast(getContext(), R.string.please_active_pro_version);
                Utils.startActivity(getContext(), ProActivity.class);
            }
        });
        binding.recyclerView.setAdapter(tweetAdapter);

        getData();
        tweetAdapter.alertEndScroll(new Listener<Void>() {
            @Override
            public void data(Void aVoid) {
                super.data(aVoid);
                paging = new Paging(paging.getPage() + 1, 200);
                getData();
            }
        });
    }

    private void getData() {
        binding.cardProgress.cardProgress.setVisibility(View.VISIBLE);
        deleter.getHomeLike(accountId, paging, new Listener<List<Status>>() {
            @Override
            public void data(List<Status> statusList) {
                super.data(statusList);
                binding.cardProgress.cardProgress.setVisibility(View.INVISIBLE);
                binding.recyclerView.setVisibility(View.VISIBLE);
                if (statusList != null && statusList.size() > 0){
                    tweetAdapter.insertNewData(statusList);
                }
            }

            @Override
            public void error(Exception e) {
                super.error(e);
                binding.cardProgress.cardProgress.setVisibility(View.INVISIBLE);
                binding.recyclerView.setVisibility(View.VISIBLE);
                if (e != null){
                    Utils.toast(getContext(), e.getMessage());
                }
            }
        });
    }

    @Override
    public void removeFromList(long id) {
        tweetAdapter.unLike(id);
    }
}
