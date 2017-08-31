package com.runette.divehub;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

@SuppressLint({ "NewApi", "NewApi" })
public class TimePickerFragment extends android.support.v4.app.DialogFragment
	implements TimePickerDialog.OnTimeSetListener {
	
	DialogReturn mCallback;
	public Calendar c = Calendar.getInstance();
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		//Use the current time as the default values for the picker
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

		//Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		//Do something with the time chosen by the user
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		c.set(year, month, day, hourOfDay, minute);
		mCallback.setTime(c);
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
	                    + " must implement OnHeadlineSelectedListener");
	        }
	 }
	public void setCalender(Calendar cal) {
		 c = cal;
	 }
}