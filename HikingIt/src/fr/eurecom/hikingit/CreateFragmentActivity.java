package fr.eurecom.hikingit;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import fr.eurecom.hikingit.R;
import fr.eurecom.hikingit.TabPagerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class CreateFragmentActivity extends FragmentActivity implements
		ActionBar.TabListener, OnFragmentClickListener {
	String title;
	String summary;
	String difficulty;
	String coords;
	String reputation;
	String startX;
	String startY;
	String pics;
	String score;
	String flags;
	String nbcoords;
	String duration;
	int count = 0;

	private ViewPager viewPager;
	private TabPagerAdapter mAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { "Info", "Edit Map" };
	public int status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_viewer);

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
			mAdapter = new TabPagerAdapter(getSupportFragmentManager());

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
		case 0:
			Log.w("fr.eurecom.hikingit","received coords");
			String position = (String) object;
			int ind = 0;
			int nb =-1;
			
			int index = 0;
			int index2 = 0;

			while (ind!=-1)
			{
				nb++;
				ind = position.indexOf("(", ind+1);
				Log.w("fr.eurecom.hikingit","ind "+ind);
			}
			Log.w("fr.eurecom.hikingit","nb "+nb);
			
			coords="";
			for (int j = 0; j < nb; j++) {
				coords+="(";
				index = position.indexOf("(", index2);
				index2 = position.indexOf(",", index);
				Log.w("fr.eurecom.hikingit",j+" index "+index+" index2 "+index2);
				double lat = Double.valueOf(position.substring(index + 1,
						index2));
				index = position.indexOf(")", index2);
				Log.w("fr.eurecom.hikingit","index "+index);
				coords+=lat+";";
				double lgt = Double.valueOf(position.substring(index2 + 1,
						index));
				coords+=lgt+")";
				Log.w("fr.eurecom.hikingit","fin it "+j);
			}
			Log.w("fr.eurecom.hikingit","fin");
			nbcoords = Integer.toString(nb);
			startX = coords.substring(1, coords.indexOf(";"));
			startY = coords.substring(coords.indexOf(";")+1,
					 coords.indexOf(")"));
			Log.w("fr.eurecom.hikingit","received : "+
					 nbcoords+", "+coords+", "+startX+", "+startY);
			count += 1;
			if (count == 3) {
				count = 0;
				Log.w("fr.eurecom.hikingit","received coords, count = 3 !!");
				// send to database
			}
			break;

		case 1:
			Log.w("fr.eurecom.hikingit","received info");
			String[] info = (String[]) object;
			title = info[0];
			summary = info[1];
			difficulty = info[2];
			pics = info[4];
			duration = info[3];
			score = duration;
			reputation = "0;0";
			flags = "3";
			count+=2;
			if (count == 3) {
				count = 0;
				Log.w("fr.eurecom.hikingit","received info, count = 3 !!");
				// send to database
			}
			break;
		}
		
	}

}