package limitless.tweetdeleter.Other.Model;

public class UserFilterModel {
    public int id;
    public String name;
    public String idString;
    public long idLong;

    public UserFilterModel() {

    }

    public UserFilterModel(int id, String name, String idString, long idLong) {
        this.id = id;
        this.name = name;
        this.idString = idString;
        this.idLong = idLong;
    }
}
