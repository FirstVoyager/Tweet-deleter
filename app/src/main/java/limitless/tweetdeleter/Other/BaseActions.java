package limitless.tweetdeleter.Other;

import java.util.List;

import twitter4j.Status;
import twitter4j.User;

/**
 * Base actions for fragment
 */
public interface BaseActions {

    /**
     * @param statuses List of tweets for delete
     */
    void deleteTweets(List<Status> statuses);

    /**
     * @param statuses List of tweets for unlike
     */
    void unlikeTweets(List<Status> statuses);

    /**
     * @param users List of users for unblock
     */
    void unBlockUsers(List<User> users);

    /**
     * @param users List of users for unmute
     */
    void unMuteUsers(List<User> users);

    /**
     * @param id Id for tweet or user
     */
    void removeFromList(long id);

}
