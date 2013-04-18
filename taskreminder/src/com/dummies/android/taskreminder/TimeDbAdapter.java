
package com.dummies.android.taskreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple reminder database access helper class. 
 * Defines the basic CRUD operations (Create, Read, Update, Delete)
 * for the example, and gives the ability to list all reminders as well as
 * retrieve or modify a specific reminder.
 * 
 */
public class TimeDbAdapter {

	//
	// Database Related Constants
	//
	public static final String DATABASE_NAME = "data2";
    public static final String DATABASE_TABLE = "timeslots";
    private static final int DATABASE_VERSION = 3;
    
	public static final String KEY_DATE = "date";
    public static final String KEY_STIME = "starttime";
    public static final String KEY_ETIME = "endtime";
    
    //public static final String KEY_DATE_TIME = "reminder_date_time"; 
    public static final String KEY_ROWID = "_id";
    
    
    private static final String TAG = "TimeDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    /**
     * Database creation SQL statement
     */
    private static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE + " ("
            		+ KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_DATE + " text not null, " 
                    + KEY_STIME + " text not null, " 
                    + KEY_ETIME + " text not null);";
                    

    private final Context mCtx;

    public static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public TimeDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public TimeDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new reminder using the title, body and reminder date time provided. 
     * If the reminder is  successfully created return the new rowId
     * for that reminder, otherwise return a -1 to indicate failure.
     * 
     * @param title the title of the reminder
     * @param body the body of the reminder
     * @param reminderDateTime the date and time the reminder should remind the user
     * @return rowId or -1 if failed
     */
    public long createTimeslot(String date, String stime, String etime) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_STIME, stime);
        initialValues.put(KEY_ETIME, etime);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the reminder with the given rowId
     * 
     * @param rowId id of reminder to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteTimeslot(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all reminders in the database
     * 
     * @return Cursor over all reminders
     */
    public Cursor fetchAllTimeslots() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_DATE,
                KEY_STIME, KEY_ETIME}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the reminder that matches the given rowId
     * 
     * @param rowId id of reminder to retrieve
     * @return Cursor positioned to matching reminder, if found
     * @throws SQLException if reminder could not be found/retrieved
     */
    public Cursor fetchTimeslot(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                        KEY_DATE, KEY_STIME, KEY_ETIME}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the reminder using the details provided. The reminder to be updated is
     * specified using the rowId, and it is altered to use the title, body and reminder date time
     * values passed in
     * 
     * @param rowId id of reminder to update
     * @param title value to set reminder title to
     * @param body value to set reminder body to
     * @param reminderDateTime value to set the reminder time.
     * @param type value set to identify type of task 
     * @return true if the reminder was successfully updated, false otherwise
     */
    public boolean updateTimeslot(long rowId, String date, String stime, String etime) {
        ContentValues args = new ContentValues();
        args.put(KEY_DATE, date);
        args.put(KEY_STIME, stime);
        args.put(KEY_ETIME, etime);
        
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
