package nctu.shoo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


public class GetThread extends Thread {

	Activity activity;
	TextView tv;
	Double _lat, _lon;
	String _dir;

	GetThread(Activity activity, TextView tv, double lat, double lon, String dir) {
		this.activity = activity;
		this.tv = tv;
		this._lat = lat;
		this._lon = lon;
		this._dir = dir;
	}

	@Override
	public void run() {
		String url = "http://140.113.88.57:8000/geoDatas";
		HttpGet request = new HttpGet(url);		
		String result = "";
		try {			
			HttpClient client = new DefaultHttpClient();		
			HttpResponse response = client.execute(request);		
			int code = response.getStatusLine().getStatusCode(); 
			if (code == 200) {
				result = EntityUtils.toString(response.getEntity());
				JSONArray ja = new JSONArray(result);				
				pairMatching(ja);			

			} else {
				connerror();
			}
			
		} catch (Exception e) {
			Log.i(url, e.toString());
			//Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();			
			error(e.toString());
		}
		
	}
	///*
	void pairMatching(final JSONArray ja) {
		activity.runOnUiThread(new Runnable() {
			double lat, lon;
			String direction, id;
			String result = "";
			int index = 0;
			boolean getMatch = false;
			@Override
			public void run() {
				Toast.makeText(activity, "in data matching", Toast.LENGTH_SHORT).show();		
				
				for (index = 0; index < ja.length(); ++index) {
					try {
						id = ja.getJSONObject(index).getString("id");
						lat = Double.parseDouble(ja.getJSONObject(index).getString("latitude"));
						lon = Double.parseDouble(ja.getJSONObject(index).getString("longitude"));
						direction = ja.getJSONObject(index).getString("direction");
						
						if ( Math.abs(lat - _lat) + Math.abs(lon - _lon) < 1) {
							if (direction.equals(_dir)) { 
								MainActivity.matchingID = ("ID: " + id + ", permission code: " + ja.getJSONObject(index).getString("permission_code"));
								getMatch = true;
							}
						}
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (!getMatch) {
					MainActivity.matchingID = ("NO DEVICE FOUND!");
				}
			}
		});
	}
	//*/

	void connerror() {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity, "connected failed", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	void error(final String str) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tv.setText(str);
				Log.v("ERROR MESSAGE", str);
				//Toast.makeText(activity, str, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
