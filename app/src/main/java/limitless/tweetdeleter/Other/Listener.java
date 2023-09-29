package limitless.tweetdeleter.Other;

/**
 * @param <T> Global listener for any thing
 */
public class Listener<T> {

    /**
     * Return your data
     * @param t Data
     */
    public void data(T t) {};

    /**
     * @param e Error
     */
    public void error(Exception e) {};

}
