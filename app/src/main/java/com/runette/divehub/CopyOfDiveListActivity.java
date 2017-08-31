package com.runette.divehub;



import java.util.Calendar;

import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.TokenPair;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class CopyOfDiveListActivity extends FragmentActivity
        implements 	DiveListFragment.Callbacks, 
        			DialogReturn,
        			DropBoxLoader.mCallback
        {

    private boolean mTwoPane;
    public DropBoxLoader loader;
	public String dbname;
	private static final String TAG = "DiveListActivity";
     

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dive_list);
        
        
       // set mTwoPane to show what sort of display we have
        if (findViewById(R.id.dive_detail_container) != null) {
            mTwoPane = true;
            ((DiveListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.dive_list_container))
                    .setActivateOnItemClick(true);
        } else {
        	FragmentManager fm = getSupportFragmentManager();
        	DiveDetailFragment detailfrag = (DiveDetailFragment) fm.findFragmentById(R.id.dive_detail_container);
        	if (detailfrag != null) {
	        	fm.beginTransaction()
	        		.detach(detailfrag)
	        		.commit();
        	}
        }
        loader = new DropBoxLoader(this);
        dbname = null;
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
                if (! loader.isLoggedIn()) {
	                TokenPair tokens = session.getAccessTokenPair();
	                loader.storeKeys(tokens.key, tokens.secret);
	                loader.setLoggedIn(true);
	                loader.fetchFile(Divelog.SOURCE + Divelog.FILE1, Divelog.DB_PATH + Divelog.FILE1);
                }
            } catch (IllegalStateException e) {
                showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
                Log.i(TAG, "Error authenticating", e);
            }
        }
    }
    
    // DONT KNOW WAHT THIS IS YET
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected(long id) {
    	//
    	// callback from fragment when item of id is clicked
    	// create new detail pane
    	//
    	newDetail(id);
    }
    //
    // set up and act upon the menu
    //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.list_menu, menu); 
        return true;
    }
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case R.id.menu_sync: 
            syncDive();
            return true; 
        case R.id.menu_insert: 
            createDive();
            return true; 
        case R.id.menu_settings: 
        	Intent i = new Intent(this, TaskPreferences.class); 
        	startActivity(i); 
            return true;
        }    
        return super.onMenuItemSelected(featureId, item);
    }
	private void syncDive() {
		if (! loader.isLoggedIn()) {
		loader.getSession().startAuthentication(this);
		} else {
			loader.fetchFile(Divelog.SOURCE + Divelog.FILE1, Divelog.DB_PATH + Divelog.FILE1);
		}
		
	}
	//
    // menu for when longpress - TODO needs to go to fragment
    //
    
	//
    // called when user requests a new dive record
    // sets ID to NEW_DIVE_FLAG and then passes control as per
    //
    private void createDive() {
    	long id = Dive.NEW_DIVE_FLAG ;
    	newDetail(id);
    }
    //
    // callback from DatePickerFragment to set new date
    //
   
    private void newDetail (long id) {
	    		if (mTwoPane) {
	        Bundle arguments = new Bundle();
	        arguments.putLong(Dive.ARG_ITEM_ID, id);
	        arguments.putBoolean(Dive.DISPLAY, mTwoPane);
	        DiveDetailFragment fragment = new DiveDetailFragment();
	        fragment.setArguments(arguments);
	        getSupportFragmentManager().beginTransaction()
	                .replace(R.id.dive_detail_container, fragment)
	                .commit();
	
	    } else {
	        Intent detailIntent = new Intent(this, DiveDetailActivity.class);
	        detailIntent.putExtra(Dive.ARG_ITEM_ID, id);
	        startActivity(detailIntent);
	    }
    }
   
    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }


	public void actionFinished(DropboxFileInfo inf) {
		try {
		String name = inf.getMetadata().fileName();
		if (name.equalsIgnoreCase(Divelog.FILE1)) {
			XmlParse pr = new XmlParse(Divelog.DB_PATH + name);
			dbname = pr.filename;
			String UUID = pr.fileUUID;
			String MD5 = pr.MD5;
			loader.fetchFile(Divelog.SOURCE + dbname, Divelog.DB_PATH + dbname);
		} else if (name.equalsIgnoreCase(dbname)) {
			Divelog adapter = new Divelog(this);
			adapter.open(Divelog.DB_PATH + dbname);
			adapter.sync();
		} 
		} finally {
			
		}
		
	}

	public void setDate(Calendar cal) {
    	DiveDetailFragment fragment = (DiveDetailFragment) getSupportFragmentManager().findFragmentById(R.id.dive_detail_container);
    	fragment.setDate(cal);	
    }
    
    public void setTime(Calendar cal){
    	DiveDetailFragment fragment = (DiveDetailFragment) getSupportFragmentManager().findFragmentById(R.id.dive_detail_container);
    	fragment.setTime(cal);
    }

	public void onSiteSelected(long id) {
		DiveDetailFragment fragment = (DiveDetailFragment) getSupportFragmentManager().findFragmentById(R.id.dive_detail_container);
    	fragment.setSite(id);
	}
	
	public void onBuddySelected(long id){
		DiveDetailFragment fragment = (DiveDetailFragment) getSupportFragmentManager().findFragmentById(R.id.dive_detail_container);
    	fragment.setBuddy(id);
	}
	public void onDMSelected(long id){
		DiveDetailFragment fragment = (DiveDetailFragment) getSupportFragmentManager().findFragmentById(R.id.dive_detail_container);
    	fragment.setDiveMaster(id);
	}
	
	public void deleteDive() {
		// call delete
				DiveDetailFragment fragment = (DiveDetailFragment) getSupportFragmentManager().findFragmentById(R.id.dive_detail_container);
		    	fragment.deleteDive();
	}

	public void deleteSite() {
		
	}

	public void deletePerson() {
		
	}

	public void invalidate() {
		((DiveListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.dive_list_container))
                .invalidate();
	}
}
