package org.zub.dnb101ru;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class RecentTracks {
	
	public String getRecentTracks(){
	    HttpClient client = new DefaultHttpClient();
	    HttpGet httpGet = new HttpGet("http://101.ru/?an=mobileAppDataLastSong&channel=6");
		try {
		      HttpResponse response = client.execute(httpGet);
		      StatusLine statusLine = response.getStatusLine();
		      int statusCode = statusLine.getStatusCode();
		      if (statusCode == 200) {
		        HttpEntity entity = response.getEntity();
		        
		        InputStream content = entity.getContent();
		        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		        StringBuilder sb = new StringBuilder();

		        String line = null;
		        while ((line = reader.readLine()) != null)
		        {
		            sb.append(line + "\n");
		        }
		        return sb.toString();
		      } else{
		    	  Log.i("JSONTest", ">>>>>>>>>>>>>>>>>>>>>Something is wrong");
		    	  return null;
		      }
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			return e.toString();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			return e.toString();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return e.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return e.toString();
		}
	}
	
	public String getLatestTrackTitle() {
		try {				
			JSONArray jArray = new JSONArray(getRecentTracks());
			JSONObject oneObject = jArray.getJSONObject(0);
			//trackURL = oneObject.getString("full_sample_track");
			String trackTitle = oneObject.getString("name");
			//result.append(trackURL);
			return trackTitle;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}			
	}
	
	public String getLatestTrackURL() {
		try {				
			JSONArray jArray = new JSONArray(getRecentTracks());
			JSONObject oneObject = jArray.getJSONObject(0);
			String trackURL = oneObject.getString("full_sample_track");
			//trackTitle = oneObject.getString("name");
			//result.append(trackURL);
			return trackURL;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}			
	}
}
