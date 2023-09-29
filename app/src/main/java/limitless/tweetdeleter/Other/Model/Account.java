package limitless.tweetdeleter.Other.Model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.StringRes;

public class Account implements Parcelable {

    public String name;
    public String screenName;
    public long id;
    public String bio;
    public String profileUrl;
    public String headerUrl;
    public boolean isMain;
    public String accessToken;
    public String secretToken;
    public String consumerKey;
    public String consumerSecret;

    public Account() {
        this.id = 0;
        this.screenName = "";
        this.bio = "";
        this.profileUrl = "";
        this.headerUrl = "";
        this.isMain = false;
        this.accessToken = "";
        this.secretToken = "";
        this.consumerSecret = "";
        this.consumerKey = "";
    }

    public Account(String name, String screenName, String bio, long id, String profileUrl, String headerUrl, boolean isMain, String accessToken, String secretToken, String consumerKey, String consumerSecret) {
        this.name = name;
        this.screenName = screenName;
        this.bio = bio;
        this.id = id;
        this.profileUrl = profileUrl;
        this.headerUrl = headerUrl;
        this.isMain = isMain;
        this.accessToken = accessToken;
        this.secretToken = secretToken;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    protected Account(Parcel in) {
        name = in.readString();
        screenName = in.readString();
        id = in.readLong();
        bio = in.readString();
        profileUrl = in.readString();
        headerUrl = in.readString();
        isMain = in.readByte() != 0;
        accessToken = in.readString();
        secretToken = in.readString();
        consumerKey = in.readString();
        consumerSecret = in.readString();
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(screenName);
        dest.writeLong(id);
        dest.writeString(bio);
        dest.writeString(profileUrl);
        dest.writeString(headerUrl);
        dest.writeByte((byte) (isMain ? 1 : 0));
        dest.writeString(accessToken);
        dest.writeString(secretToken);
        dest.writeString(consumerKey);
        dest.writeString(consumerSecret);
    }


}
