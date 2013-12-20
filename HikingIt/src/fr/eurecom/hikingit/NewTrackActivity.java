package fr.eurecom.hikingit;

import fr.eurecom.hikingit.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class NewTrackActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.new_track);
	}
	
	public void pickTrack(View v){
		Intent i = new Intent(this, PickFragmentActivity.class);
		startActivity(i);		
	};
	public void createTrack(View v){
		Intent i = new Intent(this, CreateFragmentActivity.class);
		startActivity(i);		
	};
	public void recordTrack(View v){
		Toast.makeText(this, "To be implemented",
				Toast.LENGTH_LONG).show();		
	};
}
