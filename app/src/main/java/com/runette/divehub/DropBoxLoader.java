package com.runette.divehub;
/*
 * Copyright (c) 2010-11 Dropbox, Inc.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.os.AsyncTask;

import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;

import com.dropbox.client2.android.AndroidAuthSession;

import com.dropbox.client2.exception.DropboxException;

import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;



public class DropBoxLoader  {
    private static final String TAG = "DropBoxLoader";

    ///////////////////////////////////////////////////////////////////////////
    //                      Your app-specific settings.                      //
    ///////////////////////////////////////////////////////////////////////////

    // Replace this with your app key and secret assigned by Dropbox.
    // Note that this is a really insecure way to do this, and you shouldn't
    // ship code which contains your key & secret in such an obvious way.
    // Obfuscation is good.
    private final static String APP_KEY = "77q1mlb879j8uja";
	private final static String APP_SECRET = "ddfnbiusjlhyabr";

    // If you'd like to change the access type to the full Dropbox instead of
    // an app folder, change this value.
    final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;

    ///////////////////////////////////////////////////////////////////////////
    //                      End app-specific settings.                       //
    ///////////////////////////////////////////////////////////////////////////

    // You don't need to change these, leave them alone.
    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";


    private DropboxAPI<AndroidAuthSession> mApi;
    private Context mctx;

    private boolean mLoggedIn;
    public interface mCallback {
    	public void actionFinished (DropboxFileInfo inf);
    }
    

    public DropBoxLoader(Context ctx) {
    	mctx=ctx;	
    	AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
    	
    	return;
    }

   public AndroidAuthSession getSession() {
	   return mApi.getSession();
   }
    
  public void action(DropboxFileInfo inf) {
	  mCallback cb = (mCallback) mctx;
	  cb.actionFinished(inf);
  }
    

    public void logOut() {
        // Remove credentials from the session
        mApi.getSession().unlink();

        // Clear our stored keys
        clearKeys();
        // Change UI state to display logged out version
        setLoggedIn(false);
    }

    /**
     * Convenience function to change UI state based on being logged in
     */
    public void setLoggedIn(boolean loggedIn) {
    	mLoggedIn = loggedIn;
    	
    }
    public boolean isLoggedIn () {
    	return mLoggedIn;
    }



    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     *
     * @return Array of [access_key, access_secret], or null if none stored
     */
    public String[] getKeys() {
        SharedPreferences prefs = mctx.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
        	String[] ret = new String[2];
        	ret[0] = key;
        	ret[1] = secret;
        	return ret;
        } else {
        	return null;
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    public void storeKeys(String key, String secret) {
        // Save the access key for later
        SharedPreferences prefs = mctx.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(ACCESS_KEY_NAME, key);
        edit.putString(ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    public void clearKeys() {
        SharedPreferences prefs = mctx.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session;

        String[] stored = getKeys();
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        return session;
    }
    public void createSession () {
    	 
    	
    }
    
    public void fetchFile (String from, String to) {
    	
    	if (mLoggedIn){
    	    new fetchFileTask().execute(new String[] {from,to});
    	}
    }
    	private class fetchFileTask extends AsyncTask<String, Void, DropboxFileInfo> {
    	
    		
    		/** The system calls this to perform work in a worker thread and
    	      * delivers it the parameters given to AsyncTask.execute() */
    	    protected DropboxFileInfo doInBackground(String... paths) {
    	    	FileOutputStream outputStream = null;
    	    	DropboxFileInfo info = null  ;
    	    	Log.i(TAG, "Starting download of: " + paths[0]);
    	    	try {
    	    		String from = paths[0] ;
    	    		String to = paths[1];
    	    	    File file = new File(to);
    	    	    outputStream = new FileOutputStream(file);
    	    	    info = mApi.getFile(from, null, outputStream, null);
    	    	    Log.i(TAG, "The file's rev is: " + info.getMetadata().rev);
    	    	    // /path/to/new/file.txt now has stuff in it.
    	    	} catch (DropboxException e) {
    	    	    Log.e(TAG, "Something went wrong while downloading." + e.toString());
    	    	} catch (FileNotFoundException e) {
    	    	    Log.e(TAG, "File not found.");
    	    	} finally {
    	    	    if (outputStream != null) {
    	    	        try {
    	    	            outputStream.close();
    	    	        } catch (IOException e) {}
    	    	    }
    	    	   
    	    	}
    	    	 return info;
    	    }
    	    
    	    /** The system calls this to perform work in the UI thread and delivers
    	      * the result from doInBackground() */
    	    protected void onPostExecute(DropboxFileInfo info) {
    	    	action(info);
    	    }
    	}
    	
    	

    	
    	public void putFile( String from, String to) {
    		
    	    }
}
