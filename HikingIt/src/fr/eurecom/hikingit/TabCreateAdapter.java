package fr.eurecom.hikingit;

import fr.eurecom.hikingit.CreateMapFragment;
import fr.eurecom.hikingit.CreateInfoFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class TabCreateAdapter extends FragmentPagerAdapter {
	
	
 
    public TabCreateAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            // Top Rated fragment activity
            return new CreateInfoFragment();
        case 1:
        	
        	
            // Games fragment activity
            return new CreateMapFragment();
        }
 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }
 
}
