package fr.eurecom.hikingit;

import fr.eurecom.hikingit.AboutActivity;
import fr.eurecom.hikingit.AccountActivity;
import fr.eurecom.hikingit.MyTracksActivity;
import fr.eurecom.hikingit.TrackEditActivity;
import fr.eurecom.hikingit.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class HomeActivity extends Activity {

	MediaPlayer mplayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			showGPSDisabledAlertToUser();
		}
		/*
		 * requestWindowFeature(Window.FEATURE_NO_TITLE);
		 * getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 */

		setContentView(R.layout.activity_home);

		Intent intent = null;
		mplayer = MediaPlayer.create(this, R.raw.button_30);

		Button button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(HomeActivity.this, AccountActivity.class);
				startActivity(i);
				mplayer.start();
			}
		});

		Button button2 = (Button) findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(HomeActivity.this,
						DisplayFragmentActivity.class);
				startActivity(i);
				mplayer.start();
			}
		});

		Button button3 = (Button) findViewById(R.id.button3);
		button3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(HomeActivity.this,
						TrackEditActivity.class);
				startActivity(i);
				mplayer.start();
			}
		});

		Button button4 = (Button) findViewById(R.id.button4);
		button4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(HomeActivity.this, AboutActivity.class);
				startActivity(i);
				mplayer.start();
			}
		});

	}

	private void showGPSDisabledAlertToUser() {
		Log.w("fr.eurecom.hikingit", "alert called ");
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setTitle("GPS disabled")
				.setMessage(
						"GPS is disabled in your device. Would you like to enable it?")
				.setCancelable(false)
				.setPositiveButton("Enable GPS",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent callGPSSettingIntent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(callGPSSettingIntent);
							}
						});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

}
