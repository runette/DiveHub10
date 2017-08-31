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


public class DiveSiteListFragment extends ListFragment {

    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private DiveSite mSite;
    private SimpleCursorAdapter mAdapter;
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
    

    public DiveSiteListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSite = new DiveSite(getActivity());
        mSite.open();
        if (getArguments().containsKey(DiveSite.ARG_ITEM_ID)) 
            mSite.rowID = getArguments().getLong(DiveSite.ARG_ITEM_ID);
        if (getArguments().containsKey(DiveSite.ARG_ITEM_NAME)) {
            mName = getArguments().getString(DiveSite.ARG_ITEM_NAME);
        }
        
		setHasOptionsMenu(true);
        
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null && savedInstanceState
                .containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
        this.setActivateOnItemClick(true);
        
        
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
        mSite.open(); 
        setListAdapter(createAdapter(mSite.rowID, mName)); 
        setActivateOnItemClick(true);
        setActivatedPosition(mActivatedPosition);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSite.close();
        mCallbacks = sDummyCallbacks;
    }
   

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        mSite.rowID = id;
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
    private SimpleCursorAdapter createAdapter (long id, String name) {
    	int mcount = mSite.fetch(id).getCount();
        if ( mcount  == 0) {
        	id = DiveSite.NO_ID_FLAG;
        	if ( name != null) {
        		long i = mSite.findbyValue(DiveSite.KEY_SITE_NAME, name);
        		if ( i != DiveSite.NO_NAME) {
        			id = i;
        		} else {
        			id = mSite.create(name);
        			
        		}
        	}
        }
        Cursor siteList = mSite.fetchAll();
        long row = 0;
        if ( id != DiveSite.NO_ID_FLAG) {
        	siteList.moveToFirst();
        	row = siteList.getLong(siteList.getColumnIndex(DiveSite.KEY_ROWID)) ;
        	while ( row != id && siteList.isLast() == false) {
        		siteList.moveToNext();
        		row = siteList.getLong(siteList.getColumnIndex(DiveSite.KEY_ROWID)) ;
        	}
        } ;
        if (row == id ) {
			mActivatedPosition = siteList.getPosition();
			} 
		mSite.rowID = id;
        String[] from = new String[] {DiveSite.KEY_SITE_NAME};        
        int[] to = new int[] {android.R.id.text1} ;
        mAdapter = new SimpleCursorAdapter(getActivity(),android.R.layout.simple_list_item_activated_1, siteList, from, to, 0);
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
				mCallbacks.onItemAccepted(mSite.rowID);
				getActivity().finish();
				return true; 
			case R.id.menu_site_add:
				mCallbacks.onItemAdded();
				return true;
			case R.id.menu_site_edit:
				mCallbacks.onItemEdited(mSite.rowID);
				return true;
        }    
        return super.onOptionsItemSelected(item);
    }
}
