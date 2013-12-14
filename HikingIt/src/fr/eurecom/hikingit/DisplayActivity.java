package fr.eurecom.hikingit;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class DisplayActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);
		//hello
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display, menu);
		return true;
	}
	
	public void textAllView(){
		Intent i = new Intent(this, DisplayAllActivity.class);
		startActivity(i);		
	};
	public void textView(){
		Intent i = new Intent(this, SearchTrackActivity.class);
		startActivity(i);		
	};
	public void mapView(){
		Intent i = new Intent(this, DisplayMapActivity.class);
		startActivity(i);		
	};	
	

}
