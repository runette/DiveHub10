package com.runette.divehub;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class Divelog {
	
	private Context ctx;
	private static final String  TAG = "Divelog";
	private SQLiteDatabase mDB;
	private Dive mDive;
	private DiveSite mSite;
	private Person mPerson;
	private City mCity;
	private Country mCountry;
	private String auuid;
    private String buuid;
    private String aupdated;
    private String bupdated;
    private Date adate;
    private Date bdate;
    
	//
	// Database Column names - the B string matches columnwised the appropriate ALL_KEYS string from DiveDbAdapter
	//
    public static final String SYNCH_FILE = "divinglogsynch";
	public static final String DB_PATH = "/data/data/com.runette.divehub/";
	public static final String SOURCE = "Divelog/";
	public static final String FILE1 = "DivelogSync.xml";
	private static final String B_KEY_ROWID = "ID";
	private static final String B_KEY_UUID = "UUID";
	private static final String B_KEY_UPDATE_DATE ="Updated";
	public static final String B_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	
	SimpleDateFormat adateTimeFormat ;
	SimpleDateFormat bdateTimeFormat ;
	
	private static final String B_KEY_NUMBER = "Number";
    private static final String B_KEY_DATE = "Divedate";
    private static final String B_KEY_TIME = "Entrytime";
    private static final String B_KEY_DURATION = "Divetime";
    private static final String B_KEY_DEPTH = "Depth";
    private static final String B_KEY_SITENAME = "Place";
    private static final String B_KEY_SITE= "PlaceID";
    private static final String B_KEY_BUDDY_NAME = "Buddy";
    private static final String B_KEY_BUDDY = "BuddyIDs";
    private static final String B_KEY_COMMENTS = "Comments";
    private static final String B_KEY_SIGNATURE = "Signature";
    private static final String B_KEY_DM_NAME = "Divemaster";
    
	
	private static final String[] B_DIVE_ALL_KEYS =  {	B_KEY_ROWID,
		B_KEY_NUMBER,
		B_KEY_DATE,
		B_KEY_TIME,
		B_KEY_DURATION,
		B_KEY_DEPTH,
		B_KEY_SITENAME,
		B_KEY_SITE,
		B_KEY_BUDDY_NAME,
		B_KEY_BUDDY,
		B_KEY_COMMENTS,
		B_KEY_SIGNATURE,
		B_KEY_DM_NAME,
		null
	};
	
	 private static final String B_KEY_SITE_NAME = "Place";
	 //private static final String B_KEY_SITE_LOCATION = "City";
	 //private static final String B_KEY_SITE_COUNTRY = "Country";
	 private static final String B_KEY_SITE_LAT = "Lat";
	 private static final String B_KEY_SITE_LON = "Lon";
	 private static final String[] B_SITE_ALL_KEYS = {	B_KEY_ROWID,
		 B_KEY_SITE_NAME,
		 null, 
		 null,
		 B_KEY_SITE_LAT,
		 B_KEY_SITE_LON};
				
	 private static final String B_KEY_PERSON_FIRSTNAME = "FirstName";
	 private static final String B_KEY_PERSON_LASTNAME = "LastName";
	 private static final String B_KEY_PERSON_PHONE = "Phone";
	 private static final String B_KEY_PERSON_MOBILE = "Mobile";
	 private static final String B_KEY_PERSON_EMAIL = "Email";
	 private static final String[] B_PERSON_ALL_KEYS = {
														B_KEY_ROWID,
														B_KEY_PERSON_FIRSTNAME,
														B_KEY_PERSON_LASTNAME,
														B_KEY_PERSON_PHONE,
														B_KEY_PERSON_MOBILE,
														B_KEY_PERSON_EMAIL};
												 
	 
	 
	 private static final String B_KEY_COUNTRY_NAME = "Country";
	 private static final String[] B_COUNTRY_ALL_KEYS = {
	    												B_KEY_ROWID,
	    												B_KEY_COUNTRY_NAME};
			

	 private static final String B_KEY_CITY_NAME = "City";
	 private static final String[] B_CITY_ALL_KEYS = {
	    												B_KEY_ROWID,
	    												B_KEY_CITY_NAME};
  
       

	
	
	public Divelog ( Context c) {
		ctx = c ;
        adateTimeFormat = new SimpleDateFormat(DiveDbAdapter.SYSTEM_TIME, Locale.US);
    	bdateTimeFormat = new SimpleDateFormat(B_DATE_TIME_FORMAT, Locale.US);
    	adateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    	bdateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    	mDive = new Dive(ctx);
	    mDive.open();
	    mDive.rowID= 0;
	    mSite = new DiveSite(ctx);
	    mSite.open();
	    mSite.rowID = 0;
	    mPerson = new Person(ctx);
	    mPerson.open();
	    mPerson.rowID = 0;
	    mCity = new City(ctx);
	    mCity.open();
	    mCountry = new Country(ctx);
	    mCountry.open();
	}
	
	public void open(String path) {
		mDB = ctx.openOrCreateDatabase(path,0,null);
	}
	
	public void sync () {
		new syncDive().execute();
		
		return;
	}
	private class syncDive extends AsyncTask<Void, Void, boolean[]> {
	   	
		
		/** The system calls this to perform work in a worker thread and
	      * delivers it the parameters given to AsyncTask.execute() 
		 * @return */
	    protected boolean[] doInBackground(Void... voids) {

	    	//
	    	// sync Place to Site
	    	//
	    	Cursor bPlace = mDB.query("place", null, null, null, null, null, B_KEY_UUID);
			bPlace.moveToFirst();
			Cursor aSite = mSite.fetchUpdate();
			aSite.moveToFirst();
			Log.d(TAG, "starting Dive Site Sync");
			UuidJoin(mSite, aSite,bPlace,DiveSite.SITE_ALL_KEYS,B_SITE_ALL_KEYS, "Dive site");
			Log.d(TAG, "Dive Site Sync complete");
			aSite.close();
			bPlace.close();
			//
			//sync City to City
			//
			Cursor bCity = mDB.query("city", null, null, null, null, null, B_KEY_UUID);
			bCity.moveToFirst();
			Cursor aCity=mCity.fetchUpdate();
			aCity.moveToFirst();
			Log.d(TAG, "starting Location Sync");
			UuidJoin(mCity, aCity,bCity,City.CITY_ALL_KEYS,B_CITY_ALL_KEYS, "Location");
			Log.d(TAG, "Location Sync Complete");
			aCity.close();
			bCity.close();
			//
			// sync countries
			//
			Cursor bCountry = mDB.query("country", null, null, null, null, null, B_KEY_UUID);
			bCountry.moveToFirst();
			Cursor aCountry=mCountry.fetchUpdate();
			aCountry.moveToFirst();
			Log.d(TAG, "starting Country Sync");
			UuidJoin(mCountry, aCountry,bCountry,Country.COUNTRY_ALL_KEYS,B_COUNTRY_ALL_KEYS, "Country");
			Log.d(TAG, "Country Sync Complete");
			aCountry.close();
			bCountry.close();
			//
			// sync Person to Buddy
			//
			Cursor bPerson = mDB.query("buddy", null, null, null, null, null, B_KEY_UUID);
			bPerson.moveToFirst();
			Cursor aPerson=mPerson.fetchUpdate();
			aPerson.moveToFirst();
			Log.d(TAG, "starting Person Sync");
			UuidJoin(mPerson, aPerson,bPerson,Person.PERSON_ALL_KEYS,B_PERSON_ALL_KEYS, "Person");
			Log.d(TAG, "Person Sync Complete");
			aCountry.close();
			bCountry.close();
			//
			// sync logbook entries
			//
			Cursor bLogs = mDB.query("logbook", null, null, null, null, null, B_KEY_UUID);
			bLogs.moveToFirst();
			Cursor aLogs=mDive.fetchUpdate();
			aLogs.moveToFirst();
			Log.d(TAG, "starting DiveLOg Sync");
			UuidJoin(mDive, aLogs,bLogs,Dive.DIVE_ALL_KEYS,B_DIVE_ALL_KEYS, "Divelog");
			Log.d(TAG, "DiveLog Sync Complete");
			aLogs.close();
			bLogs.close();
			
		return new boolean[]{true};
					
	    }

		
	    protected void onPostExecute(boolean[] res) {
	    	
	    
	 // Create a Notification//
	 		NotificationManager mgr = (NotificationManager) ctx.getSystemService("notification");
	 						
	 		
	 		Notification noti =  new NotificationCompat.Builder(ctx)
	 			.setAutoCancel(true)
	 			.setContentTitle(ctx.getString(R.string.notify_sync_title))
	 			.setContentText(ctx.getString(R.string.notify_sync_message))
	 			.setSmallIcon(R.drawable.android_divelog)
	 			.getNotification();
	 		 
	 		
	 		// An issue could occur if user ever enters over 2,147,483,647 tasks. (Max int value). 
	 		// I highly doubt this will ever happen. But is good to note. 
	 	
	 			mgr.notify(ctx.getString(R.string.sync_notification_dives),10, noti); 
	    }
	    private boolean UuidJoin(DiveDbAdapter obj, Cursor aObject, Cursor bObject, String[] akeys, String[] bkeys, String tag) {
	    	try {
	    		ContentValues args = new ContentValues();
				CursorJoiner joiner = new CursorJoiner(aObject, new String[] {Dive.KEY_UUID}, bObject, new String[] {B_KEY_UUID});
				for (CursorJoiner.Result joinerResult : joiner) {
					args.clear();
					
					
					switch (joinerResult) {
		    	         case LEFT:
		    	             // handle case where a row in cursorA is unique
		    	        	 auuid = aObject.getString(aObject.getColumnIndexOrThrow(Dive.KEY_UUID));
		    	        	 Log.d(TAG, "creating in b database " +tag+ "-" + auuid);
		    	             break;
		    	         case RIGHT:
		    	             // handle case where a row in cursorB is unique
		    	        	 buuid = bObject.getString(bObject.getColumnIndexOrThrow(B_KEY_UUID));
		    	        	 bupdated = bObject.getString(bObject.getColumnIndexOrThrow(B_KEY_UPDATE_DATE));
							 bdate = bdateTimeFormat.parse(bupdated);
		    	        	 Log.d(TAG, "creating in a database " +tag+ "-" + buuid);
		    	        	 args.put(Dive.KEY_UUID, buuid);
		    	        	 obj.create(args);
		    	        	 args.clear();
		    	        	 for (int i = 1 ; i < akeys.length; i++) {
									if (bkeys[i] != null ) {
										args.put( akeys[i], bObject.getString(bObject.getColumnIndexOrThrow(bkeys[i])));
	
									}
		    	        	 }
							obj.update(args,  adateTimeFormat.format(bdate))	; 
		    	            break;
		    	         case BOTH:
		    	             // handle case where a row with the same key is in both cursors
		    	        	 //
		    	        	 auuid = aObject.getString(aObject.getColumnIndexOrThrow(Dive.KEY_UUID));
		    	        	 buuid = bObject.getString(bObject.getColumnIndexOrThrow(B_KEY_UUID));
		    	        	 aupdated = aObject.getString(aObject.getColumnIndexOrThrow(Dive.KEY_UPDATE_DATE));
		    	        	 bupdated = bObject.getString(bObject.getColumnIndexOrThrow(B_KEY_UPDATE_DATE));
							 adate = adateTimeFormat.parse(aupdated);
							 bdate = bdateTimeFormat.parse(bupdated);
							 int cp = adate.compareTo(bdate);
							 if (cp < 0) {
							 
								 Log.d(TAG, "updating" +  tag + " entry in a database" + auuid);
								 for (int i = 1 ; i < akeys.length; i++) {
									if (bkeys[i] != null ) {
										args.put( akeys[i], bObject.getString(bObject.getColumnIndexOrThrow(bkeys[i])));
	
									}
									
									obj.findByUuid(auuid);
									obj.update(args,  adateTimeFormat.format(bdate))	;
								 }
							 } else if (cp >0) {
							
								
									Log.d(TAG, "updating" + tag + " in b database" + auuid);
							 } else {
								    Log.d(TAG, "no change to " + tag + " - " + auuid);
							 }
					
	    	     }
	    	 }
	    	} catch (ParseException e) {
				Log.e(TAG, e.getMessage(), e); 
			} catch (IllegalArgumentException e) {
				Log.e(TAG, e.getMessage(), e); 
			} catch (SQLException e) {
				Log.e(TAG, e.getMessage(), e); 
			}
	    	 return true;
	    	
	    }
	    
	}
	

	
	
}

