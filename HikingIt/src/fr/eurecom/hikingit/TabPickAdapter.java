package fr.eurecom.hikingit;

import fr.eurecom.hikingit.PickMapFragment;
import fr.eurecom.hikingit.PickListFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class TabPickAdapter extends FragmentPagerAdapter {
	
	
 
    public TabPickAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
        switch (index) {
        case 0:
            // Top Rated fragment activity
            return new PickListFragment();
        case 1:        	
            // Games fragment activity
            return new PickMapFragment();
        }
 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }
 
}