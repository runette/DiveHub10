package com.runette.divehub;

import java.util.Calendar;




public interface DialogReturn {
	public void setDate(Calendar cal);
	public void setTime(Calendar cal);
	public void deleteDive();
	public void deleteSite();
	public void deletePerson();
	public void invalidate();
	

}
