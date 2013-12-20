package fr.eurecom.hikingit.displaytrack;

import fr.eurecom.hikingit.displaytrack.DisplayListFragment;
import fr.eurecom.hikingit.displaytrack.DisplayMapFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class TabDisplayAdapter extends FragmentPagerAdapter {
	
	
 
    public TabDisplayAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
        switch (index) {
        case 0:
            // Top Rated fragment activity
            return new DisplayListFragment();
        case 1:        	
            // Games fragment activity
            return new DisplayMapFragment();
        }
 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }
 
}