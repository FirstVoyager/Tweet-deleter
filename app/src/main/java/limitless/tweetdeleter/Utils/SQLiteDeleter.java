package limitless.tweetdeleter.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import limitless.tweetdeleter.Other.Model.Account;
import limitless.tweetdeleter.Other.Model.TextFilterModel;
import limitless.tweetdeleter.Other.Model.UserFilterModel;

public class SQLiteDeleter extends SQLiteOpenHelper {

    private static String name = "sqlite.deleter";
    private static int version = 2;
    private SQLiteDatabase database;

    // table text filters
    private String textT = "textFilter";
    private String id = "id";
    private String text = "text";
    private String textCode = "CREATE TABLE " + textT + " ("
            + id + " INTEGER PRIMARY KEY AUTOINCREMENT , "
            + text + " TEXT );";
    // table user filter
    private String userT = "userFilter";
    private String nameT = "name";
    private String idLong = "idLong";
    private String idString = "idString";
    private String userCode = "CREATE TABLE " + userT + " ("
            + id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + nameT + " TEXT, "
            + idLong + " INTEGER, "
            + idString + " TEXT );";
    // Table accounts
    private String aTable = "accounts";
    private String aName = "name";
    private String aScreen = "screenName";
    private String aId = "id";
    private String aBio = "bio";
    private String aProfile = "profile";
    private String aHeader = "header";
    private String aIsMain = "isMain";
    private String aAccess = "accessToken";
    private String aSecret = "secretToken";
    private String aConsumerKey = "consumerKey";
    private String aConsumerSecret = "consumerSecret";
    private String accountCode = "CREATE TABLE IF NOT EXISTS " + aTable +" ("
            + aName + " TEXT, "
            + aScreen + " TEXT, "
            + aId + " INTEGER, "
            + aBio + " TEXT, "
            + aProfile + " TEXT, "
            + aHeader + " TEXT, "
            + aIsMain + " INTEGER, "
            + aAccess + " TEXT, "
            + aSecret + " TEXT, "
            + aConsumerKey + " TEXT, "
            + aConsumerSecret + " TEXT"
            + ");";

