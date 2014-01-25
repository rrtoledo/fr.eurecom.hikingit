package fr.eurecom.hikingit.displaytrack;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import fr.eurecom.hikingit.R;
import fr.eurecom.hikingit.displaytrack.TabDisplayAdapter;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.util.Log;

public class DisplayFragmentActivity extends FragmentActivity implements
		ActionBar.TabListener, OnFragmentFilterListener {
	private ViewPager viewPager;
	private TabDisplayAdapter mAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { "Filter", "Map", "List" };
	public int status;
	private ArrayMap<Integer, Fragment> fragmentDict = new ArrayMap<Integer, Fragment>();
	private static int nbfrag = 0;
	int[] fragOrder = { 0, 1, 2 };
	android.support.v4.app.Fragment previousFragment;
	android.support.v4.app.Fragment currentFragment;
	android.support.v4.app.Fragment otherFragment;
	// Filter variables
	int category = 0;
	int order = 1;
	int distance = 10;
	boolean near = true;
	String traveled = "0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.displaytab_viewer);

		status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());

		if (status != ConnectionResult.SUCCESS) { // Google Play Services are
													// not available
			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();

		}

		// Initilization
		viewPager = (ViewPager) findViewById(R.id.display_pager);
		actionBar = getActionBar();
		mAdapter = new TabDisplayAdapter(getSupportFragmentManager());
		viewPager.setAdapter(mAdapter);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				Log.w("fr.eurecom.hiking",
						"------------- Activity page selected :" + position);
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		Log.w("fr.eurecom.hiking",
				"------------- Activity tab Reselected " + tab.getPosition());
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		Log.w("fr.eurecom.hiking",
				"------------- Activity tab Selected " + tab.getPosition());
		for (int i = 0; i < 3; i++) {
			Log.w("fr.eurecom.hiking", "#### " + i);
			if (i == tab.getPosition()) {
				currentFragment = mAdapter.getFragment(tab.getPosition());
				if (currentFragment != null) {
					Log.w("fr.eurecom.hiking",
							"------------- Activity tab Selected " + i
									+ " onResume called");
					currentFragment.onResume();
					updateFilter(currentFragment, tab.getPosition());
				}
			} else {
				android.support.v4.app.Fragment frag = mAdapter.getFragment(i);
				if (frag != null) {
					Log.w("fr.eurecom.hiking",
							"------------- Activity tab Selected " + i
									+ " onPause called");
					frag.onPause();
				}
			}
		}
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		Log.w("fr.eurecom.hiking", "------------- Activity tab Unselected "
				+ tab.getPosition());
		previousFragment = mAdapter.getFragment(tab.getPosition());
	}

	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);
		Log.w("fr.eurecom.hikingit", "------------- onAttachFragment");

		fragmentDict.put(nbfrag, fragment);
		nbfrag++;
		Log.w("fr.eurecom.hiking", "Activity : onAttach " + fragmentDict.size());
	}

	public void OnFragmentFilter(int action, Object object) {
		Log.w("fr.eurecom.hiking", "Message Received: " + action + ", "
				+ object + ".");
		switch (action) {
		case 0:
			category = Integer.valueOf((String) object);
			Log.w("fr.eurecom.hiking", "Category = " + category);
			break;

		case 1:
			order = Integer.valueOf((String) object);
			Log.w("fr.eurecom.hiking", "Order = " + order);
			break;

		case 2:
			distance = Integer.valueOf((String) object);
			Log.w("fr.eurecom.hiking", "Distance = " + distance);
			break;

		case 3:
			if (object == "1")
				near = true;
			else
				near = false;
			Log.w("fr.eurecom.hiking", "Near = " + near);
			break;
		case 4:
			traveled = (String) object;
			break;
		}
	}

	@Override
	public void onFragmentFilter(int action, Object object) {
		Log.w("fr.eurecom.hiking", "Message Received not overriden" + action);
	}

	public void updateFilter(android.support.v4.app.Fragment fragment,
			int position) {
		Log.w("fr.eurecom.hiking", "Activity : Upgrade Filter " + position);
		switch (position) {
		case 0:
			Log.w("fr.eurecom.hiking", "Activity : Upgrade Filter ListFrag");
			DisplayListFragment listFrag = (DisplayListFragment) fragment;
			listFrag.getFilter(category, order, distance, near, traveled);
			break;
		case 1:
			Log.w("fr.eurecom.hiking", "Activity : Upgrade Filter Map Frag");
			DisplayMapFragment mapFrag = (DisplayMapFragment) fragment;
			mapFrag.getFilter(category, order, distance, near, traveled);
			break;
		case 2:
			Log.w("fr.eurecom.hiking", "Activity : Upgrade Filter Filter frag");
			break;
		default:
			break;
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.w("fr.eurecom.hiking", "------------- Act onStop");
	}

}