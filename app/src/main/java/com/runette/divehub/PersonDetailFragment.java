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


public class PersonDetailFragment extends Fragment {

	private int delete_state = Person.DELETED_NO;
	private EditText mFirstName;
	private EditText mLastName;
	private EditText mPhone;
	private EditText mMobile;
	private EditText mEmail;
	//
	// Data manipulation objects
    //
    private Person mPerson ;

    public PersonDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); 
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_person_detail, container, false);
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
     	super.onActivityCreated(savedInstanceState);
     	//
     	// at this point - the layout has settled and the data objects can be instatiated
     	// and linked to listeners
     	//      
         mFirstName = (EditText) getActivity().findViewById(R.id.personfirstname);
         mLastName = (EditText) getActivity().findViewById(R.id.personlastname);
         mPhone = (EditText) getActivity().findViewById(R.id.personphone);
         mMobile = (EditText) getActivity().findViewById(R.id.personmobile);
         mEmail = (EditText) getActivity().findViewById(R.id.personemail);
         //
         // link to and open the db
         //
         mPerson = new Person(getActivity());
         mPerson.open();

         //
         // get the relevant site by ID passed in from the parent activity using args.
         // if this is a new site to be created then NEW_SITE_FLAG will be passed in 
         //
         if (getArguments().containsKey(Person.ARG_ITEM_ID)) {
             mPerson.rowID = getArguments().getLong(Person.ARG_ITEM_ID);
         }
         
             
         if (mPerson.rowID == Person.NEW_PERSON_FLAG) {
         	//
         	// the request is to open a new site
         	//
         	createPerson();
         } 
         
    }
         @Override
         public void onResume() {
             super.onResume();
            // mDbHelper.open(); 
             updateScreen();
         }
         private void updateScreen(){
        	 Cursor myPerson;
             myPerson = mPerson.fetch() ;
             populateFields (myPerson);
         }
        		 
         @Override
         public void onPause() {
             super.onPause();
             savePerson();
             // mDbHelper.close(); 
         }
 
    private  void populateFields(Cursor myPerson) {
		//
		// Copy data from the cursor into the layout
		// 
		mFirstName.setText(myPerson.getString(myPerson.getColumnIndex(Person.KEY_PERSON_FIRSTNAME)));
        mLastName.setText(myPerson.getString(myPerson.getColumnIndex(Person.KEY_PERSON_LASTNAME)));
        mPhone.setText(myPerson.getString(myPerson.getColumnIndex(Person.KEY_PERSON_PHONE)));
        mMobile.setText(myPerson.getString(myPerson.getColumnIndex(Person.KEY_PERSON_MOBILE)));
        mEmail.setText(myPerson.getString(myPerson.getColumnIndex(Person.KEY_PERSON_EMAIL)));
    }
    private long savePerson () {
		//
		// Persists the current site state through the mDbHelper adapter
		//
    	if (delete_state == Person.DELETED_NO) {
			if (mFirstName.getText() != null) mPerson.update( Person.KEY_PERSON_FIRSTNAME, mFirstName.getText().toString()) ;
			if (mLastName.getText() != null) mPerson.update( Person.KEY_PERSON_LASTNAME,  mLastName.getText().toString()) ;
			if (mPhone.getText() != null) mPerson.update( Person.KEY_PERSON_PHONE,  mPhone.getText().toString()) ;
			if (mMobile.getText() != null) mPerson.update( Person.KEY_PERSON_MOBILE,  mMobile.getText().toString()) ;
			if (mEmail.getText() != null) mPerson.update( Person.KEY_PERSON_EMAIL,  mEmail.getText().toString()) ;
    	};
		return 1 ;
    }
    private long createPerson () {
		//
		// creates empty site record
		// returns the site record ID or -1 for a failure
		//
		long ID = -1;
		String name = "";
		ID = mPerson.create(name);
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
        	PersonDeleteDialog mD = new PersonDeleteDialog();
            mD.show(getActivity().getFragmentManager(), "delete_person");
            return true;
        case R.id.menu_previous :
        	savePerson();
        	mPerson.rowID = mPerson.getPrevious();
    		updateScreen();
            return true; 
        case R.id.menu_next :
        	savePerson();
        	mPerson.rowID = mPerson.getNext();
    		updateScreen();
            return true; 

        }    
        return super.onOptionsItemSelected(item);
    }
    public void deletePerson() {
    	mPerson.delete();
    	delete_state = Person.DELETED_YES;
    	getActivity().finish();
    }
    
    
}
