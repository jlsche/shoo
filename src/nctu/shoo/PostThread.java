package nctu.shoo;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class PostThread extends Thread {

	Double _latitude, _longitude;
	String _direction, _code;
	Activity activity;
	TextView tv;

	PostThread(Activity activity, TextView tv, Double in_lat, Double in_lon, String dir, String code) {
		this.activity = activity;
		this.tv = tv;
		this._latitude = in_lat;
		this._longitude = in_lon;
		this._direction = dir;
		this._code = code;
	}

	@Override
	public void run() {
		String url = "http://140.113.88.57:8000/geoDatas/";
		HttpPost request = new HttpPost(url);		
		String result = "";
		try {	
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("permission_code", _code));
	        nameValuePairs.add(new BasicNameValuePair("latitude", Double.toString(_latitude)));
	        nameValuePairs.add(new BasicNameValuePair("longitude", Double.toString(_longitude)));
	        nameValuePairs.add(new BasicNameValuePair("direction", _direction));
	        request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        
	        HttpClient client = new DefaultHttpClient();		
			HttpResponse response = client.execute(request);		
			int code = response.getStatusLine().getStatusCode(); 
			
			if (code == 201) {
				result = EntityUtils.toString(response.getEntity());
				Toast.makeText(activity, "geo data uploaded!", Toast.LENGTH_SHORT).show();
			} else {
				connerror();
			}
			
		} catch (Exception e) {
			Log.v(url, e.toString());
			//Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();			
			//error();
		}
		
	}

	void connerror() {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity, "connected failed", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	void error() {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity, "unknown error", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
