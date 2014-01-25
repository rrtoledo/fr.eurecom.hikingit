package fr.eurecom.hikingit.displaytrack;

import fr.eurecom.hikingit.displaytrack.DisplayListFragment;
import fr.eurecom.hikingit.displaytrack.DisplayMapFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import java.util.Map;
 
public class TabDisplayAdapter extends FragmentPagerAdapter {
	 ArrayMap<Integer, Fragment> fragmentDictionnary = new ArrayMap<Integer, Fragment>();
	
 
    public TabDisplayAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
        switch (index) {
        case 2:
            // Top Rated fragment activity
    		DisplayListFragment listFragment = new DisplayListFragment();
    		Log.w("fr.eurecom.hiking","------------- ListFragment created ");
    		fragmentDictionnary.put(0, listFragment);
            return listFragment;
        case 1:        	
            // Games fragment activity
    		DisplayMapFragment mapFragment = new DisplayMapFragment();
    		Log.w("fr.eurecom.hiking","------------- MapFragment created ");
    		fragmentDictionnary.put(1, mapFragment);
            return mapFragment;
        case 0:
        	DisplayFilterFragment filterFragment = new DisplayFilterFragment();
    		Log.w("fr.eurecom.hiking","------------- FilterFragment created ");
    		fragmentDictionnary.put(2, filterFragment);
            return filterFragment; 
        default:
        	return null;
        }
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

    public Fragment getFragment(int position) {
        return fragmentDictionnary.get(position);
    }
    
    public Integer getDictSize(){
    	return fragmentDictionnary.size();
    }
    
	public void getFilter()
	{
		// does nothing
	}
    
}