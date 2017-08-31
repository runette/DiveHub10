package com.runette.divehub;

import java.util.Calendar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

public class DatePickerFragment extends 	android.support.v4.app.DialogFragment
	implements DatePickerDialog.OnDateSetListener {
	
		DialogReturn mCallback;
		public Calendar c = Calendar.getInstance();
		
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of 	DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		// Do something with the date chosen by the user
		c.set(year, month, day);
		mCallback.setDate(c);
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
	                    + " must implement DateReturn");
	        }
	 }
	 public void setCalender(Calendar cal) {
		 c = cal;
	 }
}

