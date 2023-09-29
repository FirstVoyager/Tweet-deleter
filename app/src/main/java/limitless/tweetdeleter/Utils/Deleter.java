package limitless.tweetdeleter.Utils;

import android.content.Context;
import android.os.AsyncTask;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import limitless.tweetdeleter.Other.Constant;
import limitless.tweetdeleter.Other.Listener;
import limitless.tweetdeleter.Other.Model.Account;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class Deleter {

    private Twitter twitter;
    private Context context;
    private SQLiteDeleter sqlite;

    public Deleter(Context context){
        sqlite = new SQLiteDeleter(context);
        this.context = context;
        setUpLogin();
    }

    public Deleter(Context context, Listener<RequestToken> token, String... keys){
        sqlite = new SQLiteDeleter(context);
        this.context = context;
        new SetUpNoLogin(token).execute(keys);
    }

    public static String tweetUrl(Status s) {
        String url = "https://twitter.com/";
        if (s.isRetweet()){
            url = url + s.getRetweetedStatus().getUser().getScreenName() + "/status/" + s.getRetweetedStatus().getId();
        }else {
            url = url + s.getUser().getScreenName() + "/status/" + s.getId();
        }
        return url;
    }

    // functions

    private void setUpLogin() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        Account a = sqlite.getMainAccount();
        if (a == null)
            return;
        cb
                .setDebugEnabled(true)
                .setOAuthConsumerKey(a.consumerKey)
                .setOAuthConsumerSecret(a.consumerSecret)
                .setOAuthAccessToken(a.accessToken)
                .setOAuthAccessTokenSecret(a.secretToken);

        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
    }

    public void saveTwitterLogin(String verif, RequestToken requestToken, Listener<Account> accountListener) {
        new SaveTwitterLogin(verif, requestToken, accountListener).execute();
    }

    public void getHomeTweets(long id, Paging paging, Listener<List<Status>> listener){
        new GetHomeTweets(id, paging, listener).execute();
    }

    public void deleteTweet(long id, Listener<Status> listener) {
        new DeleteTweet(id, listener).execute();
    }

    public void getHomeReTweets(long id, Paging paging, Listener<List<Status>> listener) {
        new GetHomeReTweet(id, paging, listener).execute();
    }

    public void getHomeLike(long id, Paging paging, Listener<List<Status>> listener) {
        new GetHomeLikes(id, paging, listener).execute();
    }

    public void getHomeMentions(Paging paging, Listener<List<Status>> listener) {
        new GetHomeMentions(paging, listener).execute();
    }

    public void getBlocks(long i, Listener<PagableResponseList<User>> interfaceUserList) {
        new GetBlocks(interfaceUserList).execute(i);
    }

    public void unBlockUser(long id, Listener<User> interfaceUser) {
        new UnBlock(id, interfaceUser).execute();
    }

    public void getFavoriteUser(long id, Listener<PagableResponseList<User>> interfaceUserList) {
        new GetMutes(id, interfaceUserList).execute();
    }

    public void getUser(long id, Listener<User> interfaceUser) {
        new GetUser(id, interfaceUser).execute();
    }

    public void deleteMute(long id, Listener<User> interfaceUser) {
        new UnMuteUser(id, interfaceUser).execute();
    }

    public void unLike(long id, Listener<Status> listener) {
        new UnLike(id, listener).execute();
    }

    public void unMuteUser(long id, Listener<User> interfaceUser) {
        new UnMuteUser(id, interfaceUser).execute();
    }

    public void getMuteUsers(long cursor, long id, Listener<PagableResponseList<User>> interfaceUserList) {
        new GetMuteUsers(cursor, id, interfaceUserList).execute();
    }

    public void showUser(long id, Listener<User> getShowUser) {
        new GetShowUser(id, null, getShowUser).execute();
    }

    public void showUser(String ids, Listener<User> interfaceUser){
        new GetShowUser(0, ids, interfaceUser).execute();
    }

    public void getMain(Paging p, Listener<List<Status>> listener) {
        new GetMain(listener).execute(p);
    }

    public void followUser(@NonNull String id, Listener<User> userListener) {
        new FollowUser(id, userListener).execute();
    }


    // inner classes #####################################################################

    private class FollowUser extends AsyncTask<Void, Void, User>{

        private String id;
        private TwitterException exception;
        private Listener<User> userListener;

        public FollowUser(String id, Listener<User> userListener) {
            this.id = id;
            this.userListener = userListener;
        }

        @Override
        protected User doInBackground(Void... voids) {
            try {
                return twitter.createFriendship(id);
            } catch (TwitterException e) {
                exception = e;
                Utils.error(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            if (userListener == null)
                return;
            if (user == null){
                userListener.error(exception);
            }else {
                userListener.data(user);
            }
        }
    }

    private class GetMain extends AsyncTask<Paging, Void, List<Status>>{

        private Listener<List<twitter4j.Status>> listener;
        private TwitterException exception;

        public GetMain(Listener<List<twitter4j.Status>> listener) {
            this.listener = listener;
        }

        @Override
        protected List<twitter4j.Status> doInBackground(Paging... pagings) {
            try {
                return twitter.getUserTimeline(pagings[0]);
            } catch (TwitterException e) {
                Utils.error(e);
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<twitter4j.Status> statuses) {
            if (statuses == null){
                listener.error(exception);
            }else {
                listener.data(statuses);
            }
            super.onPostExecute(statuses);
        }
    }

    private class SetUpNoLogin extends AsyncTask<String, Void, RequestToken>{

        private Listener<RequestToken> listener;
        private Exception exception;

        public SetUpNoLogin(Listener<RequestToken> token) {
            this.listener = token;
        }

        @Override
        protected RequestToken doInBackground(String... keys) {
            try {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(keys[0]);
                builder.setOAuthConsumerSecret(keys[1]);
                Configuration configuration = builder.build();
                TwitterFactory twitterFactory = new TwitterFactory(configuration);
                twitter = twitterFactory.getInstance();
                return twitter.getOAuthRequestToken(Constant.twitter_back_url);
            } catch (TwitterException e) {
                Utils.error(e);
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(RequestToken token) {
            super.onPostExecute(token);
            if (token == null)
                listener.error(exception);
            else
                listener.data(token);
        }
    }

    private class SaveTwitterLogin extends AsyncTask<Void, Void, Account>{

        private String verif;
        private RequestToken requestToken;
        private Listener<Account> accountListener;

        public SaveTwitterLogin(String verif, RequestToken requestToken, Listener<Account> accountListener) {
            this.verif = verif;
            this.requestToken = requestToken;
            this.accountListener = accountListener;
        }

        @Override
        protected Account doInBackground(Void... voids) {
            try {
                AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verif);
                User user = twitter.showUser(accessToken.getUserId());

                Account a = new Account();
                a.id = accessToken.getUserId();
                a.accessToken = accessToken.getToken();
                a.secretToken = accessToken.getTokenSecret();
                a.screenName = accessToken.getScreenName();
                a.name = user.getName();
                a.profileUrl = user.getBiggerProfileImageURL();
                a.headerUrl = user.getProfileBannerURL();
                a.bio = user.getDescription();
                a.isMain = true;
                return a;
            } catch (TwitterException e) {
                Utils.error(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Account account) {
            super.onPostExecute(account);
            accountListener.data(account);
        }
    }

    private class GetHomeTweets extends AsyncTask<Void, Void, List<Status>>{

        private long id;
        private Paging paging;
        private Listener<List<twitter4j.Status>> listener;
        private TwitterException exception;

        public GetHomeTweets(long id, Paging paging, Listener<List<twitter4j.Status>> listener) {
            this.id = id;
            this.paging = paging;
            this.listener = listener;
        }

        @Override
        protected List<twitter4j.Status> doInBackground(Void... voids) {
            try {
                List<twitter4j.Status> statuses = twitter.getUserTimeline(id, paging);
                List<twitter4j.Status> newS = new ArrayList<>();
                for (twitter4j.Status s : statuses) {
                    if (! s.isRetweeted() && ! s.getText().startsWith("@") && ! s.getText().startsWith("RT")){
                        newS.add(s);
                    }
                }
                return newS;
            } catch (TwitterException e) {
                Utils.error(e);
                exception = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<twitter4j.Status> statuses) {
            super.onPostExecute(statuses);
            if (statuses == null){
                listener.error(exception);
            }else{
                listener.data(statuses);
            }
        }
    }

    private class DeleteTweet extends AsyncTask<Void, Void, Status>{

        private long id;
        private Listener<twitter4j.Status> listener;
        private Exception exception;

        public DeleteTweet(long id, Listener<twitter4j.Status> listener) {
            this.id = id;
            this.listener = listener;
        }

        @Override
        protected twitter4j.Status doInBackground(Void... voids) {
            try {
                return twitter.destroyStatus(id);
            } catch (TwitterException e) {
                Utils.error(e);
                exception = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(twitter4j.Status status) {
            super.onPostExecute(status);
            if (status == null)
                listener.error(exception);
            else 
                listener.data(status);
        }
    }

    private class GetHomeReTweet extends AsyncTask<Void, Void, List<Status>>{

        private long id;
        private Paging paging;
        private Listener<List<twitter4j.Status>> listener;

        public GetHomeReTweet(long id, Paging paging, Listener<List<twitter4j.Status>> listener) {
            this.id = id;
            this.paging = paging;
            this.listener = listener;
        }

        @Override
        protected List<twitter4j.Status> doInBackground(Void... voids) {
            try {
                List<twitter4j.Status> statuses = twitter.getUserTimeline(id, paging);
                List<twitter4j.Status> newS = new ArrayList<>();
                for (twitter4j.Status s : statuses) {
                    if (s.isRetweeted()){
                        newS.add(s);
                    }
                }
                return newS;
            } catch (TwitterException e) {
                Utils.error(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<twitter4j.Status> statuses) {
            super.onPostExecute(statuses);
            listener.data(statuses);
        }
    }

    private class GetHomeLikes extends AsyncTask<Void, Void, List<Status>>{

        private long id;
        private Paging paging;
        private Listener<List<twitter4j.Status>> listener;
        private Exception exception;

        public GetHomeLikes(long id, Paging paging, Listener<List<twitter4j.Status>> listener) {
            this.listener = listener;
            this.id = id;
            this.paging = paging;
        }

        @Override
        protected List<twitter4j.Status> doInBackground(Void... voids) {
            try {
                return twitter.favorites().getFavorites(id, paging);
            } catch (TwitterException e) {
                Utils.error(e);
                exception = e;
            }
            return null;

        }

        @Override
        protected void onPostExecute(List<twitter4j.Status> statuses) {
            super.onPostExecute(statuses);
            if (statuses == null)
                listener.error(exception);
            else
                listener.data(statuses);
        }
    }

    private class GetHomeMentions extends AsyncTask<Void, Void, List<Status>>{

        private Paging paging;
        private Listener<List<twitter4j.Status>> listener;

        public GetHomeMentions(Paging paging, Listener<List<twitter4j.Status>> listener) {
            this.paging = paging;
            this.listener = listener;
        }

        @Override
        protected List<twitter4j.Status> doInBackground(Void... voids) {
            try {
                List<twitter4j.Status> statuses = twitter.getUserTimeline(paging);
                List<twitter4j.Status> newS = new ArrayList<>();
                for (twitter4j.Status s : statuses) {
                    if (s.getText().startsWith("@")){
                        newS.add(s);
                    }
                }
                return newS;
            } catch (TwitterException e) {
                Utils.error(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<twitter4j.Status> statuses) {
            super.onPostExecute(statuses);
            listener.data(statuses);
        }
    }

    private class GetBlocks extends AsyncTask<Long, Void, PagableResponseList<User>>{

        private Listener<PagableResponseList<User>> userList;
        private TwitterException exception;

        public GetBlocks(Listener<PagableResponseList<User>> userList) {
            this.userList = userList;
        }

        @Override
        protected PagableResponseList<User> doInBackground(Long... longs) {
            try {
                return twitter.getBlocksList(longs[0]);
            } catch (TwitterException e) {
                Utils.error(e);
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(PagableResponseList<User> users) {
            super.onPostExecute(users);
            if (users == null){
                userList.error(exception);
            }else {
                userList.data(users);
            }
        }
    }

    private class UnBlock extends AsyncTask<Void, Void, User>{

        private long id;
        private Listener<User> interfaceUser;

        public UnBlock(long id, Listener<User> interfaceUser) {
            this.id = id;
            this.interfaceUser = interfaceUser;
        }

        @Override
        protected User doInBackground(Void... voids) {
            try {
                return twitter.destroyBlock(id);
            } catch (TwitterException e) {
                Utils.error(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            interfaceUser.data(user);
            super.onPostExecute(user);
        }
    }

    private class GetMutes extends AsyncTask<Void, Void, PagableResponseList<User>>{

        private long id;
        private Listener<PagableResponseList<User>> userList;

        public GetMutes(long id, Listener<PagableResponseList<User>> userList) {
            this.id = id;
            this.userList = userList;
        }

        @Override
        protected PagableResponseList<User> doInBackground(Void... voids) {
            try {
                return twitter.getMutesList(id);
            } catch (TwitterException e) {
                Utils.error(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(PagableResponseList<User> users) {
            userList.data(users);
            super.onPostExecute(users);
        }
    }

    private class GetUser extends AsyncTask<Void, Void, User>{

        private long id;
        private Listener<User> interfaceUser;

        public GetUser(long id, Listener<User> interfaceUser) {
            this.id = id;
            this.interfaceUser = interfaceUser;
        }

        @Override
        protected User doInBackground(Void... voids) {
            try {
                return twitter.showUser(id);
            } catch (TwitterException e) {
                Utils.error(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            interfaceUser.data(user);
            super.onPostExecute(user);
        }
    }

    private class UnMuteUser extends AsyncTask<Void, Void, User>{

        private long id;
        private Listener<User> interfaceUser;
        private TwitterException exception;

        public UnMuteUser(long id, Listener<User> interfaceUser) {
            this.id = id;
            this.interfaceUser = interfaceUser;
        }

        @Override
        protected User doInBackground(Void... voids) {
            try {
                return twitter.destroyMute(id);
            } catch (TwitterException e) {
                Utils.error(e);
                exception = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            if (user == null){
                interfaceUser.error(exception);
            }else {
                interfaceUser.data(user);
            }
            super.onPostExecute(user);
        }
    }

    private class UnLike extends AsyncTask<Void, Void, Status>{

        private long id;
        private Listener<twitter4j.Status> listener;

        public UnLike(long id, Listener<twitter4j.Status> listener) {
            this.id = id;
            this.listener = listener;
        }

        @Override
        protected twitter4j.Status doInBackground(Void... voids) {
            try {
                return twitter.destroyFavorite(id);
            } catch (TwitterException e) {
                Utils.error(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(twitter4j.Status status) {
            listener.data(status);
            super.onPostExecute(status);
        }
    }

    private class GetMuteUsers extends AsyncTask<Void, Void, PagableResponseList<User>>{

        private long cursor;
        private long id;
        private Listener<PagableResponseList<User>> userList;

        public GetMuteUsers(long cursor, long id, Listener<PagableResponseList<User>> userList) {
            this.cursor = cursor;
            this.id = id;
            this.userList = userList;
        }

        @Override
        protected PagableResponseList<User> doInBackground(Void... voids) {
            try {
                return twitter.getMutesList(cursor);
            } catch (TwitterException e) {
                Utils.error(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(PagableResponseList<User> users) {
            userList.data(users);
            super.onPostExecute(users);
        }
    }

    private class GetShowUser extends AsyncTask<Void, Void, User>{

        private long id;
        private String screenName;
        private Listener<User> listener;
        private Exception exception;

        public GetShowUser(long id, String screenName, Listener<User> listener) {
            this.id = id;
            this.screenName = screenName;
            this.listener = listener;
        }

        @Override
        protected User doInBackground(Void... voids) {
            try {
                if (screenName != null)
                    return twitter.showUser(screenName);
                else
                    return twitter.showUser(id);
            } catch (TwitterException e) {
                Utils.error(e);
                exception = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            listener.data(user);
            if (user == null)
                listener.error(exception);
            else
                listener.data(user);
        }
    }

}
