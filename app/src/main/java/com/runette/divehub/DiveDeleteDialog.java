package com.runette.divehub;

import android.app.AlertDialog;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


public class DiveDeleteDialog extends DialogFragment {
	
	DialogReturn mCallback;
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	builder.setMessage(R.string.dive_delete_message)
    		.setTitle(R.string.dive_delete_title);
    	// Add the buttons
    	builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
               mCallback.deleteDive();
               return;
           }
       });
    	builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
               return;
           }
       });
    	return builder.create(); 

    }
    @Override
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
	 }
}
