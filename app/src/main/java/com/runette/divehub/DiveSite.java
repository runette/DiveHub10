package com.runette.divehub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;



/**
 *Dive Sute data access object
 * Defines the basic CRUD operations (Create, Read, Update, Delete)
 * for the example, and gives the ability to list all reminders as well as
 * retrieve or modify a specific reminder.
 * 
 */


public class DiveSite  extends DiveDbAdapter {
	
    
	public static final String ARG_ITEM_ID = "site_id";
    public static final String ARG_ITEM_NAME = "site_name";
	private static final String TAG = "Site Object";
    

    public DiveSite(Context ctx) {
		super(ctx);
		this.mCtx = ctx;
		dbtable = DATABASE_TABLE_SITE;
		keys = SITE_ALL_KEYS;
		rowID = NO_ID_FLAG;
	}

	

 // 
    // SITE CRUD Operations
    //
        /**
         * Create a new site using the name
         * If the action is  successfully created return the new rowId
         * for that site, otherwise return a -1 to indicate failure.
         * 
         * @param divesite name
         * @return rowId or -1 if failed
         */
       public long create(String sitename) {
           ContentValues args = new ContentValues();
           args.put(KEY_SITE_NAME, sitename);
           return super.create( args);
        	
       }

        

        /**
         * Return a Cursor over the list of all sites in the database
         * 
         * @return Cursor over all reminders
         */
        public Cursor fetchAll() {

            return super.fetchAll( KEY_SITE_NAME + " COLLATE NOCASE ");
            		
        }
        
        	
}