    public SQLiteDeleter(@Nullable Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(textCode);
        db.execSQL(userCode);
        try {
            db.execSQL(accountCode);
        }catch (Exception e){
            Utils.error(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == 2){
            db.execSQL(accountCode);
        }
    }

    /**
     * get readable database
     */
    private void read(){
        database = getReadableDatabase();
    }

    /**
     * get writable database
     */
    private void write(){
        database = getWritableDatabase();
    }

    // text table

    /**
     * add new filter item
     * @param filter new filter
     * @return added item model
     */
    public TextFilterModel putTextFilter(String filter){
        write();
        ContentValues cv = new ContentValues();
        cv.put(text, filter);
        if (database.insert(textT, null, cv) >= 0){
            return getTextFilter(filter);
        }else {
            return null;
        }
    }

    /**
     * get model from database with text
     * @param s text filter for find
     * @return it model finded
     */
    private TextFilterModel getTextFilter(String s) {
        read();
        Cursor cursor = database.rawQuery("SELECT * FROM " + textT + " WHERE text=='" + s + "'", null);
        if (cursor == null)
            return null;
        if (cursor.getCount() <= 0 && ! cursor.moveToFirst()){
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        TextFilterModel tfm = new TextFilterModel();
        tfm.id = cursor.getInt(cursor.getColumnIndex(id));
        tfm.text = cursor.getString(cursor.getColumnIndex(text));
        cursor.close();
        return tfm;
    }

    /**
     * delete text filter from database
     * @param s text filter for delete
     * @return if delete or no
     */
    public boolean deleteTextFilter(String s){
        write();
        return database.delete(textT, "text==?", new String[]{s}) >= 0;
    }

    /**
     * @return all text filters
     */
    public List<TextFilterModel> getTexts() {
        read();
        Cursor cursor = database.rawQuery("SELECT * FROM " + textT, null);
        if (cursor == null)
            return null;
        if (cursor.getCount() <= 0 || ! cursor.moveToFirst()){
            cursor.close();
            return null;
        }
        List<TextFilterModel> tfs = new ArrayList<>();
        int i = cursor.getColumnIndex(id);
        int t = cursor.getColumnIndex(text);
        do {
            tfs.add(new TextFilterModel(cursor.getInt(i), cursor.getString(t)));
        }while (cursor.moveToNext());
        return tfs;
    }

    // user table

    /**
     * insert new user
     * @param user model want to insert
     * @return model inserted with id
     */
    public UserFilterModel putUser(UserFilterModel user){
        write();
        ContentValues cv = new ContentValues();
        cv.put(nameT, user.name);
        cv.put(idLong, user.idLong);
        cv.put(idString, user.idString);
        if (database.insert(userT, null, cv) >= 0){
            return getUser(user.idLong);
        }else {
            return null;
        }
    }

    /**
     * get user model if exist in database
     * @param idUser id user you want to find if exist
     * @return user model
     */
    public UserFilterModel getUser(long idUser) {
        read();
        Cursor cursor = database.rawQuery("SELECT * FROM " + userT + " WHERE " + this.idLong + "==" + idUser, null);
        if (cursor == null)
            return null;
        if (cursor.getCount() <= 0 && ! cursor.moveToFirst()){
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        UserFilterModel user = new UserFilterModel();
        user.id = cursor.getInt(cursor.getColumnIndex(id));
        user.name = cursor.getString(cursor.getColumnIndex(nameT));
        user.idLong = cursor.getLong(cursor.getColumnIndex(idLong));
        user.idString = cursor.getString(cursor.getColumnIndex(idString));
        cursor.close();
        return user;
    }

    /**
     * delete user form database
     * @param idUser id user want to delete
     * @return if delete or no
     */
    public boolean deleteUser(long idUser){
        write();
        return  database.delete(userT, idLong + "==?", new String[]{String.valueOf(idUser)}) >= 0;
    }

    /**
     * get all users in database
     * @return user list
     *
     */
    public List<UserFilterModel> getUsers(){
        read();
        Cursor cursor = database.rawQuery("SELECT * FROM " + userT, null);
        if (cursor == null)
            return null;
        if (cursor.getCount() <= 0 && ! cursor.moveToFirst()){
            cursor.close();
            return null;
        }
        List<UserFilterModel> users = new ArrayList<>();
        int i = cursor.getColumnIndex(id);
        int idL = cursor.getColumnIndex(idLong);
        int ids = cursor.getColumnIndex(idString);
        int n = cursor.getColumnIndex(nameT);
        cursor.moveToFirst();
        do {
            users.add(new UserFilterModel(
                    cursor.getInt(i),
                    cursor.getString(n),
                    cursor.getString(ids),
                    cursor.getLong(idL)
            ));
        }while (cursor.moveToNext());
        cursor.close();
        return users;
    }

    public boolean insertAccount(@Nullable Account a){
        if (a == null)
            return false;
        write();
        ContentValues cv = new ContentValues();
        cv.put(aName, a.name);
        cv.put(aScreen, a.screenName);
        cv.put(aId, a.id);
        cv.put(aBio, a.bio);
        cv.put(aProfile, a.profileUrl);
        cv.put(aHeader, a.headerUrl);
        cv.put(aIsMain, a.isMain);
        cv.put(aAccess, a.accessToken);
        cv.put(aSecret, a.secretToken);
        cv.put(aConsumerKey, a.consumerKey);
        cv.put(aConsumerSecret, a.consumerSecret);
        return database.insert(aTable, null, cv) > 0;
    }

    public boolean deleteAccount(long id){
        write();
        return database.delete(aTable, aId + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    public Account getMainAccount(){
        read();
        Account a = new Account();
        Cursor cursor = database.rawQuery("SELECT * FROM " + aTable + " WHERE " + aIsMain + "=1", null);
        if (cursor == null)
            return a;
        if (cursor.getCount() <= 0){
            cursor.close();
            return a;
        }
        cursor.moveToFirst();
        a = new Account();
        a.name = cursor.getString(cursor.getColumnIndex(aName));
        a.screenName = cursor.getString(cursor.getColumnIndex(aScreen));
        a.bio = cursor.getString(cursor.getColumnIndex(aBio));
        a.id = cursor.getLong(cursor.getColumnIndex(aId));
        a.profileUrl = cursor.getString(cursor.getColumnIndex(aProfile));
        a.headerUrl = cursor.getString(cursor.getColumnIndex(aHeader));
        a.isMain = true;
        a.accessToken = cursor.getString(cursor.getColumnIndex(aAccess));
        a.secretToken = cursor.getString(cursor.getColumnIndex(aSecret));
        a.consumerKey = cursor.getString(cursor.getColumnIndex(aConsumerKey));
        a.consumerSecret = cursor.getString(cursor.getColumnIndex(aConsumerSecret));
        return a;
    }

    public void setMainAccount(long id){
        write();
        String query = "UPDATE " + aTable + " SET " + aIsMain + "=0" + " WHERE " + aIsMain + "=1";
        database.execSQL(query);
        query = "UPDATE " + aTable + " SET " + aIsMain + "=1" + " WHERE " + aId + "=" + id;
        database.execSQL(query);
    }

    public List<Account> getAccounts(){
        read();
        Cursor cursor = database.rawQuery("SELECT * FROM " + aTable, null);
        if (cursor == null)
            return null;
        if (cursor.getCount() <= 0 || ! cursor.moveToFirst()){
            cursor.close();
            return null;
        }
        List<Account> accounts = new ArrayList<>();
        int name = cursor.getColumnIndex(aName);
        int screen = cursor.getColumnIndex(aScreen);
        int id = cursor.getColumnIndex(aId);
        int bio = cursor.getColumnIndex(aBio);
        int profile = cursor.getColumnIndex(aProfile);
        int header = cursor.getColumnIndex(aHeader);
        int main = cursor.getColumnIndex(aIsMain);
        int access = cursor.getColumnIndex(aAccess);
        int secret = cursor.getColumnIndex(aSecret);
        int consumerKey = cursor.getColumnIndex(aConsumerKey);
        int consumerSecret = cursor.getColumnIndex(aConsumerSecret);
        do {
            accounts.add(new Account(
                    cursor.getString(name),
                    cursor.getString(screen),
                    cursor.getString(bio),
                    cursor.getLong(id),
                    cursor.getString(profile),
                    cursor.getString(header),
                    cursor.getInt(main) == 1,
                    cursor.getString(access),
                    cursor.getString(secret),
                    cursor.getString(consumerKey),
                    cursor.getString(consumerSecret)
            ));
        }while (cursor.moveToNext());
        cursor.close();
        return accounts;
    }

    public boolean checkAccount(long id) {
        read();
        Cursor cursor = database.rawQuery("SELECT * FROM " + aTable + " WHERE " + aId + "=" + id + " LIMIT 1", null);
        if (cursor == null)
            return false;
        if (cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        return true;
    }

    public int accountCount() {
        read();
        Cursor cursor = database.rawQuery("SELECT * FROM " + aTable, null);
        if (cursor == null)
            return 0;
        int n = cursor.getCount();
        cursor.close();
        return n;
    }
}
