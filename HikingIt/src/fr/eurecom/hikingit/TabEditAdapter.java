package fr.eurecom.hikingit;

import fr.eurecom.hikingit.EditMapFragment;
import fr.eurecom.hikingit.EditInfoFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
 
public class TabEditAdapter extends FragmentPagerAdapter {
	
	
 
    public TabEditAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
 
        switch (index) {
        case 0:
            // Top Rated fragment activity
            return new EditInfoFragment();
        case 1:
        	
        	
            // Games fragment activity
            return new EditMapFragment();
        }
 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }
 
}