package org.technocracy.app.kleos.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.technocracy.app.kleos.api.User;

/**
 * Created by MOHIT on 13-Sep-16.
 */
public class DatabaseHandler extends SQLiteOpenHelper{

    private static final String TAG = DatabaseHandler.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "KleosInAppDatabase";

    private static final String TABLE_USER = "user";

    private static final String USER_ID = "uid";
    private static final String USER_NAME = "name";
    private static final String USER_USERNAME = "username";
    private static final String USER_EMAIL = "email";
    private static final String USER_PHONE = "phone";
    private static final String USER_BRANCH = "branch";
    private static final String USER_SEMESTER = "semester";
    private static final String USER_COLLEGE = "college";
    private static final String USER_ROLLNO = "rollno";
    private static final String USER_ACCESSTOKEN = "accesstoken";
    private static final String USER_CREATEDAT = "createdat";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE " + TABLE_USER + "("
                + USER_ID + " INTEGER PRIMARY KEY," + USER_NAME + " TEXT,"
                + USER_USERNAME + " TEXT," + USER_EMAIL + " TEXT,"
                + USER_PHONE + " TEXT," + USER_BRANCH + " TEXT,"
                + USER_SEMESTER + " INTEGER," + USER_COLLEGE + " TEXT,"
                + USER_ROLLNO + " TEXT," + USER_ACCESSTOKEN + " TEXT,"
                + USER_CREATEDAT + " TEXT" + ")";
        db.execSQL(createUserTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        // Create tables again
        onCreate(db);
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER_ID, user.getUid());
        values.put(USER_NAME, user.getName());
        values.put(USER_USERNAME, user.getUsername());
        values.put(USER_EMAIL, user.getEmail());
        values.put(USER_PHONE, user.getPhone());
        values.put(USER_BRANCH, user.getBranch());
        values.put(USER_SEMESTER, user.getSemester());
        values.put(USER_COLLEGE, user.getCollege());
        values.put(USER_ROLLNO, user.getRollno());
        values.put(USER_ACCESSTOKEN, user.getAccesstoken());
        values.put(USER_CREATEDAT, user.getCreatedAt());
        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
    }

    public User getUser() {
        User user = new User();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor != null) {
            cursor.moveToFirst();
            user.setUid(cursor.getInt(0));
            user.setName(cursor.getString(1));
            user.setUsername(cursor.getString(2));
            user.setEmail(cursor.getString(3));
            user.setPhone(cursor.getString(4));
            user.setBranch(cursor.getString(5));
            user.setSemester(cursor.getInt(6));
            user.setCollege(cursor.getString(7));
            user.setRollno(cursor.getString(8));
            user.setAccesstoken(cursor.getString(9));
            user.setCreatedAt(cursor.getString(10));
            cursor.close();
        }
        db.close();

        return user;
    }

    public void deleteUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
}
