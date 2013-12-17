package fr.eurecom.hikingit;

import fr.eurecom.hikingit.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class DisplayActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);
		//hello
	}
	
	public void textAllView(View v){
		Intent i = new Intent(this, DisplayAllActivity.class);
		startActivity(i);		
	};
	public void textView(View v){
		Intent i = new Intent(this, SearchTrackActivity.class);
		startActivity(i);		
	};
	public void mapView(View v){
		Intent i = new Intent(this, DisplayMapActivity.class);
		startActivity(i);		
	};	
	

}
