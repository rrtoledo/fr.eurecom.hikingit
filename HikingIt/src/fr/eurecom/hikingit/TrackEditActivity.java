package fr.eurecom.hikingit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import fr.eurecom.hikingit.R;

public class TrackEditActivity extends Activity {

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.choice);
	}
	
	public void textView(){
		Intent i = new Intent(this, TrackEditDetailActivity.class);
		startActivity(i);		
	};
	public void imageView(){
		Intent i = new Intent(this, TrackEditMapActivity.class);
		startActivity(i);		
	};

}
