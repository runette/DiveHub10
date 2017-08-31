package com.runette.divehub;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class PersonListFragment extends ListFragment {

	 private static final String STATE_ACTIVATED_POSITION = "activated_position";
	    private Person mPerson;
	    private SimpleCursorAdapter mAdapter;
	    private long mID = Person.NO_ID_FLAG;
	    private String mName = null;

	    private Callbacks mCallbacks = sDummyCallbacks;
	    private int mActivatedPosition = ListView.INVALID_POSITION;

	    public interface Callbacks {

	        public void onItemAccepted(long id);
	        public void onItemEdited (long id);
	        public void onItemSelected (long id);
	        public void onItemAdded ();
	    }

	    private static Callbacks sDummyCallbacks = new Callbacks() {
	        //@Override
	        public void onItemAccepted(long id) {
	        	
	        } 
	        public void onItemEdited (long id) {
	        	
	        }
	        public void onItemSelected (long id){
	        	
	        }
	        public void onItemAdded (){
	        	
	        }
	    };
	    

	    public PersonListFragment() {
	    }

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	         if (getArguments().containsKey(Person.ARG_ITEM_ID)) 
	            mID = getArguments().getLong(Person.ARG_ITEM_ID);
	         if (getArguments().containsKey(Person.ARG_ITEM_NAME)) 
	           mName = getArguments().getString(Person.ARG_ITEM_NAME);
	        
	        mPerson = new Person(getActivity());
	        mPerson.open();
			setHasOptionsMenu(true);
	        
	    }

	    @Override
	    public void onViewCreated(View view, Bundle savedInstanceState) {
	        super.onViewCreated(view, savedInstanceState);
	        if (savedInstanceState != null && savedInstanceState
	                .containsKey(STATE_ACTIVATED_POSITION)) {
	            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
	        }
	        
	        
	    }

	    @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        if (!(activity instanceof Callbacks)) {
	            throw new IllegalStateException("Activity must implement fragment's callbacks.");
	        }

	        mCallbacks = (Callbacks) activity;
	    }
	    
	    @Override
	    public void onResume() {
	        super.onResume();
	        mPerson.open(); 
	        setListAdapter(createAdapter(mID, mName)); 
	        setActivateOnItemClick(true);
	        setActivatedPosition(mActivatedPosition);
	    }

	    @Override
	    public void onDetach() {
	        super.onDetach();
	        mPerson.close();
	        mCallbacks = sDummyCallbacks;
	    }
	   

	    @Override
	    public void onListItemClick(ListView listView, View view, int position, long id) {
	        super.onListItemClick(listView, view, position, id);
	        mID = id;
	        mCallbacks.onItemSelected(id);
	    }

	    @Override
	    public void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	        if (mActivatedPosition != ListView.INVALID_POSITION) {
	            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
	        }
	    }

	    public void setActivateOnItemClick(boolean activateOnItemClick) {
	        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	        getListView().setSelector(android.R.color.holo_blue_dark);
	    }

	    public void setActivatedPosition(int position) {
	        if (position == ListView.INVALID_POSITION) {
	            getListView().setItemChecked(mActivatedPosition, false);
	            
	        } else {
	            getListView().setItemChecked(position, true);
	        }

	        mActivatedPosition = position;
	    }
	    private SimpleCursorAdapter createAdapter (Long id, String name) {
	    	int mcount = mPerson.fetch(id).getCount();
	        if ( mcount  == 0) {
	        	id = Person.NO_ID_FLAG;
	        	if ( name != null) {
	        		long i = mPerson.findbyValue(Person.KEY_PERSON_LASTNAME, name);
	        		if ( i != Person.NO_NAME) {
	        			id = i;
	        		} else {
	        			id = mPerson.create(name);
	        			
	        		}
	        	}
	        }
	        Cursor siteList = mPerson.fetchAll();
	        long row = 0;
	        if ( id != Person.NO_ID_FLAG) {
	        	siteList.moveToFirst();
	        	row = siteList.getLong(siteList.getColumnIndex(Person.KEY_ROWID)) ;
	        	while ( row != id && siteList.isLast() == false) {
	        		siteList.moveToNext();
	        		row = siteList.getLong(siteList.getColumnIndex(Person.KEY_ROWID)) ;
	        	}
	        } ;
	        if (row == id ) {
				mActivatedPosition = siteList.getPosition();
				} 
			mID = id;
	        String[] from = new String[] {Person.KEY_PERSON_FIRSTNAME,Person.KEY_PERSON_LASTNAME};        
	        int[] to = new int[] {android.R.id.text2,android.R.id.text1} ;
	        mAdapter = new SimpleCursorAdapter(getActivity(),android.R.layout.simple_list_item_activated_2, siteList, from, to, 0);
	        return mAdapter ;
	    }
		// set up and act upon the menu
	    //
	    @Override
	    public void onCreateOptionsMenu(Menu menu, MenuInflater mi) {
	    	super.onCreateOptionsMenu(menu, mi);
	    	 mi.inflate(R.menu.site_list_menu, menu); 
	    }
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch(item.getItemId()) {
				case R.id.menu_site_accept :
					mCallbacks.onItemAccepted(mID);
					getActivity().finish();
					return true; 
				case R.id.menu_site_add:
					mCallbacks.onItemAdded();
					return true;
				case R.id.menu_site_edit:
					mCallbacks.onItemEdited(mID);
					return true;
	        }    
	        return super.onOptionsItemSelected(item);
	    }
	}
