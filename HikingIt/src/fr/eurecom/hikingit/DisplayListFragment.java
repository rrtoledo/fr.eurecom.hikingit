package fr.eurecom.hikingit;

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
import fr.eurecom.hikingit.contentprovider.TrackContentProvider;
import fr.eurecom.hikingit.database.TrackTable;

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
	private double margin = 5;
	private double nonRefreshArea[] = { 0, 0, 0, 0 };
	private double marginRefresh = 10;
	private ListView listView;
	private Spinner spinner;
	private RadioGroup radio;
	private RadioButton nearButton;
	private RadioButton allButton;
	private TextView text;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View rootView = inflater.inflate(R.layout.displaylist_fragment,
				container, false);

		text = (TextView) rootView.findViewById(R.id.result);
		nearButton = (RadioButton) rootView.findViewById(R.id.RbNear);
		allButton = (RadioButton) rootView.findViewById(R.id.RbAll);

		listView = (ListView) rootView.findViewById(R.id.dlf_listview);
		registerForContextMenu(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.v("fr.eurecom/hikingit", "DLF onItemClick");
				Intent i = new Intent(getActivity(), TrackDetailActivity.class);
				Uri trackUri = Uri.parse(TrackContentProvider.CONTENT_URI + "/"
						+ id);
				i.putExtra(TrackContentProvider.CONTENT_ITEM_TYPE, trackUri);
				startActivity(i);
			}
		});

		spinner = (Spinner) rootView.findViewById(R.id.typeChoice);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				fillData();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		radio = (RadioGroup) rootView.findViewById(R.id.radioChoice);
		radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				Log.w("fr.eurecom.hikingit",
						"Radio changed " + radio.getCheckedRadioButtonId());
				fillData();
			}

		});

		locationListener = new mylocationlistener();

		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);

		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			allButton.setChecked(true);
			nearButton.setClickable(false);
		}

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				5000, 0, locationListener);

		// Creating a criteria object to retrieve provider
		Criteria criteria = new Criteria();

		// Getting the name of the best provider
		String provider = locationManager.getBestProvider(criteria, true);

		// Getting Current Location
		location = locationManager.getLastKnownLocation(provider);

		if (location != null) {
			longitude = location.getLongitude();
			latitude = location.getLatitude();
			Log.w("fr.eurecom.hikingit", "location initialized " + latitude
					+ " " + longitude);
			nonRefreshArea[0] = (latitude - marginRefresh);
			nonRefreshArea[1] = (latitude + marginRefresh);
			nonRefreshArea[2] = (longitude - marginRefresh);
			nonRefreshArea[3] = (longitude + marginRefresh);
			Log.w("fr.eurecom.hikingit",
					"ListFragment calling fillData beginning");
			fillData();
		}
		// registerForContextMenu(listView);
		return rootView;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
		menu.add(0, EDIT_ID, 0, R.string.menu_edit);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			allButton.setChecked(true);
			nearButton.setClickable(false);
		} else {
			allButton.setChecked(true);
			nearButton.setClickable(true);
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
		}
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
			Intent i = new Intent(getActivity(), TrackEditDetailActivity.class);
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

		String[] projection = { TrackTable.COLUMN_ID, TrackTable.COLUMN_TITLE,
				TrackTable.COLUMN_DIFFICULTY };

		int select = Integer.valueOf((int) spinner.getSelectedItemId() + 1);

		String radioId = Integer.toString(radio.getCheckedRadioButtonId());

		String order = "difficulty";

		Cursor cursor = null;

		if (nearButton.isChecked()) {
			Log.w("fr.eurecom.hikingit", "Nearby tracks asked ");

			double limitXMax = latitude + margin;
			String lttMax = String.valueOf(limitXMax);

			double limitXMin = latitude - margin;
			String lttMin = String.valueOf(limitXMin);

			double limitYMax = longitude + margin;
			String lgtMax = String.valueOf(limitYMax);

			double limitYMin = longitude - margin;
			String lgtMin = String.valueOf(limitYMin);

			if (select == 1) {
				String selection = "flags = ? OR flags = ? AND startX < ? AND startX > ? AND startY < ? AND startY > ?";
				String selectionArgs[] = { Integer.toString(select), "3",
						lttMax, lttMin, lgtMax, lgtMin };
				Log.w("fr.eurecom.hikingit", "selection args "
						+ selectionArgs[1] + " " + selectionArgs[2] + " "
						+ selectionArgs[3] + " " + selectionArgs[4]);

				cursor = getActivity().getContentResolver().query(
						TrackContentProvider.CONTENT_URI, projection,
						selection, selectionArgs, order);
			}

			else {
				String selection = "flags = ? AND startX < ? AND startX > ? AND startY < ? AND startY > ?";
				String selectionArgs[] = { Integer.toString(select), lttMax,
						lttMin, lgtMax, lgtMin };
				Log.w("fr.eurecom.hikingit", "selection args "
						+ selectionArgs[1] + " " + selectionArgs[2] + " "
						+ selectionArgs[3] + " " + selectionArgs[4]);

				cursor = getActivity().getContentResolver().query(
						TrackContentProvider.CONTENT_URI, projection,
						selection, selectionArgs, order);
			}
		} else {
			if (select == 1) {
				Log.w("fr.eurecom.hikingit", "All tracks asked " + radioId);
				String selection = "flags = ? OR flags = ?";
				String selectionArgs[] = { Integer.toString(select), "3" };

				cursor = getActivity().getContentResolver().query(
						TrackContentProvider.CONTENT_URI, projection,
						selection, selectionArgs, order);
			}
			else {
				Log.w("fr.eurecom.hikingit", "All tracks asked " + radioId);
				String selection = "flags = ?";
				String selectionArgs[] = { Integer.toString(select) };

				cursor = getActivity().getContentResolver().query(
						TrackContentProvider.CONTENT_URI, projection,
						selection, selectionArgs, order);
			}
		}

		if (cursor != null) {
			Log.w("fr.eurecom.hikingit", " ListFragment : fill data cursor "
					+ cursor.toString() + " nb answers " + cursor.getCount());
		}

		if (cursor.getCount() == 0) {
			text.setText("No tracks");
			Log.w("fr.eurecom.hikingit", " ListFragment empty cursor");
		} else {
			text.setText("Result :");
		}

		String[] from = new String[] { TrackTable.COLUMN_TITLE,
				TrackTable.COLUMN_DIFFICULTY };
		// Fields on the UI to which we map
		int[] to = new int[] { R.id.label, R.id.diff };

		getActivity().getLoaderManager().initLoader(0, null, this);

		adapter = new SimpleCursorAdapter(
				getActivity().getApplicationContext(), R.layout.track_row,
				cursor, from, to, 0);

		listView.setAdapter(adapter);

		Log.w("fr.eurecom.hikingit", " ListFragment adapter called");
	}

	// creates a new loader after the initLoader () call
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { TrackTable.COLUMN_ID, TrackTable.COLUMN_TITLE,
				TrackTable.COLUMN_DIFFICULTY };
		if (nearButton.isChecked()) {

		}
		String selection = "flags = ? or flags = ? AND startX < ? AND startX > ? AND startY < ? AND startY > ?";

		double limitXMax = latitude + margin;
		String lttMax = String.valueOf(limitXMax);

		double limitXMin = latitude - margin;
		String lttMin = String.valueOf(limitXMin);

		double limitYMax = longitude + margin;
		String lgtMax = String.valueOf(limitYMax);

		double limitYMin = longitude - margin;
		String lgtMin = String.valueOf(limitYMin);

		String[] selectionArgs = { "1", "3", lttMax, lttMin, lgtMax, lgtMin };
		String order = "difficulty";

		Cursor cursor = getActivity().getContentResolver().query(
				TrackContentProvider.CONTENT_URI, projection, selection,
				selectionArgs, order);
		if (cursor != null) {
			Log.w("fr.eurecom.hikingit",
					"ListFragment : onCreate Loader : cursor "
							+ cursor.toString() + " nb answers "
							+ cursor.getCount());
		} else {
			Log.w("fr.eurecom.hikingit",
					"ListFragment : onCreate Loader : empty cursor ");
		}

		CursorLoader cursorLoader = new CursorLoader(getActivity()
				.getApplicationContext(), TrackContentProvider.CONTENT_URI,
				projection, selection, selectionArgs, order);
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
			// TODO Auto-generated method stub

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
	
	public void onPause(){
		super.onPause();
		Log.w("fr.eurecom.hiking","List onPause");
	}
	
	public void onStop(){
		super.onStop();
		Log.w("fr.eurecom.hiking","List onStop");
		locationManager.removeUpdates(locationListener);
		
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			allButton.setChecked(true);
			nearButton.setClickable(false);
		} else {
			allButton.setChecked(true);
			nearButton.setClickable(true);
		}
	}
}