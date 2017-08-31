package com.runette.divehub;

import java.util.Calendar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class PersonDetailActivity extends FragmentActivity 
implements DialogReturn {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            long mID = getIntent().getLongExtra(Person.ARG_ITEM_ID, -65555 );
            if ( mID != -65555 ){
            	arguments.putLong(Person.ARG_ITEM_ID, mID);
            };
            PersonDetailFragment fragment = new PersonDetailFragment();
                        fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.person_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, PersonListActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

	public void setDate(Calendar cal) {
		
	}

	public void setTime(Calendar cal) {
		
	}

	public void deleteDive() {
		
	}

	public void deleteSite() {
		
	}

	public void deletePerson() {
		PersonDetailFragment fragment = (PersonDetailFragment) getSupportFragmentManager().findFragmentById(R.id.person_detail_container);
    	fragment.deletePerson();
	}

	public void invalidate() {
		// TODO Auto-generated method stub
		
	}
}
