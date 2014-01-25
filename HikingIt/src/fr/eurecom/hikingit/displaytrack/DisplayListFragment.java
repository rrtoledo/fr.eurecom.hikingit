package fr.eurecom.hikingit.displaytrack;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Spinner;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;
import fr.eurecom.hikingit.R;
import fr.eurecom.hikingit.SelectedTrackActivity;
import fr.eurecom.hikingit.TrackDetailActivity;
import fr.eurecom.hikingit.R.id;
import fr.eurecom.hikingit.R.layout;
import fr.eurecom.hikingit.R.string;
import fr.eurecom.hikingit.contentprovider.TrackContentProvider;
import fr.eurecom.hikingit.database.TrackTable;
import fr.eurecom.hikingit.edittrack.EditFragmentActivity;

public class DisplayListFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = DELETE_ID + 1;
	private SimpleCursorAdapter adapter;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private Location location;
	private double longitude;
	private double latitude;
	private double nonRefreshArea[] = { 0, 0, 0, 0 };
	private double marginRefresh = 10;
	private ListView listView;
	private TextView text;
	// Filter variables
	int category = 0;
	int order = 0;
	double margin = 10;
	boolean near = true;
	String traveled = "0";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.v("fr.eurecom/hikingit", "List onCreateView");
		View rootView = inflater.inflate(R.layout.displaylist_fragment,
				container, false);

		text = (TextView) rootView.findViewById(R.id.result);

		listView = (ListView) rootView.findViewById(R.id.dlf_listview);
		registerForContextMenu(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.v("fr.eurecom/hikingit", "DLF onItemClick");
				Intent i = new Intent(getActivity(),
						SelectedTrackActivity.class);
				Uri trackUri = Uri.parse(TrackContentProvider.CONTENT_URI + "/"
						+ id);
				i.putExtra(TrackContentProvider.CONTENT_ITEM_TYPE, trackUri);
				i.putExtra("Caller", 0);
				startActivity(i);
			}
		});

		locationListener = new mylocationlistener();
		Log.v("fr.eurecom/hikingit", "List ll created");
		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		Log.v("fr.eurecom/hikingit", "List lm created");
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			near = false;
		else
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 10000, 0, locationListener);

		// Creating a criteria object to retrieve provider
		Criteria criteria = new Criteria();

		// Getting the name of the best provider
		String provider = locationManager.getBestProvider(criteria, true);
		Log.v("fr.eurecom/hikingit", "List provider created");
		
		// Getting Current Location
		location = locationManager.getLastKnownLocation(provider);
		
		if (location != null) {
			longitude = location.getLongitude();
			latitude = location.getLatitude();
			Log.w("fr.eurecom.hikingit", "List : location initialized " + latitude
					+ " " + longitude);
			nonRefreshArea[0] = (latitude - marginRefresh);
			nonRefreshArea[1] = (latitude + marginRefresh);
			nonRefreshArea[2] = (longitude - marginRefresh);
			nonRefreshArea[3] = (longitude + marginRefresh);
			Log.w("fr.eurecom.hikingit",
					"ListFragment calling fillData beginning");
			fillData();
		}
		// } else
		{
			// popup ask for enabling ...
		}
		// registerForContextMenu(listView);
		return rootView;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
		// if (spinner.getSelectedItemPosition() == 2) // if (categorySelected =
		// "2")
		menu.add(0, EDIT_ID, 0, R.string.menu_edit);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			Uri uri = Uri.parse(TrackContentProvider.CONTENT_URI + "/"
					+ info.id);
			getActivity().getContentResolver().delete(uri, null, null);
			fillData();
			return true;

		case EDIT_ID:
			AdapterContextMenuInfo inf = (AdapterContextMenuInfo) item
					.getMenuInfo();
			Intent i = new Intent(getActivity(), EditFragmentActivity.class);
			Uri trackUri = Uri.parse(TrackContentProvider.CONTENT_URI + "/"
					+ inf.id);
			i.putExtra(TrackContentProvider.CONTENT_ITEM_TYPE, trackUri);

			startActivity(i);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {

		Log.v("fr.eurecom/hikingit", "DLF ListItemClick");
		Intent i = new Intent(getActivity(), TrackDetailActivity.class);
		Uri trackUri = Uri.parse(TrackContentProvider.CONTENT_URI + "/" + id);
		i.putExtra(TrackContentProvider.CONTENT_ITEM_TYPE, trackUri);
		startActivity(i);
	}

	private void fillData() {

		// Fields from the database (projection)
		// Must include the _id column for the adapter to work

		Cursor cursor = getCursor();

		if (cursor != null) {
			Log.w("fr.eurecom.hikingit", " ListFragment : fill data cursor "
					+ " nb answers " + cursor.getCount());

			if (cursor.getCount() == 0) {
				text.setText("No tracks");
				Log.w("fr.eurecom.hikingit", " ListFragment empty cursor");
			} else {
				text.setText("Result :");
			}

			String[] from = new String[] { TrackTable.COLUMN_TITLE,
					TrackTable.COLUMN_DIFFICULTY };
			
			switch (order) {
			case 0:
				from[1]= TrackTable.COLUMN_DIFFICULTY;
				Log.w("fr.eurecom.hikingit", "order by 0 - diff");
				break;
			case 1:
				from[1]=TrackTable.COLUMN_DURATION;
				Log.w("fr.eurecom.hikingit", "order by 1 - time");
				break;
			case 2:
				from[1]= TrackTable.COLUMN_LENGTH;
				Log.w("fr.eurecom.hikingit", "order by 2 - dist");
				break;
			case 3:
				from[1]= TrackTable.COLUMN_DIFFICULTY;
				Log.w("fr.eurecom.hikingit", "order by 3 - name");
				break;
			default:
				from[1]= TrackTable.COLUMN_DIFFICULTY;
				Log.w("fr.eurecom.hikingit", "order by default - dur");
				break;
			}			

			int[] to = new int[] { R.id.label, R.id.order };
			
			adapter = new SimpleCursorAdapter(getActivity()
					.getApplicationContext(), R.layout.track_row, cursor, from,
					to, 0);

			listView.setAdapter(adapter);

			Log.w("fr.eurecom.hikingit", " ListFragment adapter called");
		}
	}

	// creates a new loader after the initLoader () call
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Cursor cursor = getCursor();
		if (cursor != null) {
			Log.w("fr.eurecom.hikingit",
					"ListFragment : onCreate Loader : cursor "
							+ cursor.toString() + " nb answers "
							+ cursor.getCount());
		} else {
			Log.w("fr.eurecom.hikingit",
					"ListFragment : onCreate Loader : empty cursor ");
		}

		CursorLoader cursorLoader = getCursorLoader();
		return cursorLoader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		// data is not available anymore, delete reference
		adapter.swapCursor(null);
	}

	private class mylocationlistener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			Log.w("fr.eurecom.hiking", "List : New location");
			if (location.getLatitude() < nonRefreshArea[0]
					|| location.getLatitude() > nonRefreshArea[1]
					|| location.getLongitude() < nonRefreshArea[2]
					|| location.getLongitude() > nonRefreshArea[3]) {

				latitude = location.getLatitude();
				longitude = location.getLongitude();

				nonRefreshArea[0] = (latitude - marginRefresh);
				nonRefreshArea[1] = (latitude + marginRefresh);
				nonRefreshArea[2] = (longitude - marginRefresh);
				nonRefreshArea[3] = (longitude + marginRefresh);

				fillData();
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			// If the provider is disabled, we look for any tracks
			near = false;

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (locationManager == null)
			locationManager = (LocationManager) getActivity().getSystemService(
					Context.LOCATION_SERVICE);
		Log.w("fr.eurecom.hiking", "------------- List onResume");
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			near = false;
			if (locationListener != null)
				locationManager.removeUpdates(locationListener);
		} else {
			if (locationListener != null)
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
		}
	}

	public void onPause() {
		super.onPause();
		Log.w("fr.eurecom.hiking", "------------- List onPause");
		if (locationListener != null)
		{
			Log.w("fr.eurecom.hiking", " ll exist ");
			if (locationManager != null)
			{
				Log.w("fr.eurecom.hiking", " lm exist ");
				locationManager.removeUpdates(locationListener);
				locationManager = null;
			}	
		}
	}

	public void onStop() {
		super.onStop();
		Log.w("fr.eurecom.hiking", "------------- List onStop");
		if (locationListener != null && locationManager !=null)
		{
			locationManager.removeUpdates(locationListener);
			Log.w("fr.eurecom.hiking", "ll exist");
			locationManager = null;
		}
	}

	public void onStart() {
		super.onStart();
		Log.w("fr.eurecom.hiking", "------------- List onStart");
	}

	public void getFilter(int cat, int ord, int dist, boolean close, String trav) {
		Log.w("fr.eurecom.hiking", "List getFilter called");
		Log.w("fr.eurecom.hiking", "Received : category "+cat+", order "+ord+", distance "+dist+", near "+close);
		category = cat;
		order = ord;
		margin = (double) dist/110;
		near = close;
		traveled = trav;
		Log.w("fr.eurecom.hiking", "Saved : category "+category+", order "+order+", distance "+margin+", near "+near);
		fillData();
	}

	public Cursor getCursor() {
		Log.w("fr.eurecom.hiking", "List getCursor called");
		String[] projection = { TrackTable.COLUMN_ID, TrackTable.COLUMN_TITLE,
				TrackTable.COLUMN_SUMMARY, TrackTable.COLUMN_DIFFICULTY };

		String groupBy = "";
		switch (order) {
		case 0:
			groupBy = "difficulty";
			projection[2]= TrackTable.COLUMN_DIFFICULTY;
			break;
		case 1:
			groupBy = "duration";
			projection[2]=TrackTable.COLUMN_DURATION;
			break;
		case 2:
			groupBy = "length";
			projection[2]= TrackTable.COLUMN_LENGTH;
			break;
		case 3:
			groupBy = "title";
			projection[2]= TrackTable.COLUMN_TITLE;
			break;
		default:
			groupBy = "difficulty";
			projection[2]= TrackTable.COLUMN_DIFFICULTY;
			break;
		}

		String selection = getSelection();
		String[] selectionArgs = getSelectionArgs();
		Log.w("fr.eurecom.hiking", "List getCursor args : selection "
				+ selection + ", category " + category);
		Cursor cursor = getActivity().getContentResolver().query(
				TrackContentProvider.CONTENT_URI, projection, selection,
				selectionArgs, groupBy);
		return cursor;
	}

	public CursorLoader getCursorLoader() {
		Log.w("fr.eurecom.hiking", "List getCursorLoader called");
		String[] projection = { TrackTable.COLUMN_ID, TrackTable.COLUMN_TITLE,
				TrackTable.COLUMN_DIFFICULTY };

		String groupBy = "";
		switch (order) {
		case 0:
			groupBy = "difficulty";
			projection[2]= TrackTable.COLUMN_DIFFICULTY;
			break;
		case 1:
			groupBy = "duration";
			projection[2]=TrackTable.COLUMN_DURATION;
			break;
		case 2:
			groupBy = "length";
			projection[2]= TrackTable.COLUMN_LENGTH;
			break;
		case 3:
			groupBy = "title";
			projection[2]= TrackTable.COLUMN_TITLE;
			break;
		default:
			groupBy = "difficulty";
			projection[2]= TrackTable.COLUMN_DIFFICULTY;
			break;
		}
		Log.w("fr.eurecom.hiking","List getCursorLoader : groupby"+groupBy+" , "+projection[2].toString());
		String selection = getSelection();
		String[] selectionArgs = getSelectionArgs();
		Log.w("fr.eurecom.hiking", "List getCursorLoader : selection "
				+ selection + ", category " + category);

		CursorLoader cursorLoader = new CursorLoader(getActivity()
				.getApplicationContext(), TrackContentProvider.CONTENT_URI,
				projection, selection, selectionArgs, groupBy);
		Log.w("fr.eurecom.hiking", "CursorLoader created");
		return cursorLoader;
	}

	public String getSelection() {
		String selection;
		if (near) {
			Log.w("fr.eurecom.hikingit", "List : getSelection : near true");
			if (category == 0)
				selection = "traveled = ? AND startX < ? AND startX > ? AND startY < ? AND startY > ? ";
			else
				selection = "flags = ? AND traveled = ? AND startX < ? AND startX > ? AND startY < ? AND startY > ? ";

		} else {
			Log.w("fr.eurecom.hikingit", "List : getSelection : near false");

			if (category == 0)
				selection = "traveled = ?";
			else
				selection = "flags = ? AND traveled = ?";
		}
		return selection;
	}

	public String[] getSelectionArgs() {
		if (near) {
			double limitMaxX = latitude + margin;
			String lttdMax = String.valueOf(limitMaxX);
			Log.w("fr.eurecom.fr", "latitude max : " + lttdMax);

			double limitMinX = latitude - margin;
			String lttdMin = String.valueOf(limitMinX);
			Log.w("fr.eurecom.fr", "latitude min : " + lttdMin);

			double limitMaxY = longitude + margin;
			String lgtdMax = String.valueOf(limitMaxY);
			Log.w("fr.eurecom.fr", "longitude max : " + lgtdMax);

			double limitMinY = longitude - margin;
			String lgtdMin = String.valueOf(limitMinY);
			Log.w("fr.eurecom.fr", "longitude min : " + lgtdMin);

			if (category == 0) {
				String[] args1 = {traveled, lttdMax, lttdMin, lgtdMax, lgtdMin };
				return args1;
			} else {
				String[] args1 = {String.valueOf(category), traveled, lttdMax, lttdMin,
						lgtdMax, lgtdMin };
				return args1;
			}
		} else {
			if (category == 0) {
				String[] args1 = {traveled};
				return args1;
			} else {
				String[] args1 = {String.valueOf(category), traveled};
				return args1;
			}
		}
	}
}