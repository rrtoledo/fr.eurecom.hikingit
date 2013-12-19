package fr.eurecom.hikingit;

import MyTracks.TabsAdapterMyTracks;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;

public class MyTracksActivity extends FragmentActivity implements ActionBar.TabListener {
	
	private ViewPager viewPager;
	private TabsAdapterMyTracks mAdapter;
	private ActionBar actionBar;
	    
	    // Tab titles
	    private String[] tabs = { "Maps", "Comments" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mytracks);
	
	// Initialization
    viewPager = (ViewPager) findViewById(R.id.page);
    actionBar = getActionBar();
    mAdapter = new TabsAdapterMyTracks(getSupportFragmentManager());

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_tracks, menu);
		return true;
	}

}
