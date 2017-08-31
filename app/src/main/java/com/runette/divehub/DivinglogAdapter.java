package com.runette.divehub;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.TokenPair;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import android.widget.Toast;

public class DivinglogAdapter extends Activity
implements DropBoxLoader.mCallback
{
	//
	//DEPRECATED
	//
	
	
	private static final String TAG = "DivinglogAdapter";
	public static final String SYNCH_FILE = "divinglogsynch";
	public static final String DB_PATH = "/data/data/com.runette.divehub/";
	public static final String SOURCE = "Divelog/";
	public static final String FILE1 = "DivelogSync.xml";
	//public static final String FILE2 = "NewLogbook2.sql";
	public DropBoxLoader loader;
	public String dbname;
	
	//
	// Database Column names - the B string matches columnwised the appropriate ALL_KEYS string from DiveDbAdapter
	//
	
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
		"Number",
		"Divedate",
		"Entrytime",
		"Divetime",
		"Depth",
		"Place",
		"PlaceID",
		"Buddy",
		"BuddyIDs",
		"Comments",
		"Signature",
		"Divemaster",
		null
	};
	
	 private static final String B_KEY_SITE_NAME = "Place";
	 private static final String B_KEY_SITE_LOCATION = "City";
	 private static final String B_KEY_SITE_COUNTRY = "Country";
	 private static final String B_KEY_SITE_LAT = "Lat";
	 private static final String B_KEY_SITE_LON = "Lon";
				
	 private static final String B_KEY_PERSON_FIRSTNAME = "FirstName";
	 private static final String B_KEY_PERSON_LASTNAME = "LastName";
	 private static final String B_KEY_PERSON_PHONE = "Phone";
	 private static final String B_KEY_PERSON_MOBILE = "Mobile";
	 private static final String B_KEY_PERSON_EMAIL = "Email";
			

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = new DropBoxLoader(this);
        loader.getSession().startAuthentication(this);
        dbname = null;
        adateTimeFormat = new SimpleDateFormat(DiveDbAdapter.SYSTEM_TIME, Locale.US);
    	bdateTimeFormat = new SimpleDateFormat(B_DATE_TIME_FORMAT, Locale.US);
    	adateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    	bdateTimeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    
    @Override
    protected void onResume() {
        super.onResume();
        AndroidAuthSession session = loader.getSession();

        // The next part must be inserted in the onResume() method of the
        // activity from which session.startAuthentication() was called, so
        // that Dropbox authentication completes properly.
        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                // Store it locally in our app for later use
                TokenPair tokens = session.getAccessTokenPair();
                loader.storeKeys(tokens.key, tokens.secret);
                loader.setLoggedIn(true);
                download();
            } catch (IllegalStateException e) {
                showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
                Log.i(TAG, "Error authenticating", e);
            }
        }
    }
    
    
    public void download(){
    	loader.fetchFile(SOURCE + FILE1, DB_PATH + FILE1);
    	
    	
    }
    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }


	public void actionFinished(DropboxFileInfo inf) {
		String name = inf.getMetadata().fileName();
				
		if (name.equalsIgnoreCase(FILE1)) {
			XmlParse pr = new XmlParse(DB_PATH + name);
			dbname = pr.filename;
			String UUID = pr.fileUUID;
			String MD5 = pr.MD5;
			loader.fetchFile(SOURCE + dbname, DB_PATH + dbname);
		} else if (name.equalsIgnoreCase(dbname)) {
			sync(DB_PATH + dbname);
			finish();
		} 
		
	}

	private void sync (String path) {
		SQLiteDatabase db = openOrCreateDatabase(path,0,null);
		Cursor bLogs = db.query("logbook", null, null, null, null, null, null);
		bLogs.moveToFirst();
		Dive mDive = new Dive(this);
	    mDive.open();
	    Cursor aLogs  = mDive.fetchUpdate();
	    String auuid;
	    String buuid;
	    String aupdated;
	    String bupdated;
	    Date adate;
	    Date bdate;

			for (int bi = 0; bi < bLogs.getCount(); bi++) {
				aLogs.moveToFirst();
				try {
					buuid = bLogs.getString(bLogs.getColumnIndexOrThrow(B_KEY_UUID));
					boolean present = false;
					for (int ai = 0; ai < aLogs.getCount(); ai++) {
						mDive.rowID = aLogs.getLong(aLogs.getColumnIndexOrThrow(Dive.KEY_ROWID));
						auuid = aLogs.getString(aLogs.getColumnIndexOrThrow(Dive.KEY_UUID));
							if (auuid.equalsIgnoreCase(buuid)) {
								present = true;
								aupdated = aLogs.getString(aLogs.getColumnIndexOrThrow(Dive.KEY_UPDATE_DATE));
								bupdated = bLogs.getString(bLogs.getColumnIndexOrThrow(B_KEY_UPDATE_DATE));
								adate = adateTimeFormat.parse(aupdated);
								bdate = bdateTimeFormat.parse(bupdated);
								int cp = adate.compareTo(bdate);
								if (cp <= 0) {
									for (int i = 1 ; i < Dive.DIVE_ALL_KEYS.length; i++) {
										if (B_DIVE_ALL_KEYS[i] != null ) {
											mDive.update( Dive.DIVE_ALL_KEYS[i], bLogs.getString(bLogs.getColumnIndexOrThrow(B_DIVE_ALL_KEYS[i])));
										}
									} 
										
									} else {
									
									
								}
							}
						aLogs.moveToNext();	
					} 
					if (! present) {
						mDive.rowID = mDive.create(bLogs.getInt(bLogs.getColumnIndexOrThrow(B_KEY_NUMBER)),bLogs.getString(bLogs.getColumnIndexOrThrow(B_KEY_DATE)),bLogs.getString(bLogs.getColumnIndexOrThrow(B_KEY_TIME)));
						mDive.update( Dive.KEY_UUID, bLogs.getString(bLogs.getColumnIndexOrThrow(B_KEY_UUID)));
						for (int i = 4 ; i < Dive.DIVE_ALL_KEYS.length; i++) {
							if (B_DIVE_ALL_KEYS[i] != null ) {
								mDive.update( Dive.DIVE_ALL_KEYS[i], bLogs.getString(bLogs.getColumnIndexOrThrow(B_DIVE_ALL_KEYS[i])));
							}
						}
					}
				}
				catch (ParseException e) {
							Log.e(TAG, e.getMessage(), e); 
						}
				catch (IllegalArgumentException e) {
					Log.e(TAG, e.getMessage(), e); 
				}
				bLogs.moveToNext();
				} 
					
				
				
					
				
			
		
	
		return;
	}
	
    
}
