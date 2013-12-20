package fr.eurecom.hikingit;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import fr.eurecom.hikingit.R;
import fr.eurecom.hikingit.TabEditAdapter;
import fr.eurecom.hikingit.contentprovider.TrackContentProvider;
import fr.eurecom.hikingit.database.TrackTable;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class EditFragmentActivity extends FragmentActivity implements
		ActionBar.TabListener, OnFragmentClickListener {
	private String difficulty;
	private String reputation;
	private String summary;
	private String coords;
	private String startX;
	private String startY;
	private String title;
	private String pics;
	private double duration;
	private double score;
	private int nbcoords;
	private int flags;
	private int id;

	private Uri trackUri;
	private int count = 0;
	private String content = "content://fr.eurecom.hikingit.contentprovider/";

	private ViewPager viewPager;
	private TabEditAdapter mAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { "Info", "Edit Map" };
	public int status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createtab_viewer);
		
		Bundle extras = getIntent().getExtras();

		// check from the saved Instance
		trackUri = (savedInstanceState == null) ? null
				: (Uri) savedInstanceState
						.getParcelable(TrackContentProvider.CONTENT_ITEM_TYPE);

		// Or passed from the other activity
		if (extras != null) {
			trackUri = extras
					.getParcelable(TrackContentProvider.CONTENT_ITEM_TYPE);
		}

		status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());

		if (status != ConnectionResult.SUCCESS) { // Google Play Services are
													// not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();

		} else {

			// Initilization
			viewPager = (ViewPager) findViewById(R.id.pager);
			actionBar = getActionBar();
			mAdapter = new TabEditAdapter(getSupportFragmentManager());

			viewPager.setAdapter(mAdapter);
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

			// Adding Tabs
			for (String tab_name : tabs) {
				actionBar.addTab(actionBar.newTab().setText(tab_name)
						.setTabListener(this));
			}

			viewPager
					.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

						@Override
						public void onPageSelected(int position) {
							// on changing the page
							// make respected tab selected
							actionBar.setSelectedNavigationItem(position);
						}

						@Override
						public void onPageScrolled(int arg0, float arg1,
								int arg2) {
						}

						@Override
						public void onPageScrollStateChanged(int arg0) {
						}
					});
		}
	}
	
	/*public void onBackPressed() {    
		Intent intent = new Intent(this, HomeActivity.class);
		//intent.setAction(Intent.ACTION_MAIN);
    //intent.addCategory(Intent);
    startActivity(intent);
	}


	public boolean onKeyDown (int keyCode, KeyEvent event){
	if (keyCode ==KeyEvent.KEYCODE_BACK ){
		onBackPressed();
		}
		return true;
	}*/

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	public void onFragmentClick(int action, Object object) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnFragmentClick(int action, Object object) {
		switch (action) {
		case 2:
			Log.w("fr.eurecom.hikingit", "Coords received " + object.toString());
			Parser(action, (String) object);

			Log.w("fr.eurecom.hikingit", "received : " + nbcoords + ", "
					+ coords + ", " + startX + ", " + startY);
			break;

			
		case 1:
			Log.w("fr.eurecom.hikingit", "Info received " + object.toString());
			Parser(action, (String) object);

			Log.w("fr.eurecom.hikingit", "received :" + " title " + title
					+ ", summary " + summary + ", difficulty " + difficulty
					+ ", duration" + duration + ", pics" + pics);
			Log.w("fr.eurecom.hikingit", "created : " + "reputation "
					+ reputation + ", flags " + flags + ", score " + score);
			break;

			
		case 0:
			Log.w("fr.eurecom.hikingit", "Error received");
			String error = (String) object;
			Toast.makeText(this, "Track not saved. " + error + " missing.",
					Toast.LENGTH_LONG).show();
			break;

			
		case -1:
			Log.w("fr.eurecom.hikingit", "action received " + action);
			Toast.makeText(this, "No GPS",
					Toast.LENGTH_LONG).show();
			break;
		}
		Log.w("fr.eurecom.hikingit", "Leaving from cases ");

	}

	private void Parser(int action, String object) {
		Log.w("fr.eurecom.hikingit", "Parser receives :" + action + " and "
				+ object);
		int start;
		int end;
		switch (action) {
		case 1:
			start = object.indexOf("[") + 1;
			end = object.indexOf("]");
			Log.w("fr.eurecom.hikingit", "Start" + start + " End " + end);
			title = object.substring(start, end);
			Log.w("fr.eurecom.hikingit", "title " + title);

			start = object.indexOf("[", end) + 1;
			end = object.indexOf("]", start);
			Log.w("fr.eurecom.hikingit", "Start" + start + " End " + end);
			summary = object.substring(start, end);
			Log.w("fr.eurecom.hikingit", "summary " + summary);

			start = object.indexOf("[", end) + 1;
			end = object.indexOf("]", start);
			Log.w("fr.eurecom.hikingit", "Start" + start + " End " + end);
			difficulty = object.substring(start, end);
			Log.w("fr.eurecom.hikingit", "difficulty " + difficulty);

			start = object.indexOf("[", end) + 1;
			end = object.indexOf("]", start);
			Log.w("fr.eurecom.hikingit", "Start" + start + " End " + end);
			duration = Double.valueOf(object.substring(start, end));
			Log.w("fr.eurecom.hikingit", "duration " + duration);

			start = object.indexOf("[", end) + 1;
			end = object.indexOf("]", start);
			Log.w("fr.eurecom.hikingit", "Start" + start + " End " + end);
			pics = object.substring(start, end);
			Log.w("fr.eurecom.hikingit", "pics " + pics);
			
			start = object.indexOf("[", end) + 1;
			end = object.indexOf("]", start);
			Log.w("fr.eurecom.hikingit", "Start" + start + " End " + end);
			id = Integer.valueOf(object.substring(start, end));
			Log.w("fr.eurecom.hikingit", "id " + id);

			
			if (difficulty.equals("Easy"))
				score = 0.5 * duration;
			if (difficulty.equals("Normal"))
				score = 1 * duration;
			if (difficulty.equals("Hard"))
				score = 3 * duration;
			reputation = "0;0";
			flags = 3;
			if (count != 2)
				Toast.makeText(this, "Add markers",
						Toast.LENGTH_LONG).show();
			if (count !=1)
				count++;
			break;

		case 2:
			start = object.indexOf("[") + 1;
			end = object.indexOf("]");
			Log.w("fr.eurecom.hikingit", "Start " + start + " End " + end);
			nbcoords = Integer.valueOf(object.substring(start, end));
			Log.w("fr.eurecom.hikingit", "nbcoords " + nbcoords);

			start = object.indexOf("[", end) + 1;
			end = object.indexOf("]", start);
			Log.w("fr.eurecom.hikingit", "Start" + start + " End " + end);
			coords = object.substring(start, end);

			start = object.indexOf("[", end) + 1;
			end = object.indexOf("]", start);
			Log.w("fr.eurecom.hikingit", "Start" + start + " End " + end);
			startX = object.substring(start, end);

			start = object.indexOf("[", end) + 1;
			end = object.indexOf("]", start);
			Log.w("fr.eurecom.hikingit", "Start" + start + " End " + end);
			startY = object.substring(start, end);
			if (count != 1)
				Toast.makeText(this, "Fill information",
						Toast.LENGTH_LONG).show();
			if (count != 2)
				count += 2;

			break;
		}
		Log.w("fr.eurecom.hikingit", "Finish Parsing" + action);
		if (count >= 3) {
			alertSaveTrack();
		}
	}
	
	private void alertSaveTrack() {
		Log.w("fr.eurecom.hikingit", "alert called ");
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setTitle("Save Track")
				.setMessage(
						" Have you finished editing?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								SaveTrack();
							}
						});
		alertDialogBuilder.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	private void SaveTrack() {
		count = 0;
		ContentValues values = new ContentValues();

		values.put(TrackTable.COLUMN_ID, id);
		values.put(TrackTable.COLUMN_TITLE, title);
		values.put(TrackTable.COLUMN_SUMMARY, summary);
		values.put(TrackTable.COLUMN_DURATION, duration);
		values.put(TrackTable.COLUMN_DIFFICULTY, difficulty);
		values.put(TrackTable.COLUMN_NBCOORDS, nbcoords);
		values.put(TrackTable.COLUMN_COORDS, coords);
		values.put(TrackTable.COLUMN_STARTX, startX);
		values.put(TrackTable.COLUMN_STARTY, startY);
		values.put(TrackTable.COLUMN_FLAGS, flags);
		values.put(TrackTable.COLUMN_SCORE, score);
		values.put(TrackTable.COLUMN_REP, reputation);
		values.put(TrackTable.COLUMN_PIC, pics);

		if (trackUri != null) {
			Log.w("fr.eurecom.hikingit", "Update Uri");
			getContentResolver().update(trackUri, values, null, null);
		}

		Toast.makeText(this, "Track created", Toast.LENGTH_LONG).show();
		finish();

	}

}