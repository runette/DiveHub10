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


public class Person  extends DiveDbAdapter {
	
    
	public static final String ARG_ITEM_ID = "person_id";
    public static final String ARG_ITEM_NAME = "person_name";
	private static final String TAG = "Person Object";
   

    public Person(Context ctx) {
		super(ctx);
		this.mCtx = ctx;
		dbtable = DATABASE_TABLE_PERSON;
		keys = PERSON_ALL_KEYS;
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
    public long create(String lastname) {
        ContentValues initialValues = new ContentValues();
        // initialValues.put(KEY_PERSON_FIRSTNAME, firstname);
        initialValues.put(KEY_PERSON_LASTNAME, lastname);

        return super.create( initialValues);
    }

   
    public Cursor fetchAll() {

        return super.fetchAll( KEY_PERSON_LASTNAME + " COLLATE NOCASE ");
        		
    }
    
}

