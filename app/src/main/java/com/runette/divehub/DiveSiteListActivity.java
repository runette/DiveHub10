package com.runette.divehub;



import android.support.v4.app.FragmentActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;

import android.view.MenuItem;

public class DiveSiteListActivity extends FragmentActivity
        implements DiveSiteListFragment.Callbacks  {

    private boolean mTwoPane;
    private long mID = 0;
    private String mName = null;
    public Intent result = new Intent();
    
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_divesite_list);
        getActionBar().setDisplayHomeAsUpEnabled(false);

        Bundle in = getIntent().getExtras();
       
        if ( in != null ) {
        	mID = in.getLong(DiveSite.ARG_ITEM_ID, -6555);
        	mName = in.getString(DiveSite.ARG_ITEM_NAME);
        }
        
        Bundle arguments = new Bundle();
        
        if ( mID > 0 ){
        	arguments.putLong(DiveSite.ARG_ITEM_ID, mID);
        	arguments.putString(DiveSite.ARG_ITEM_NAME, mName);
        };
        
                    		
        
        if (findViewById(R.id.divesite_detail_container) != null) {
            mTwoPane = true;
        }
        
        DiveSiteListFragment fragment = new DiveSiteListFragment();
        fragment.setArguments(arguments);
        FragmentManager fm= getSupportFragmentManager();
        fm.beginTransaction()
        	.add(R.id.divesite_list, fragment)
        	.commit();
        
        //fragment.setActivateOnItemClick(true);
        
    }

   
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case R.id.menu_insert: 
            createSite();
            return true; 
        case R.id.menu_settings: 
        	Intent i = new Intent(this, TaskPreferences.class); 
        	startActivity(i); 
            return true;
        }    
        return super.onMenuItemSelected(featureId, item);
    }
    //
	// create new detail pane
	// either pass ID as an arg to new detail fragment 
	// or create a new detail ativity and use an intent to pass the id
	//
    private void newDetail (long id) {
	    		if (mTwoPane) {
	        Bundle arguments = new Bundle();
			arguments.putLong(DiveSite.ARG_ITEM_ID, id);
	        DiveSiteDetailFragment fragment = new DiveSiteDetailFragment();
	        fragment.setArguments(arguments);
	        getSupportFragmentManager().beginTransaction()
	                .replace(R.id.divesite_detail_container, fragment)
	                .commit();
	
	    } else {
	        Intent detailIntent = new Intent(this, DiveSiteDetailActivity.class);
	        detailIntent.putExtra(DiveSite.ARG_ITEM_ID, id);
	        startActivityForResult(detailIntent, 1);
	    }
    }
    


	//
    // called when user requests a new dive record
    // sets ID to NEW_DIVE_FLAG and then passes control as per
    //
    private void createSite() {
    	long id = DiveSite.NEW_SITE_FLAG ;
    	newDetail(id);
    }

	

	//@Override
	public void onItemAccepted(long id) {
		result.putExtra(DiveSite.ARG_ITEM_ID, id) ;
        setResult(android.app.Activity.RESULT_OK, result);
	}

	public void onItemEdited(long id) {
		newDetail(id);
	}

	public void onItemSelected(long id) {
		mID = id; 
        
	}
	
	public void onItemAdded() {
		createSite();
	}
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	      
         if (resultCode == RESULT_OK) {
             // A site was edited.  
             long id = data.getLongExtra(DiveSite.ARG_ITEM_ID,-1);
             if (id != -1 ) mID = id;
         }
    
 }
}
 
