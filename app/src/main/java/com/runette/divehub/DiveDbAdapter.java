package com.runette.divehub;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple message database access helper class. 
 * Defines the basic CRUD operations (Create, Read, Update, Delete)
 * for the example, and gives the ability to list all reminders as well as
 * retrieve or modify a specific reminder.
 * 
 */
public class DiveDbAdapter {
	//
	// Application level Related Constants
	//
	public static final String APPLICATION_VERSION = " 0.10.1";
	public static final int APPLICATION_NUMBER = 17 ;		
	//
	// Database Related Constants
	//
	private static final String DATABASE_NAME = "logbook";   
    private static final int DATABASE_VERSION = 21;
    public String dbtable;
    public DatabaseHelper mDbHelper;
    public SQLiteDatabase mDb;
    public Context mCtx;
    public String[] keys;
    public long rowID;
    
    //
    // System Table Fields
    //
    public static final String KEY_ROWID = "_id";
    public static final String KEY_UUID = "uuid";
    public static final String KEY_UPDATE_DATE = "updated";
    public static final String UPDATE_FLAG = "_dirty";
    
    public static final String DATABASE_TABLE_DIVE = "dive";
    //
    // Table Fields
    //

    public static final String KEY_NUMBER = "divenumber";
    public static final String KEY_DATE = "date";
    public static final String KEY_TIME = "time";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_DEPTH = "depth";
    public static final String KEY_SITENAME = "divesitename";
    public static final String KEY_SITE= "divesiteid";
    public static final String KEY_BUDDY_NAME = "buddy";
    public static final String KEY_BUDDY = "buddyid";
    public static final String KEY_COMMENTS = "comments";
    public static final String KEY_SIGNATURE = "signature";
    public static final String KEY_DM_NAME = "divemaster";
    public static final String KEY_DM_ID= "divemasterid";
    public static final String[] DIVE_ALL_KEYS = {	KEY_ROWID,
    											KEY_NUMBER,
    											KEY_DATE,
    											KEY_TIME,
    											KEY_DURATION,
    											KEY_DEPTH,
    											KEY_SITENAME,
    											KEY_SITE,
    											KEY_BUDDY_NAME,
    											KEY_BUDDY,
    											KEY_COMMENTS,
    											KEY_SIGNATURE,
    											KEY_DM_NAME,
    											KEY_DM_ID
    										};		
    

    
    public static final String DATABASE_TABLE_SITE = "divesite";
    //
    // Table Fields
    //
    // ROWID is common
    public static final String KEY_SITE_NAME = "sitename";
    public static final String KEY_SITE_LOCATION = "location";
    public static final String KEY_SITE_COUNTRY = "country";
    public static final String KEY_SITE_LAT = "lat";
    public static final String KEY_SITE_LON = "lon";
    public static final String[] SITE_ALL_KEYS = {	KEY_ROWID,
    												KEY_SITE_NAME,
    												KEY_SITE_LOCATION, 
    												KEY_SITE_COUNTRY,
    												KEY_SITE_LAT,
    												KEY_SITE_LON};
    
    public static final String DATABASE_TABLE_PERSON = "person";
    //
    // Table Fields
    //
    public static final String KEY_PERSON_FIRSTNAME = "FirstName";
    public static final String KEY_PERSON_LASTNAME = "LastName";
    public static final String KEY_PERSON_PHONE = "Phone";
    public static final String KEY_PERSON_MOBILE = "Mobile";
    public static final String KEY_PERSON_EMAIL = "Email";
    public static final String[] PERSON_ALL_KEYS = {
    												KEY_ROWID,
    												KEY_PERSON_FIRSTNAME,
    												KEY_PERSON_LASTNAME,
    												KEY_PERSON_PHONE,
    												KEY_PERSON_MOBILE,
    												KEY_PERSON_EMAIL};
    
    public static final String DATABASE_TABLE_CITY = "city";
    //
    // Table Fields
    //
    public static final String KEY_CITY_NAME = "city";
    public static final String[] CITY_ALL_KEYS = {
    												KEY_ROWID,
    												KEY_CITY_NAME};
    
