package com.runette.divehub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

public class XmlParse extends DefaultHandler{
	public String tempVal = null;
	public String syncver = null;
	public String fileUUID = null;
	public String filename = null;
	public String MD5 = null;
	public String updated = null;
	public String TAG = "XmlParse";
	
	public XmlParse(String path) {
		
	
	
		//get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		FileInputStream inputStream = null;
		try {
	        File file = new File(path);
	        inputStream = new FileInputStream(file);
	        
	    } catch (FileNotFoundException e) {
	        Log.e(TAG, "File not found.");
	    }
		try {
	
			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();
	
			//parse the file and also register this class for call backs
			sp.parse(inputStream, this);
	
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}
	}//Event Handlers
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
			//reset
			tempVal = "";
			if(qName.equalsIgnoreCase("Sync")) {
				syncver = attributes.getValue("Version");
			}
			if(qName.equalsIgnoreCase("File")) {
				fileUUID = attributes.getValue("UUID");
			}
		}


		public void characters(char[] ch, int start, int length) throws SAXException {
			tempVal = new String(ch,start,length);
		}

		public void endElement(String uri, String localName,
			String qName) throws SAXException {

			if(qName.equalsIgnoreCase("SqliteFilename")) {
				filename=tempVal;

			}else if (qName.equalsIgnoreCase("MD5")) {
				MD5 = tempVal;
			}else if (qName.equalsIgnoreCase("Updated")) {
				updated = tempVal;
			}
			

		}
}

