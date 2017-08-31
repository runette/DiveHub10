package com.runette.divehub;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

public class SignatureActivity extends Activity {
	long mID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            mID = getIntent().getLongExtra(Dive.ARG_ITEM_ID, -2 );
            if ( mID != -2 ){
            	arguments.putLong(Dive.ARG_ITEM_ID, mID);
            ;
        }
        SignatureFragment fragment = new SignatureFragment();
        fragment.setArguments(arguments);
        getFragmentManager().beginTransaction()
        	.add(R.id.signature_frame, fragment)
        	.commit();
        }
        return;
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.signature_menu, menu);
       
        return true;
    }
    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
    	SignatureFragment fragment = (SignatureFragment) getFragmentManager().findFragmentById(R.id.signature_frame);
    	switch(item.getItemId()) {
        case R.id.menu_signature_accept: 
        	fragment.put();
	    	finish() ;
	    	return true;
        case R.id.menu_signature_cancel:
        	fragment.clear();
        	return true;
    	}
    	return super.onOptionsItemSelected(item);
    }
    
}
