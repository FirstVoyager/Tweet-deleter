package limitless.tweetdeleter.Fragment;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import limitless.tweetdeleter.Adapter.UserAdapter;
import limitless.tweetdeleter.Other.Listener;
import limitless.tweetdeleter.R;
import limitless.tweetdeleter.Utils.Deleter;
import limitless.tweetdeleter.Utils.Utils;
import limitless.tweetdeleter.databinding.FragmentHomeBinding;
import twitter4j.PagableResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

public class BlocksFragment extends BaseFragment {

    private FragmentHomeBinding binding;
    private UserAdapter userAdapter;
    private boolean hasNext = true;
    private long nextCursor = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        userAdapter = new UserAdapter(getContext(), new ArrayList<>(), true);

        binding.fab.setImageResource(R.drawable.ic_baseline_lock_open_24);
        binding.fab.setOnClickListener(v -> unBlockUsers(userAdapter.getAllUsers()));
        binding.recyclerView.setAdapter(userAdapter);
        userAdapter.endListener(new Listener<Void>() {
            @Override
            public void data(Void aVoid) {
                super.data(aVoid);
                if (hasNext)
                    getData(nextCursor);
            }
        });
        getData(nextCursor);
    }

    private void getData(long cursor) {
        deleter.getBlocks(cursor, new Listener<PagableResponseList<User>>() {
            @Override
            public void data(PagableResponseList<User> users) {
                super.data(users);
                binding.cardProgress.cardProgress.setVisibility(View.INVISIBLE);
                if (users != null && users.size() > 0){
                    userAdapter.insertNewData(users);
                    hasNext = users.hasNext();
                    nextCursor = users.getNextCursor();
                }
            }

            @Override
            public void error(Exception e) {
                super.error(e);
                binding.cardProgress.cardProgress.setVisibility(View.INVISIBLE);
                if (e != null)
                    Utils.toast(getContext(), e.getMessage());
            }
        });
    }

    @Override
    public void removeFromList(long id) {
        userAdapter.actionClick(id);
    }
}
