package com.runette.divehub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

public class PersonListActivity extends FragmentActivity
        implements PersonListFragment.Callbacks {

    private boolean mTwoPane;
    private long mID = 0;
    private String mName = null;
    public Intent result = new Intent();
    
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_person_list);
        getActionBar().setDisplayHomeAsUpEnabled(false);

        Bundle in = getIntent().getExtras();
       
        if ( in != null ) {
        	mID = in.getLong(Person.ARG_ITEM_ID, -6555);
        	mName = in.getString(Person.ARG_ITEM_NAME);
        }
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            
            if ( mID > 0 ){
            	arguments.putLong(Person.ARG_ITEM_ID, mID);
            	arguments.putString(Person.ARG_ITEM_NAME, mName);
            };
            PersonListFragment fragment = new PersonListFragment();
                        fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.person_list, fragment)
                    .commit();
        }
        if (findViewById(R.id.person_detail_container) != null) {
            mTwoPane = true;
            ((PersonListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.person_list))
                    .setActivateOnItemClick(true);
        }
    }

   
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case R.id.menu_insert: 
            createPerson();
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
			arguments.putLong(Person.ARG_ITEM_ID, id);
	        PersonDetailFragment fragment = new PersonDetailFragment();
	        fragment.setArguments(arguments);
	        getSupportFragmentManager().beginTransaction()
	                .replace(R.id.person_detail_container, fragment)
	                .commit();
	
	    } else {
	        Intent detailIntent = new Intent(this, PersonDetailActivity.class);
	        detailIntent.putExtra(Person.ARG_ITEM_ID, id);
	        startActivityForResult(detailIntent, 1);
	    }
    }
    


	//
    // called when user requests a new dive record
    // sets ID to NEW_DIVE_FLAG and then passes control as per
    //
    private void createPerson() {
    	long id = Person.NEW_PERSON_FLAG ;
    	newDetail(id);
    }

	

	//@Override
	public void onItemAccepted(long id) {
		result.putExtra(Person.ARG_ITEM_ID, id) ;
        setResult(android.app.Activity.RESULT_OK, result);
	}

	public void onItemEdited(long id) {
		newDetail(id);
	}

	public void onItemSelected(long id) {
		mID = id; 
        
	}
	
	public void onItemAdded() {
		createPerson();
	}
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	      
         if (resultCode == RESULT_OK) {
             // A site was edited.  
             long id = data.getLongExtra(Person.ARG_ITEM_ID,-1);
             if (id != -1 ) mID = id;
         }
    
 }

}