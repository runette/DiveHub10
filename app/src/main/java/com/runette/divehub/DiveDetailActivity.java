package com.runette.divehub;

import java.util.Calendar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class DiveDetailActivity 
extends FragmentActivity 
implements DialogReturn {
	
	long mID;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.runette.divehub.R.layout.activity_dive_detail);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            mID = getIntent().getLongExtra(Dive.ARG_ITEM_ID, -2 );
            if ( mID != -2 ){
            	arguments.putLong(Dive.ARG_ITEM_ID, mID);
            };
            DiveDetailFragment fragment = new DiveDetailFragment();
                        fragment.setArguments(arguments);
           getSupportFragmentManager().beginTransaction()
                    .add(R.id.dive_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, DiveListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                // A site was picked.  
            	switch (requestCode - 0x10000) {
            	case Person.PICK_SITE_REQUEST :
            		long id = data.getLongExtra(DiveSite.ARG_ITEM_ID,DiveSite.NO_ID_FLAG);
            		if (id != DiveSite.NO_ID_FLAG ) onSiteSelected(id);
            		return;
            	case Person.PICK_BUDDY_REQUEST :
            		id	 = data.getLongExtra(Person.ARG_ITEM_ID,Person.NO_ID_FLAG);
            		if (id != Person.NO_ID_FLAG ) onBuddySelected(id);
            		return;
            	case Person.PICK_DM_REQUEST :
            		id = data.getLongExtra(Person.ARG_ITEM_ID,Person.NO_ID_FLAG);
            		if (id != Person.NO_ID_FLAG ) onDMSelected(id);
            	}
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
		// TODO Auto-generated method stub
		
	}
    
}
