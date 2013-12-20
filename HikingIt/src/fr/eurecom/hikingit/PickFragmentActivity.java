package fr.eurecom.hikingit;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import fr.eurecom.hikingit.R;
import fr.eurecom.hikingit.TabPickAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;


public class PickFragmentActivity extends FragmentActivity implements ActionBar.TabListener {
	 private ViewPager viewPager;
	 private TabPickAdapter mAdapter;
	 private ActionBar actionBar;
	    // Tab titles
	 private String[] tabs = { "Pick on List", "Pick on Map" };
	 public int status;
	 private int fragmentIds[];
	 private static int nbfrag=0;
	 
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.picktab_viewer);
	        
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
	        viewPager = (ViewPager) findViewById(R.id.display_pager);
	        actionBar = getActionBar();
	        mAdapter = new TabPickAdapter(getSupportFragmentManager());
	 
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
	    
	    public void onAttachFragment(Fragment fragment) {
	        // TODO Auto-generated method stub
	    	Log.w("fr.eurecom.hikingit","onAttachFragment");
	        super.onAttachFragment(fragment);
	        fragmentIds[nbfrag] = fragment.getId();
	        nbfrag++;
	    }
	    
	    
	    public void onStop(){	
	    	super.onStop();
	    }
}