    public static final String DATABASE_TABLE_COUNTRY = "country";
    //
    // Table Fields
    //
    public static final String KEY_COUNTRY_NAME = "country";
    public static final String[] COUNTRY_ALL_KEYS = {
    												KEY_ROWID,
    												KEY_COUNTRY_NAME};
    //
    // Formats - published for use in calling objects
    //
    public static final String DATE_FORMAT = "yyyy-MM-dd"; 
	public static final String TIME_FORMAT = "HH:mm";
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
	public static final String SYSTEM_TIME = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final long NO_NAME = -10;
	public static final int PICK_SITE_REQUEST = 0x2;
	public static final int PICK_BUDDY_REQUEST = 0x3;
	public static final int PICK_DM_REQUEST = 0x4;
	public static final int DELETED_YES = 10;
	public static final int DELETED_NO= 11;
	public static final long NEW_DIVE_FLAG = -14;
	public static final long NEW_SITE_FLAG = -15;
	public static final long NEW_PERSON_FLAG = -16;
	public static final long NO_ID_FLAG = -20;
	public static final int DIRTY = 1;
	public static final int CLEAN = 0;
	public static final String DISPLAY = "display_flag";
	public static final String TWOPANE = "yes";
	public SimpleDateFormat sdf ;


	
    //
	// Other
    //
    private static final String TAG = "DiveDbAdapter";

    
    /**
     * Database creation SQL statement
     */
    private static final String DATABASE_CREATE_DIVE =
            "create table " + DATABASE_TABLE_DIVE + " ("
            		+ KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_NUMBER + " integer, " + KEY_NUMBER + UPDATE_FLAG + " integer, "
                    + KEY_DATE + " text, " + KEY_DATE + UPDATE_FLAG + " integer, "
                    + KEY_TIME + " text, "  + KEY_TIME + UPDATE_FLAG + " integer, "
                    + KEY_DURATION + " text," + KEY_DURATION + UPDATE_FLAG + " integer, "
                    + KEY_DEPTH + " text," + KEY_DEPTH + UPDATE_FLAG + " integer, "
                    + KEY_SITENAME + " text, " + KEY_SITENAME + UPDATE_FLAG + " integer, "
                    + KEY_SITE + " integer, " + KEY_SITE + UPDATE_FLAG + " integer, "
                    + KEY_BUDDY_NAME + " text, " + KEY_BUDDY_NAME + UPDATE_FLAG + " integer, "
                    + KEY_BUDDY + " text, " + KEY_BUDDY + UPDATE_FLAG + " integer, "
                    + KEY_SIGNATURE + " text, " + KEY_SIGNATURE + UPDATE_FLAG + " integer, "
                    + KEY_COMMENTS + " text, "  + KEY_COMMENTS + UPDATE_FLAG + " integer, "
                    + KEY_DM_NAME + " text, "  + KEY_DM_NAME + UPDATE_FLAG + " integer, "
                    + KEY_DM_ID + " text, "  + KEY_DM_ID + UPDATE_FLAG + " integer, "
                    + KEY_UUID + " text unique not null, " + KEY_UUID + UPDATE_FLAG + " integer, "
                    + KEY_UPDATE_DATE + " text " + KEY_UPDATE_DATE + UPDATE_FLAG + " integer )";
                    ; 
    private static final String DATABASE_CREATE_SITE =
            "create table " + DATABASE_TABLE_SITE + " ("
            		+ KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_SITE_NAME + " text, " + KEY_SITE_NAME + UPDATE_FLAG + " integer, "
                    + KEY_SITE_LOCATION + " text,"+ KEY_SITE_LOCATION + UPDATE_FLAG + " integer, "
                    + KEY_SITE_COUNTRY + " text, " + KEY_SITE_COUNTRY + UPDATE_FLAG + " integer, "
                    + KEY_SITE_LAT + " num,"+ KEY_SITE_LAT + UPDATE_FLAG + " integer, "
                    + KEY_SITE_LON + " num, " + KEY_SITE_LON + UPDATE_FLAG + " integer, "
                    + KEY_UUID  + " text unique not null, " + KEY_UUID + UPDATE_FLAG + " integer, "
                    + KEY_UPDATE_DATE  + " text " + KEY_UPDATE_DATE + UPDATE_FLAG + " integer )";
                    ; 
    private static final String DATABASE_CREATE_PERSON =
            "create table " + DATABASE_TABLE_PERSON + " ("
            		+ KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_PERSON_FIRSTNAME + " text, "  + KEY_PERSON_FIRSTNAME + UPDATE_FLAG + " integer, "
                    + KEY_PERSON_LASTNAME + " text," + KEY_PERSON_LASTNAME + UPDATE_FLAG + " integer, "
                    + KEY_PERSON_PHONE + " text, "  + KEY_PERSON_PHONE + UPDATE_FLAG + " integer, "
                    + KEY_PERSON_MOBILE + " text," + KEY_PERSON_MOBILE + UPDATE_FLAG + " integer, "
                    + KEY_PERSON_EMAIL + " text," + KEY_PERSON_EMAIL + UPDATE_FLAG + " integer, "
                    + KEY_UUID  + " text unique not null, " + KEY_UUID + UPDATE_FLAG + " integer, "
                    + KEY_UPDATE_DATE  + " text " + KEY_UPDATE_DATE + UPDATE_FLAG + " integer )";
                    ; 
  
