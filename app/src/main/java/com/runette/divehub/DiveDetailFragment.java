package com.runette.divehub;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

public class DiveDetailFragment
extends Fragment {
		
 		//
 		// the main display items - also act as temp data repositories
 		//
 		private int delete_state = Dive.DELETED_NO;
		private Calendar mCalendar;  
 	    private long msiteID = DiveSite.NO_ID_FLAG;
 	    private long mbuddyID = Person.NO_ID_FLAG;
 	    private long mdmID = Person.NO_ID_FLAG;
 	    private EditText mDiveNumber;
 	    private EditText mDepth;
 	    private EditText mDuration;
 	    private Button mDiveSite;
 	    private Button mBuddy;
 	    private Button mDiveMaster;
 	    private EditText mComments;
 		private Button mDateButton;
 	    private Button mTimeButton;
 	    private Button mAdv;
 	    private Button mRbt;
 	    private Button mCommentButton;
 	    private Button mSignatureButton;
 	    private SignatureView mSignature;
 	    private FrameLayout frmLayout;
 	    private Signature signature;
 	  	private boolean mTwoPane;
 	  	DialogReturn mCallback;
 	  	private static final String TAG = "DiveDetailFragment";
 	  	private boolean mViewable = false;
 
 	    //
 	    // Data manipulation objects
 	    //
 	    private Dive mDive ;
 	    private DiveSite mSite ;
 	    private Person mPerson ;
 	    SimpleDateFormat dateFormat = new SimpleDateFormat(Dive.DATE_FORMAT);
 	    SimpleDateFormat timeFormat = new SimpleDateFormat(Dive.TIME_FORMAT);
 	    SimpleDateFormat displayDate = new SimpleDateFormat("dd' / 'MM' / 'yyyy");
 	    SimpleDateFormat displayTime = new SimpleDateFormat("HH:mm");
 	    private boolean changed; // used to keep track of if a change has been made 
 	    private boolean newrec; // used to keep track of if this is a new record

    public DiveDetailFragment() {
    }
    public void onAttach(Activity activity) {
        super.onAttach(activity);
     // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (DialogReturn) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DialogReturn");
        }
        //
        // link to and open the db
        //
        if (mDive == null) {
	        mDive = new Dive(activity);
	        mSite = new DiveSite(activity);  
	        mPerson = new Person(activity);      
	        mCalendar =  Calendar.getInstance() ; 
        }
        mDive.open();
        mSite.open();
        mPerson.open();
       return;
 }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        //
        // get the relevant dive by ID passed in from the parent activity using args.
        // if this is a new dive to be created then NEW_DIVE_FLAG will be passed in 
        //
       
        
    }
    //
    // Create the layout
    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
    		mViewable = false;
            return null;
    	}
        View rootView = inflater.inflate(R.layout.fragment_dive_detail, container, false);
        mViewable = true; 
        return rootView;
    }
    @Override
   public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	//
    	// at this point - the layout has settled and the data objects can be instatiated
    	// and linked to listeners
    	//
    	
     try {
    	 if (mViewable) {
	        mDateButton = (Button) getActivity().findViewById(R.id.reminder_date);
	        mTimeButton = (Button) getActivity().findViewById(R.id.reminder_time);
	        mDiveSite = (Button) getActivity().findViewById(R.id.site);
	        mBuddy = (Button) getActivity().findViewById(R.id.buddy);
	        mDiveMaster = (Button) getActivity().findViewById(R.id.divemaster);
	        mDiveNumber = (EditText) getActivity().findViewById(R.id.divenumber);
	        mDepth = (EditText) getActivity().findViewById(R.id.depth);
	        mDuration = (EditText) getActivity().findViewById(R.id.duration);
	        mAdv = (Button) getActivity().findViewById(R.id.advancedbutton);
	        mRbt = (Button) getActivity().findViewById(R.id.rebratherbutton);
	        mCommentButton = (Button) getActivity().findViewById(R.id.comment_buitton);
	        mSignatureButton = (Button) getActivity().findViewById(R.id.signature_button);
	        frmLayout = (FrameLayout) getActivity().findViewById(R.id.comments);
	        mComments = new EditText(getActivity());
	        mSignature = new SignatureView(getActivity());
	        signature = new Signature(getActivity());
	        frmLayout.addView(mSignature);
	        mSignature.setVisibility(View.INVISIBLE);
	        frmLayout.addView(mComments);
	        mCommentButton.setBackgroundColor(Color.BLACK);
	        registerButtonListeners();
	        if (getArguments().containsKey(Dive.DISPLAY)) {
	         	mTwoPane = getArguments().getBoolean(Dive.DISPLAY);
	         }
	         if (getArguments().containsKey(Dive.ARG_ITEM_ID)) {
	         	setDive(getArguments().getLong(Dive.ARG_ITEM_ID));
	         }
	        
    	 }
	        
	     } catch (NullPointerException e) {
	    	 Log.e(TAG, "error in creating view", e);
	     } catch (SQLException e) {
	    	 Log.e(TAG, "error in creating view", e);
	     }
     
     
     
    	
   }
    
    //
    // then populate the display from the database and repeat whenever focus comes back
    @Override
    public void onResume() {
        	super.onResume();
        if (mViewable)  updateScreen();
               
    }
    private void updateScreen() {
        changed = false;
    	if (mDive.rowID != Dive.NO_ID_FLAG){
	    	Cursor myDive;
	        myDive = mDive.fetch() ;
	        populateFields (myDive);
    	}
    }
    
    //
    // when focus leaves - save to database
    //
    @Override
    public void onPause() {
        super.onPause();
        saveDive();
        
    }
    @Override
    public void onDestroyView () {
    	super.onDestroyView();
    	mViewable=false;
    }
    
    @Override
    public void onDetach(){
    	super.onDetach();
    	if (newrec && ! changed) deleteDive();
        mDive.close();
        mSite.close();
        mPerson.close();
    }
    
    
	public void setTime(Calendar cal) {
		// Set the time button text based upon the value from the database
        String time = setTimeButtons(cal);
        if ( mDive.rowID != Dive.NEW_DIVE_FLAG && mDive.rowID != Dive.NO_ID_FLAG) mDive.update( Dive.KEY_TIME, time);
        Toast.makeText(getActivity(), getString(R.string.detail_saved_message), Toast.LENGTH_SHORT ).show();
	}

	public void setDate(Calendar cal) {
		// Set the date button text based upon the value from the database 
         
        String date = setDateButtons(cal);
        if (mDive.rowID != Dive.NEW_DIVE_FLAG && mDive.rowID != Dive.NO_ID_FLAG) mDive.update( Dive.KEY_DATE, date);
        Toast.makeText(getActivity(), getString(R.string.detail_saved_message), Toast.LENGTH_SHORT ).show();
	}
	private void registerButtonListeners() {

		mDateButton.setOnClickListener(new View.OnClickListener() {
		//
		// The Date Button is linked to the standard DatePickerFragement that returns the dat through the date interface in the 
		// parent activity and which in turn calls setButtons.
		// parent activity must implement the dateReturn interface
		//	
			public void onClick(View v) {
		    	DatePickerFragment newFragment = new DatePickerFragment();
		    	newFragment.setCalender(mCalendar);	
		    	newFragment.show(getFragmentManager(), "datePicker");
		    }
		}); 
		
		
		mTimeButton.setOnClickListener(new View.OnClickListener() {
		//
		// The Date Button is linked to the standard TimePickerFragement that returns the dat through the date interface in the 
		// parent activity and which in turn calls setButtons.
		// parent activity must implement the dateReturn interface
		//	
			public void onClick(View v) {
		    	TimePickerFragment newFragment = new TimePickerFragment();
		    	newFragment.setCalender(mCalendar);	
		    	newFragment.show(getFragmentManager(), "timePicker");
		    }
		}); 
		
		mDiveSite.setOnClickListener(new View.OnClickListener() {
			//
			// The Site Button is linked to the DiveSiteList activity using an intent - passes any know name and id and returns a new id.
			// 
			//	
				public void onClick(View v) {
		Intent detailIntent = new Intent(getActivity(), DiveSiteListActivity.class);
        detailIntent.putExtra(DiveSite.ARG_ITEM_NAME, mDiveSite.getText().toString());
        detailIntent.putExtra(DiveSite.ARG_ITEM_ID, msiteID);
        startActivityForResult(detailIntent, DiveSite.PICK_SITE_REQUEST);
				}
		});
		mBuddy.setOnClickListener(new View.OnClickListener() {
			//
			// The Buddy Button is linked to the DiveSiteList activity using an intent - passes any know name and id and returns a new id.
			// 
			//	
				public void onClick(View v) {
		Intent detailIntent = new Intent(getActivity(), PersonListActivity.class);
        detailIntent.putExtra(Person.ARG_ITEM_NAME, mBuddy.getText().toString());
        detailIntent.putExtra(Person.ARG_ITEM_ID, mbuddyID);
        startActivityForResult(detailIntent, Person.PICK_BUDDY_REQUEST);
				}
		});
		
		mDiveMaster.setOnClickListener(new View.OnClickListener() {
			//
			// The Buddy Button is linked to the DiveSiteList activity using an intent - passes any know name and id and returns a new id.
			// 
			//	
				public void onClick(View v) {
		Intent detailIntent = new Intent(getActivity(), PersonListActivity.class);
        detailIntent.putExtra(Person.ARG_ITEM_NAME, mDiveMaster.getText().toString());
        detailIntent.putExtra(Person.ARG_ITEM_ID, mbuddyID);
        startActivityForResult(detailIntent, Person.PICK_DM_REQUEST);
				}
		});
		
		mAdv.setOnClickListener(new View.OnClickListener() {
			//
			// The Site Button is linked to the DiveSiteList activity using an intent - passes any know name and id and returns a new id.
			// 
			//	
				public void onClick(View v) {
		
				}
		});
		
		mRbt.setOnClickListener(new View.OnClickListener() {
			//
			// The Site Button is linked to the DiveSiteList activity using an intent - passes any know name and id and returns a new id.
			// 
			//	
				public void onClick(View v) {
		
				}
		});
		mCommentButton.setOnClickListener(new View.OnClickListener() {
			//
			// The Site Button is linked to the DiveSiteList activity using an intent - passes any know name and id and returns a new id.
			// 
			//	
				public void onClick(View v) {
					mSignature.setVisibility(View.INVISIBLE);
					mComments.setVisibility(View.VISIBLE);
					mSignatureButton.setBackgroundColor(Color.DKGRAY);
					mCommentButton.setBackgroundColor(Color.BLACK);
				}
		});
		mSignatureButton.setOnClickListener(new View.OnClickListener() {
			//
			// The Site Button is linked to the DiveSiteList activity using an intent - passes any know name and id and returns a new id.
			// 
			//	
				public void onClick(View v) {
		mSignature.setVisibility(View.VISIBLE);
		mComments.setVisibility(View.INVISIBLE);
		mSignatureButton.setBackgroundColor(Color.BLACK);
		mCommentButton.setBackgroundColor(Color.DKGRAY);
				}
		});
		mSignature.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN ) {
					Intent detailIntent = new Intent(getActivity(), SignatureActivity.class);
				    detailIntent.putExtra(Dive.ARG_ITEM_ID, mDive.rowID);  
				    startActivity(detailIntent);
				}
				return true;	
			}
		});
		
	}

	private  void populateFields(Cursor myDive) {
		//
		// Copy data from the currsor into the layout
		//
	try {
		if (mViewable) {
			int number = myDive.getInt(myDive.getColumnIndex(Dive.KEY_NUMBER));
			mDiveNumber.setText(String.valueOf(number));
			String time = myDive.getString(myDive.getColumnIndex(Dive.KEY_TIME));
			String day  = myDive.getString(myDive.getColumnIndex(Dive.KEY_DATE));
			mCalendar = stringToCal(day, time) ;
			setButtons(mCalendar);
			String name = myDive.getString(myDive.getColumnIndex(Dive.KEY_SITENAME));
			mDiveSite.setText(name);
			msiteID = myDive.getLong(myDive.getColumnIndex(Dive.KEY_SITE));
	        mBuddy.setText(myDive.getString(myDive.getColumnIndex(Dive.KEY_BUDDY_NAME)));
	        mbuddyID = myDive.getLong(myDive.getColumnIndex(Person.KEY_BUDDY));
	        mDepth.setText(myDive.getString(myDive.getColumnIndex(Dive.KEY_DEPTH)));
	        mDuration.setText(myDive.getString(myDive.getColumnIndex(Dive.KEY_DURATION)));
	        mComments.setText(myDive.getString(myDive.getColumnIndex(Dive.KEY_COMMENTS)));
			mdmID = myDive.getLong(myDive.getColumnIndex(Person.KEY_DM_ID));
			mDiveMaster.setText(myDive.getString(myDive.getColumnIndex(Dive.KEY_DM_NAME)));
			signature.get(mDive);
			mSignature.setSignature(signature);
		}
	} catch (NullPointerException e) {
   	 Log.e(TAG, "error in populating  view", e);
    } catch (SQLException e) {
   	 Log.e(TAG, "error in populating view", e);
    }
	}		
		
	private String setTimeButtons (Calendar cal) {
		
		
        mTimeButton.setText(displayTime.format(cal.getTime())); 
        return timeFormat.format(cal.getTime()) ;
	}
	
	private String setDateButtons (Calendar cal) {
		
        
        mDateButton.setText(displayDate.format(cal.getTime()));
        return dateFormat.format(cal.getTime());
	}
	
	private void setButtons (Calendar cal) {
		setTimeButtons(cal);
		setDateButtons(cal);
	}
	
	public void setDive(long id){
		if (mDive.rowID != Dive.NO_ID_FLAG) saveDive();
		mDive.rowID=id;
		if (mDive.rowID == Dive.NEW_DIVE_FLAG) {
        	//
        	// create new dive record - using defaults for dive number, date and time
        	//
        	long number = mDive.nextNumber();
        	mDiveNumber.setText(String.valueOf(number));
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        	String defaultTimeKey = getString(R.string.pref_default_time_from_now_key);
        	String defaultTime = prefs.getString(defaultTimeKey, null);
        	if(defaultTime != null)
        		mCalendar.add(Calendar.MINUTE, (Integer.parseInt(defaultTime) * -1));
        	setButtons(mCalendar);
        	createDive() ;
        	newrec= true;
        	changed = false ; 
        } else newrec = false;
		if (mTwoPane && mViewable) updateScreen();
	}
	public void setSite (long id) {
		msiteID = id;
		Cursor site = mSite.fetch(msiteID);
		String name = site.getString(site.getColumnIndex(DiveSite.KEY_SITE_NAME));
		String location = site.getString(site.getColumnIndex(DiveSite.KEY_SITE_LOCATION));
		mDiveSite.setText(name + ", " + location);
		saveDive();
		}
	public void setBuddy(long id) {
		mbuddyID = id;
		Cursor buddy = mPerson.fetch(mbuddyID);
		String firstname = buddy.getString(buddy.getColumnIndex(Person.KEY_PERSON_FIRSTNAME));
		String lastname = buddy.getString(buddy.getColumnIndex(Person.KEY_PERSON_LASTNAME));
		mBuddy.setText(firstname + " " + lastname);
		saveDive();
	}
	public void setDiveMaster(long id) {
		mdmID = id;
		Cursor buddy = mPerson.fetch(mdmID);
		String firstname = buddy.getString(buddy.getColumnIndex(Person.KEY_PERSON_FIRSTNAME));
		String lastname = buddy.getString(buddy.getColumnIndex(Person.KEY_PERSON_LASTNAME));
		mDiveMaster.setText(firstname + " " + lastname);
		saveDive();
	}
	
	
	private long saveDive () {
		//
		// Persists the current dive state through the mDbHelper adapter
		//
		if (delete_state == Dive.DELETED_NO && mDive.rowID != Dive.NO_ID_FLAG) {
			// if (mDiveSite.getText() != null) mDive.update(mID, Dive.KEY_SITE, (String) mDiveSite.getText().toString()) ;
			if (mDiveNumber.getText() != null) changed = changed | mDive.update( Dive.KEY_NUMBER,  mDiveNumber.getText().toString()) ;
			if (mBuddy.getText() != null) changed = changed | mDive.update( Dive.KEY_BUDDY_NAME, (String) mBuddy.getText().toString()) ;
			if (mDepth.getText() != null) changed = changed | mDive.update( Dive.KEY_DEPTH, (String) mDepth.getText().toString()) ;
			if (mDiveSite.getText() != null) changed = changed | mDive.update( Dive.KEY_SITENAME, (String) mDiveSite.getText().toString());
			if (mDuration.getText() != null) changed = changed | mDive.update( Dive.KEY_DURATION, (String) mDuration.getText().toString());
			if (mComments.getText() != null) changed = changed | mDive.update( Dive.KEY_COMMENTS, (String) mComments.getText().toString());
			if (mDiveMaster.getText() != null) changed = changed | mDive.update( Dive.KEY_DM_NAME, (String) mDiveMaster.getText().toString()) ;
			if (msiteID > 0 ) { 
				changed = changed | mDive.update( Dive.KEY_SITE, Long.toString(msiteID));
			} else {
				changed = changed | mDive.update( Dive.KEY_SITE, Long.toString(DiveSite.NO_ID_FLAG));
			}
			if (mbuddyID > 0 ) { 
				changed = changed | mDive.update( Dive.KEY_BUDDY, Long.toString(mbuddyID));
			} else {
				changed = changed | mDive.update( Dive.KEY_BUDDY, Long.toString(Person.NO_ID_FLAG));
			}
			if (mdmID > 0 ) { 
				changed = changed | mDive.update( Dive.KEY_DM_ID, Long.toString(mdmID));
			} else {
				changed = changed | mDive.update( Dive.KEY_DM_ID, Long.toString(Person.NO_ID_FLAG));
			}
			signature.put(mDive);
		}
		return 1 ;
	}
	private Calendar stringToCal(String day, String time )	 {
		//
		// convenience routine used to convert string variables from the db in a Calendar object
		//
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat(DiveDbAdapter.DATE_TIME_FORMAT);
		Date date = null;
		Calendar c = Calendar.getInstance() ; ; 
		try {
			date = dateTimeFormat.parse(day + " " + time);
            c.setTime(date); 
		} catch (ParseException e) {
			Log.e("DiveDetailFragment", e.getMessage(), e); 
		} 
		return c;
	}	
	
	private long createDive () {
		//
		// uses current layout state to create a new dive record
		// returns the dive record ID or -1 for a failure
		//
		Calendar now = Calendar.getInstance();
		long ID = -1;
		if (mCalendar.before(now)) {
			String date = dateFormat.format(mCalendar.getTime());
			String time = timeFormat.format(mCalendar.getTime());
			int number = Integer.valueOf(mDiveNumber.getText().toString());
			ID = mDive.create(number, date, time);
		} else Toast.makeText(getActivity(), getString(R.string.dive_in_future_message), Toast.LENGTH_SHORT).show();
		return ID;
	}
	//
    // set up and act upon the menu
    //
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater mi) {
    	super.onCreateOptionsMenu(menu, mi);
    	mi.inflate(R.menu.detail_menu, menu);
        return ; 
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.menu_accept :
        	mCallback.invalidate();
            return true; 
        case R.id.menu_cancel: 
        	if ( changed | (! newrec)) {
        		DiveDeleteDialog mD = new DiveDeleteDialog();
        		mD.show(getActivity().getFragmentManager(), "delete_dive");
        	} else deleteDive();
        	
            return true;
        case R.id.menu_previous :
        	saveDive();
        	mDive.rowID = mDive.getPrevious();
    		updateScreen();
            return true; 
        case R.id.menu_next :
        	saveDive();
        	mDive.rowID = mDive.getNext();
    		updateScreen();
            return true; 
            
        }    
        return super.onOptionsItemSelected(item);
    }

	public void deleteDive() {
		//
		//called to delete current dive
		//
		mDive.delete();
		delete_state = Dive.DELETED_YES;
		mCallback.invalidate();
		
		}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	      super.onActivityResult(requestCode, resultCode, data);
	            if (resultCode == android.app.Activity.RESULT_OK) {
	                // A site was picked.  
	            	switch (requestCode) {
	            	case Person.PICK_SITE_REQUEST :
	            		long id = data.getLongExtra(DiveSite.ARG_ITEM_ID,DiveSite.NO_ID_FLAG);
	            		if (id != DiveSite.NO_ID_FLAG ) setSite(id);
	            		return;
	            	case Person.PICK_BUDDY_REQUEST :
	            		id	 = data.getLongExtra(Person.ARG_ITEM_ID,Person.NO_ID_FLAG);
	            		if (id != Person.NO_ID_FLAG ) setBuddy(id);
	            		return;
	            	case Person.PICK_DM_REQUEST :
	            		id = data.getLongExtra(Person.ARG_ITEM_ID,Person.NO_ID_FLAG);
	            		if (id != Person.NO_ID_FLAG ) setDiveMaster(id);
	            	}
	            }
	       
	    }
	
}
		
	
