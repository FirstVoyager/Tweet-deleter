package limitless.tweetdeleter.Fragment;


import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;

import limitless.tweetdeleter.Adapter.UserAdapter;
import limitless.tweetdeleter.Other.Listener;
import limitless.tweetdeleter.R;
import limitless.tweetdeleter.Utils.Deleter;
import limitless.tweetdeleter.Utils.Utils;
import limitless.tweetdeleter.databinding.FragmentHomeBinding;
import twitter4j.PagableResponseList;
import twitter4j.TwitterException;
import twitter4j.User;

public class MutesFragment extends BaseFragment {

    private FragmentHomeBinding binding;
    private UserAdapter userAdapter;
    private long cursor = -1;
    private boolean hasNext = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        userAdapter = new UserAdapter(getContext(), new ArrayList<>(), false);

        binding.fab.setImageResource(R.drawable.ic_volume_mute_black_24dp);
        binding.fab.setOnClickListener(v -> unMuteUsers(userAdapter.getAllUsers()));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.recyclerView.setAdapter(userAdapter);
        userAdapter.endListener(new Listener<Void>(){
            @Override
            public void data(Void aVoid) {
                super.data(aVoid);
                if (hasNext)
                    getData(cursor);
            }
        });
        getData(cursor);
    }

    private void getData(final long c) {
        binding.cardProgress.cardProgress.setVisibility(View.VISIBLE);
        deleter.getMuteUsers(c, accountId, new Listener<PagableResponseList<User>>() {
            @Override
            public void data(PagableResponseList<User> users) {
                super.data(users);
                binding.cardProgress.cardProgress.setVisibility(View.INVISIBLE);
                binding.recyclerView.setVisibility(View.VISIBLE);
                if (users != null && users.size() > 0){
                    userAdapter.insertNewData(users);
                    if (users.hasNext()){
                        cursor = users.getNextCursor();
                        hasNext = true;
                    }else {
                        hasNext = false;
                    }
                    Utils.toast(getContext(), userAdapter.getSize() + " user");
                    if (userAdapter.getSize() <= 0){
                        Utils.toast(getContext(), "Not found muted users");
                    }
                }
            }

            @Override
            public void error(Exception e) {
                super.error(e);
                binding.cardProgress.cardProgress.setVisibility(View.INVISIBLE);
                if (e != null){
                    Utils.toast(getContext(), e.getMessage());
                }
            }
        });
    }

    @Override
    public void removeFromList(long id) {
        userAdapter.actionClick(id);
    }
}
