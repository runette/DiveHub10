package com.runette.divehub;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Path;


import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;


public class Signature   {
	
	private ArrayList<float[]> signature ;
	public static final float NOT_LINE_START = 0;
	public static final float LINE_START = NOT_LINE_START +1;
	public static final float X_SIZE = 210;
	public static final float Y_SIZE = 100;
	private float top;
	private float bottom;
	private float left;
	private float right;
	private float scalex;
	private float scaley;
	//private Dive mDive ;
	//private long mID;
	


	
	
	public Signature (Context ctx) {
		signature = new ArrayList<float[]>(1000) ;
		top = 0;
		bottom = 0;
		left = 0;
		right = 0;
		scalex = 1;
		scaley= 1;
		

		}
	
	public void clear() {
		signature.clear();
	}

	public Path getPath () {
		Path path = new Path();
		int start =1;
		float x;
		float y;
		if (signature.size() > 0) {
			for (int i = 0; i < signature.size(); i++ ) {
				float[] coords = {0,0,0} ;
				coords = signature.get(i);
				x = coords[0] * scalex;
				y =  coords[1] * scaley;
				start = (coords[2] >  (NOT_LINE_START + 0.1)) ? 1 : 0;   
				if ( start == 1 ) {
					path.moveTo(x, y);
				} else {
					path.lineTo(x, y);
				}
			}
		}
		return path ;
	}
	

	public ArrayList<float[]> getList() {
		return signature;
	}
	
	public boolean setList(ArrayList<float[]> data) {
		
		signature.clear();
		signature = data;

		
		return true;
	}
	
	public char[] getString() {
		byte [] buffill = new byte[] {0,0,0,0};
		ByteBuffer buffer = ByteBuffer.wrap(buffill);
		float x;
		float y;
		byte[] bytevals  = new byte[signature.size()*2];
		char[] signatureString = new char[signature.size()*4];
		for (int i=0 ; i < signature.size(); i++) {
			float[] coords = {0,0,0} ;
			coords = signature.get(i);
			 x = coords[0] ; 
			 y = coords[1] ;
			 buffer.putShort(0, (short) x);
			 buffer.putShort(2, (short) y);
			bytevals [2*i] = buffer.get(1) ;
			bytevals [2*i+1] =  (byte) ((coords[2] > NOT_LINE_START) ? buffer.get(3) + (byte) 0x80 : buffer.get(3));
		}

		signatureString = Hex.encodeHex(bytevals);
		return signatureString ;
	}
	
	public boolean setString (char[] input){
		byte [] buffill = new byte[] {0,0,0,0};
		ByteBuffer buffer = ByteBuffer.wrap(buffill);
		signature.clear();
		float x =0;
		float y =0;
		float status = NOT_LINE_START;
		byte[] data = new byte[input.length/2];
		try {
			data = Hex.decodeHex(input);
		} catch (DecoderException e) {
			e.printStackTrace();
			return false;
		}
		for (int i = 0; i<data.length; i+=2 ) {
			buffer.put(1, data[i]);
			buffer.put(3, data[i+1]);
			x = (float) buffer.getShort(0);
			y = (float) buffer.getShort(2);
			if (y < 100)  {
				status = NOT_LINE_START;
			} else {
				y = y - 0x80;
				status = LINE_START;
			}

		float[] coords = {0,0,0};
		coords[0] = x ;
		coords[1] = y;
		coords[2] = status;
		signature.add(coords);
		}
		return true;
	}
	
	
	public boolean addPoint (float x, float y, float state) {
		float[] coords = {0,0,0} ;
		if (! (x >= left && x <= right && y >= bottom && y <= top)) {
			coords[0] = x/scalex;
			coords[1] = y/scaley;
			coords[2] = state;
			signature.add(coords);
		}
		return true;
	}
	
	
	public void setSize (boolean changed, int ileft, int itop, int iright, int ibottom) {
		if (changed) {
			bottom = ibottom;
			top = itop ;
			left = ileft;
			right = iright;
			scalex = (right - left)/X_SIZE; 
			scaley = (bottom - top)/Y_SIZE;
			return;
		}
	}
	
	public void get (Dive mDive){
		//mDive.rowID = id;
		Cursor c = mDive.fetch();
        String sigstring = c.getString(c.getColumnIndex(Dive.KEY_SIGNATURE));
        if (sigstring != null) { 
        	setString (sigstring.toCharArray());
        }  else clear();
	}
	
	
	
	public void put (Dive mDive) {
		mDive.update( Dive.KEY_SIGNATURE, new String(getString()));
	}
	
	
	
}
