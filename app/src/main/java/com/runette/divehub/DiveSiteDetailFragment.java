package com.runette.divehub;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;


public class DiveSiteDetailFragment extends Fragment {

    
   

	private int delete_state = DiveSite.DELETED_NO;
	 //private long mID = 0;
	private EditText mSiteName;
	private EditText mSiteLocation;
	private EditText mSiteCountry;
	//
	// Data manipulation objects
    //
    private DiveSite mDiveSite ;

    public DiveSiteDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); 
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_divesite_detail, container, false);
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
     	super.onActivityCreated(savedInstanceState);
     	//
     	// at this point - the layout has settled and the data objects can be instatiated
     	// and linked to listeners
     	//      
         mSiteName = (EditText) getActivity().findViewById(R.id.sitename);
         mSiteLocation = (EditText) getActivity().findViewById(R.id.sitelocation);
         mSiteCountry = (EditText) getActivity().findViewById(R.id.sitecountry);
         //
         // link to and open the db
         //
         mDiveSite = new DiveSite(getActivity());
         mDiveSite.open();

         //
         // get the relevant site by ID passed in from the parent activity using args.
         // if this is a new site to be created then NEW_SITE_FLAG will be passed in 
         //
         if (getArguments().containsKey(DiveSite.ARG_ITEM_ID)) {
             mDiveSite.rowID = getArguments().getLong(DiveSite.ARG_ITEM_ID);
         }
         
             
         if (mDiveSite.rowID == DiveSite.NEW_SITE_FLAG) {
         	//
         	// the request is to open a new site
         	//
         	createSite();
         } 
         
    }
         @Override
         public void onResume() {
             super.onResume();
            // mDbHelper.open(); 
             updateScreen();
         }
         private void updateScreen() {
        	 Cursor mySite;
             mySite = mDiveSite.fetch() ;
             populateFields (mySite);
         }
         @Override
         public void onPause() {
             super.onPause();
             saveSite();
             // mDbHelper.close(); 
         }
 
    private  void populateFields(Cursor mySite) {
		//
		// Copy data from the currsor into the layout
		//
		mSiteName.setText(mySite.getString(mySite.getColumnIndex(DiveSite.KEY_SITE_NAME)));
        mSiteLocation.setText(mySite.getString(mySite.getColumnIndex(DiveSite.KEY_SITE_LOCATION)));
        mSiteCountry.setText(mySite.getString(mySite.getColumnIndex(DiveSite.KEY_SITE_COUNTRY)));
    }
    private long saveSite () {
		//
		// Persists the current site state through the mDbHelper adapter
		//
    	if (delete_state == DiveSite.DELETED_NO) {
			if (mSiteName.getText() != null) mDiveSite.update( DiveSite.KEY_SITE_NAME, mSiteName.getText().toString()) ;
			if (mSiteLocation.getText() != null) mDiveSite.update( DiveSite.KEY_SITE_LOCATION,  mSiteLocation.getText().toString()) ;
			if (mSiteCountry.getText() != null) mDiveSite.update( DiveSite.KEY_SITE_COUNTRY,  mSiteCountry.getText().toString()) ;
    	};
		return 1 ;
    }
    private long createSite () {
		//
		// creates empty site record
		// returns the site record ID or -1 for a failure
		//
		long ID = -1;
		String name = "";
		ID = mDiveSite.create(name);
		return ID;
	}
	//
    // set up and act upon the menu
    //
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater mi) {
    	super.onCreateOptionsMenu(menu, mi);
        mi.inflate(R.menu.detail_menu, menu); 
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.menu_accept :
        	getActivity().finish();
            return true; 
        case R.id.menu_cancel:
        	SiteDeleteDialog mD = new SiteDeleteDialog();
            mD.show(getActivity().getFragmentManager(), "delete_site");
            return true;
        case R.id.menu_previous :
    		saveSite();
        	mDiveSite.rowID = mDiveSite.getPrevious();
    		updateScreen();
            return true; 
        case R.id.menu_next :
        	saveSite();
    		mDiveSite.rowID = mDiveSite.getNext();
    		updateScreen();
            return true; 
        }    
        return super.onOptionsItemSelected(item);
    }
    public void deleteSite() {
    	mDiveSite.delete();
    	delete_state = DiveSite.DELETED_YES;
    	getActivity().finish();
    }
    
    
}
