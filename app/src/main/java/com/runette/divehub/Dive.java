package com.runette.divehub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


/**
 * Dive Data object 
 * Defines the basic CRUD operations (Create, Read, Update, Delete)
 * for the example, and gives the ability to list all reminders as well as
 * retrieve or modify a specific reminder.
 * 
 */


public class Dive  extends DiveDbAdapter {
	
    
	public static final String ARG_ITEM_ID = "dive_id";
	private static final String TAG = "Dive Object";
   

    public Dive(Context ctx) {
		super(ctx);
		mCtx = ctx;
		dbtable = DATABASE_TABLE_DIVE;
		keys = DIVE_ALL_KEYS;
		rowID = NO_ID_FLAG;
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
    
    

// 
// DIVE CRUD Operations
//
    /**
     * Create a new dive using the dive number, date and time provided. 
     * If the action is  successfully created return the new rowId
     * for that dive, otherwise return a -1 to indicate failure.
     * 
     * @param dive number
     * @param dive date
     * @param dive time
     * @return rowId or -1 if failed
     */
    public long create(int divenumber, String date, String time) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NUMBER, divenumber);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_TIME, time); 
        return super.create( initialValues);
        		
    }
    

    /**
     * Return a Cursor over the list of all dives in the database
     * 
     * @return Cursor over all reminders
     */
    public Cursor fetchAll() {

        return super.fetchAll( KEY_NUMBER);
        		
    }
    
   
    //
    // Get the highest current used divenumber and add 1
    // to get the default number for the next new dive
    //
    public long  nextNumber () {
    	long next = 0 ;
    	Cursor c = mDb.query(DATABASE_TABLE_DIVE, new String[] {KEY_ROWID, KEY_NUMBER,}, null, null, null, null, KEY_NUMBER );
    	if ( c.getCount() > 0 ) {
    		c.moveToLast();
    		int i = c.getInt(c.getColumnIndex(KEY_NUMBER));	
        	if (i > 0) next = i;
    	} 
    	next ++ ;
    	return next ;
    }
    
    
}