                    private static final String DATABASE_CREATE_CITY =
                            "create table " + DATABASE_TABLE_CITY + " ("
                            		+ KEY_ROWID + " integer primary key autoincrement, "
                                    + KEY_CITY_NAME + " text, "  + KEY_CITY_NAME + UPDATE_FLAG + " integer, "
                                    + KEY_UUID  + " text unique not null, " + KEY_UUID + UPDATE_FLAG + " integer, "
                                    + KEY_UPDATE_DATE  + " text " + KEY_UPDATE_DATE + UPDATE_FLAG + " integer )";
                    ;
                    private static final String DATABASE_CREATE_COUNTRY =
                            "create table " + DATABASE_TABLE_COUNTRY + " ("
                            		+ KEY_ROWID + " integer primary key autoincrement, "
                                    + KEY_COUNTRY_NAME + " text, "  + KEY_COUNTRY_NAME + UPDATE_FLAG + " integer, "
                                    + KEY_UUID  + " text unique not null, " + KEY_UUID + UPDATE_FLAG + " integer, "
                                    + KEY_UPDATE_DATE  + " text " + KEY_UPDATE_DATE + UPDATE_FLAG + " integer )";
                    ;
    public static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE_DIVE);
            db.execSQL(DATABASE_CREATE_SITE);
            db.execSQL(DATABASE_CREATE_PERSON);
            db.execSQL(DATABASE_CREATE_CITY);
            db.execSQL(DATABASE_CREATE_COUNTRY);
            return;
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            switch (oldVersion) {
            /*case 11 :
            	db.execSQL("alter table " + DATABASE_TABLE_DIVE + " add column " + KEY_DM_NAME + " text, "  + KEY_DM_NAME + UPDATE_FLAG + " integer ");
            case 12 :
            	db.execSQL("alter table " + DATABASE_TABLE_DIVE + " add column " + KEY_DM_ID + " text " );
            	db.execSQL("alter table " + DATABASE_TABLE_DIVE + " add column " + KEY_DM_ID + UPDATE_FLAG + " integer ");
            case 17 :
            	 db.execSQL(DATABASE_CREATE_CITY);
                 db.execSQL(DATABASE_CREATE_COUNTRY);
            	return 	;
 */
            default :
            	db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_DIVE);
            	db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_SITE);
            	db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_PERSON);
                db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_CITY);
                db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_COUNTRY);
            	onCreate(db);
            
            }
        }
    } 

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    
    public DiveDbAdapter(final Context ctx) {
    	sdf = new SimpleDateFormat(SYSTEM_TIME, Locale.US);
    	sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    	dbtable = "" ;
       
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
    public DiveDbAdapter open() throws SQLException {
    	mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() throws SQLException{
    	 if (mDbHelper!= null) mDbHelper.close();
    }
    
    protected long create ( ContentValues initialValues) throws SQLException {
    	if (! initialValues.containsKey(KEY_UUID))  {
	    	UUID uuid = UUID.randomUUID();
	    	initialValues.put(KEY_UUID, uuid.toString());
    	}
    	initialValues.put(KEY_UPDATE_DATE, sdf.format( Calendar.getInstance().getTime()));
    	for (int i = 0; i<keys.length; i++) {
    		if (keys[i] != KEY_ROWID) {
    			initialValues.put(keys[i]+ UPDATE_FLAG, CLEAN);
    		}
    	}
    	
    	rowID =  mDb.insert(dbtable, null, initialValues);
    	return rowID;
    }
    

    
    protected Cursor fetchAll(String order) throws SQLException {

        return mDb.query(dbtable, keys, null, null, null, null, order);
    }
    protected Cursor fetchUpdate() {

        return mDb.query(dbtable, new String[] {KEY_ROWID, KEY_UUID, KEY_UPDATE_DATE}, null, null, null, null, KEY_UUID);
    }
    
    protected boolean delete() throws SQLException {
    	boolean res = mDb.delete(dbtable, KEY_ROWID + "=" + rowID, null) > 0;
    	rowID = NO_ID_FLAG;
        return res;
       
    }
    
    protected Cursor fetch() throws SQLException {

        Cursor mCursor =

                mDb.query(true, dbtable, keys, KEY_ROWID + "=" + rowID, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    protected Cursor fetch(long mID) throws SQLException {

        Cursor mCursor =

                mDb.query(true, dbtable, keys, KEY_ROWID + "=" + mID, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    protected boolean update( String key, String value) throws SQLException {
        //
    	// if the value is actually different - update the db and return true if a save is made
    	//
    
    	ContentValues args = new ContentValues();

        args.put(key, value);
        
        Cursor c = mDb.query(true, dbtable, new String[] {key}, KEY_ROWID + "=" + rowID, 
        		null, null, null, null, null );
        if (c != null) {
            c.moveToFirst();
        }
        String val =  c.getString(c.getColumnIndex(key));
        if ( val  != value)  {

        args.put(KEY_UPDATE_DATE, sdf.format( Calendar.getInstance().getTime()));
        args.put(key + UPDATE_FLAG, DIRTY);
        return mDb.update(dbtable, args, KEY_ROWID + "=" + rowID, null) > 0 ;
        
        } else {

        	return false;
        }
    }
    protected boolean update( ContentValues args, String date ) throws SQLException{
        

        args.put(KEY_UPDATE_DATE, date);
        return mDb.update(dbtable, args, KEY_ROWID + "=" + rowID, null) > 0 ;
        
    }
    
    
    protected long getNext ( ) {
    	Cursor c = mDb.query(dbtable, new String[] {KEY_ROWID}, null, null, null, null, null);
    	c.moveToFirst();
    	while  (! c.isLast()) {
    		if (c.getLong(c.getColumnIndex(KEY_ROWID)) == rowID) break;
    		c.moveToNext();
    	}
    	if( ! c.isLast()) {
    		c.moveToNext();
    	}
    	return c.getLong(c.getColumnIndex(KEY_ROWID));	
    }
    
    
    protected long getPrevious ()  throws SQLException {
    	Cursor c = mDb.query(dbtable, new String[] {KEY_ROWID}, null, null, null, null, null);
    	c.moveToFirst();
    	while  (! c.isLast()) {
    		if (c.getLong(c.getColumnIndex(KEY_ROWID)) == rowID) break;
    		c.moveToNext();
    	}
    	if( ! c.isFirst()) {
    		c.moveToPrevious();
    	}
    	return c.getLong(c.getColumnIndex(KEY_ROWID));	
    }
    
    public long findbyValue(String key, String value ) throws SQLException {
   	 long id = NO_NAME;
   	 Cursor mCursor =

                mDb.query(true, dbtable, keys, key + " = ?",new String[] {value} ,
                        null, null, null, null);
   	 
        if (mCursor.getCount() >= 1) {
            mCursor.moveToFirst();   		
        id = mCursor.getLong(mCursor.getColumnIndex(KEY_ROWID));
        rowID = id;
        }
        
        return id;
        
    }
    public long findByUuid(String uuid) throws SQLException {
    	 long id = NO_NAME;
    	 Cursor mCursor =

                 mDb.query(true, dbtable, keys, KEY_UUID + " = ?",new String[] {uuid} ,
                         null, null, null, null);
    	 
         if (mCursor.getCount() >= 1) {
             mCursor.moveToFirst();   		
         id = mCursor.getLong(mCursor.getColumnIndex(KEY_ROWID));
         rowID = id;
         } 		           	
         return id;
         
     } 
}

