package nctu.shoo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements LocationListener, SensorEventListener, OnGestureListener {

	/* tv_current : including current latitude, longitude, facing direction
	 * tv_result  : show matchingID, which is the matching result 
	 * default value : (latitude, longitude, direction, permission_code) = (1.0, 1.0, N, abc)
	 */
	
	TextView tv_current, tv_result;
	static String matchingID = "";
	SensorManager smgr;
	Sensor smf, sa;
	LocationManager lmgr;
	Double current_longitude = 11.0, current_latitude = 11.0, azimuth = 0.0;
	String current_direction = "N", oppsite_direction = "S", permission_code = "abc";
	private String best_provider;
	
	private float[] valuesAccelerometer;
	private float[] valuesMagneticField;	   
	private float[] matrixR;
	private float[] matrixI;
	private float[] matrixValues;
	
	private GestureDetectorCompat gestureScanner;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		tv_current = (TextView)findViewById(R.id.tv_current);
		tv_result = (TextView)findViewById(R.id.tv_result);
		Button btn_get = (Button)findViewById(R.id.btn_get);
		btn_get.setOnClickListener(get);
		Button btn_post = (Button)findViewById(R.id.btn_post);
		btn_post.setOnClickListener(post);
		
		lmgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		smgr = (SensorManager)getSystemService(SENSOR_SERVICE);
		sa = smgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		smf = smgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		Criteria criteria = new Criteria();
		best_provider = lmgr.getBestProvider(criteria, false);
		lmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 10, this); // update location every 20 secs or 10 meters
		Location location = lmgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		valuesAccelerometer = new float[3];
		valuesMagneticField = new float[3];		  
		matrixR = new float[9];
		matrixI = new float[9];
		matrixValues = new float[3];
		
		gestureScanner = new GestureDetectorCompat(this, this);
		
		if (location != null) {
			tv_current.setText(Double.toString(current_latitude) + ", " + Double.toString(current_latitude) + " " + current_direction);		
		}else {
			tv_current.setText("cannot get position\n");
		}		
	}
	
	///* 
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
	    
		switch(event.sensor.getType()){
		case Sensor.TYPE_ACCELEROMETER:
			for(int i =0; i < 3; i++){
				valuesAccelerometer[i] = event.values[i];
			}
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			for(int i =0; i < 3; i++){
				valuesMagneticField[i] = event.values[i];
			}
			break;
		}
	    
		boolean success = SensorManager.getRotationMatrix(matrixR, matrixI, valuesAccelerometer, valuesMagneticField);
	    
		if(success){
			SensorManager.getOrientation(matrixR, matrixValues);	     
			azimuth = Math.toDegrees(matrixValues[0]);
			
			if ((azimuth > 0 && azimuth <= 22.5) || azimuth > 337.5){
				oppsite_direction = "S";
				current_direction = "N";
				//tv_current.setText(Double.toString(current_latitude) + ", " + Double.toString(current_latitude) + " " + current_direction);		
			}else if (azimuth > 22.5 && azimuth <= 67.5) {
				oppsite_direction = "WS";
				current_direction = "EN";
				//tv_current.setText(Double.toString(current_latitude) + ", " + Double.toString(current_latitude) + " " + current_direction);		
			}else if (azimuth > 67.5 && azimuth <= 112.5) {
				oppsite_direction = "W";
				current_direction = "E";
			}else if (azimuth > 112.5 && azimuth <= 157.5) {
				oppsite_direction = "WN";
				current_direction = "ES";
				//tv_current.setText(Double.toString(current_latitude) + ", " + Double.toString(current_latitude) + " " + current_direction);		
			}else if (azimuth > 157.5 && azimuth <= 202.5) {
				oppsite_direction = "N";
				current_direction = "S";
				//tv_current.setText(Double.toString(current_latitude) + ", " + Double.toString(current_latitude) + " " + current_direction);		
			}else if (azimuth > 202.5 && azimuth <= 247.5) {
				oppsite_direction = "EN";
				current_direction = "WS";
				//tv_current.setText(Double.toString(current_latitude) + ", " + Double.toString(current_latitude) + " " + current_direction);		
			}else if (azimuth > 247.5 && azimuth <= 292.5) {
				oppsite_direction = "E";
				current_direction = "W";
				//tv_current.setText(Double.toString(current_latitude) + ", " + Double.toString(current_latitude) + " " + current_direction);		
			}else if (azimuth > 292.5 && azimuth <= 337.5) {
				oppsite_direction = "ES";
				current_direction = "WN";
				//tv_current.setText(Double.toString(current_latitude) + ", " + Double.toString(current_latitude) + " " + current_direction);		
			}
			tv_current.setText(Double.toString(current_latitude) + ", " + Double.toString(current_latitude) + " " + current_direction);	
		}  
		
	}
	
	@Override
	protected void onPause() {	  
		smgr.unregisterListener(this, sa);
		smgr.unregisterListener(this, smf);
		super.onPause();
	}
	 

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}
		
	@Override
	protected void onResume() {  
		smgr.registerListener(this, sa, SensorManager.SENSOR_DELAY_NORMAL);
		smgr.registerListener(this, smf, SensorManager.SENSOR_DELAY_NORMAL);
		super.onResume();
	}
	//*/
	////////////////////////////////////////
	OnClickListener get = new OnClickListener() {
		@Override
		public void onClick(View v) {
			GetThread thread = new GetThread(MainActivity.this, tv_result, current_latitude, current_longitude, current_direction);
			thread.start();
			tv_result.setText(matchingID);
			matchingID = "";
		}
	};
	
	OnClickListener post = new OnClickListener() {
		@Override
		public void onClick(View v) {
			PostThread thread = new PostThread(MainActivity.this, tv_result, current_latitude, current_longitude, oppsite_direction, permission_code);
			thread.start();
		}
	};
	/////////////////////////////////////////
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		current_latitude = location.getLatitude();
		current_longitude = location.getLongitude();
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	
    @Override 
    public boolean onTouchEvent(MotionEvent event){ 
        this.gestureScanner.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		// TODO Auto-generated method stub
		GetThread thread = new GetThread(MainActivity.this, tv_result, current_latitude, current_longitude, current_direction);
		thread.start();
		tv_result.setText(matchingID);
		matchingID = "";
		return true;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}
