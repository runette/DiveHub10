package com.runette.divehub;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


/**
 * Simple person data access class. 
 * Defines the basic CRUD operations (Create, Read, Update, Delete)
 * for the example, and gives the ability to list all reminders as well as
 * retrieve or modify a specific reminder.
 * 
 */


public class Country  extends DiveDbAdapter {
	
    
	public static final String ARG_ITEM_ID = "country_id";
    public static final String ARG_ITEM_NAME = "country_name";
  
	private static final String TAG = "Country Object";
   

    public Country(Context ctx) {
		super(ctx);
		this.mCtx = ctx;
		dbtable = DATABASE_TABLE_COUNTRY;
		keys = COUNTRY_ALL_KEYS;
		rowID = NO_ID_FLAG;
	}

	
   

// 
// Person CRUD Operations
//
    /**
     * Create a new person using the dive name
     * If the action is  successfully created return the new rowId
     * for that dive, otherwise return a -1 to indicate failure.
     * 
     * @param person name

     * @return rowId or -1 if failed
     */
    public long create(String name) {
        ContentValues initialValues = new ContentValues();
        // initialValues.put(KEY_PERSON_FIRSTNAME, firstname);
        initialValues.put(KEY_COUNTRY_NAME, name);
        return super.create( initialValues);
    }

   
    /**
     * Return a Cursor over the list of all peopl;e in the database
     * 
     * @return Cursor over all people
     */
    public Cursor fetchAll() {

        return super.fetchAll( KEY_COUNTRY_NAME + " COLLATE NOCASE ");
        		
    }
    
           
     
}

