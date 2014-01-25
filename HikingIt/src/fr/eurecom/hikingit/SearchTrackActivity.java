package fr.eurecom.hikingit;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import fr.eurecom.hikingit.contentprovider.TrackContentProvider;
import fr.eurecom.hikingit.database.TrackTable;

public class SearchTrackActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = DELETE_ID + 1;
	// private Cursor cursor;
	private SimpleCursorAdapter adapter;
	// private location
	private double longitude;
	private double latitude;
	private double margin = 5;
	private double marginRefresh = 10;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.track_list);

		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		LocationListener ll = new mylocationlistener();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);

		this.getListView().setDividerHeight(2);
		fillData();
		registerForContextMenu(getListView());
	}

	// create the menu based on the XML defintion
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listmenu, menu);
		return true;
	}

	// Reaction to the menu selection
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert:
			createTrack();
			return true;
		case R.id.all:
			displayAll();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			Uri uri = Uri.parse(TrackContentProvider.CONTENT_URI + "/"
					+ info.id);
			getContentResolver().delete(uri, null, null);
			fillData();
			return true;

		case EDIT_ID:
			AdapterContextMenuInfo inf = (AdapterContextMenuInfo) item
					.getMenuInfo();
			Intent i = new Intent(this, TrackEditDetailActivity.class);
			Uri trackUri = Uri.parse(TrackContentProvider.CONTENT_URI + "/"
					+ inf.id);
			i.putExtra(TrackContentProvider.CONTENT_ITEM_TYPE, trackUri);
			
			startActivity(i);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void createTrack() {
		Intent i = new Intent(this, TrackEditDetailActivity.class);
		startActivity(i);
	}
	
	private void displayAll(){
		Intent i = new Intent(this, DisplayAllActivity.class);
		startActivity(i);
	}

	// Opens the second activity if an entry is clicked
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent i = new Intent(this, TrackDetailActivity.class);
		Uri trackUri = Uri.parse(TrackContentProvider.CONTENT_URI + "/" + id);
		
		i.putExtra(TrackContentProvider.CONTENT_ITEM_TYPE, trackUri);

		startActivity(i);
	}

	private void fillData() {

		// Fields from the database (projection)
		// Must include the _id column for the adapter to work
		
		String[] projection = { TrackTable.COLUMN_ID, TrackTable.COLUMN_TITLE, TrackTable.COLUMN_DIFFICULTY };
        String selection = "flags = ? AND startX < ? AND startY < ?";
        
        double limitX= longitude + margin;
        String lgt = String.valueOf(limitX);
        
        double limitY= latitude + margin;
        String ltt = String.valueOf(limitY);    
        
        String[] selectionArgs = {"1",lgt,ltt};
        String order = "difficulty";
        
		Cursor cursor = getContentResolver().query(
				TrackContentProvider.CONTENT_URI, projection, selection,
				selectionArgs, order);
		if (cursor != null)
		{
			Log.w("fr.eurecom.hikingit"," fill data cursor "+ cursor.toString() + " nb answers " + cursor.getCount());
		}
		
		String[] from = new String[] { TrackTable.COLUMN_TITLE, TrackTable.COLUMN_DIFFICULTY };
		// Fields on the UI to which we map
		int[] to = new int[] { R.id.label, R.id.order };

		getLoaderManager().initLoader(0, null, this);
		adapter = new SimpleCursorAdapter(this, R.layout.track_row, null, from,
				to, 0);

		setListAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
		menu.add(0, EDIT_ID, 0, R.string.menu_edit);
	}

	// creates a new loader after the initLoader () call
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { TrackTable.COLUMN_ID, TrackTable.COLUMN_TITLE, TrackTable.COLUMN_DIFFICULTY };
        String selection = "flags = ? AND startX < ? AND startY < ?";
        
        double limitX= longitude + margin;
        String lgt = String.valueOf(limitX);
        
        double limitY= latitude + margin;
        String ltt = String.valueOf(limitY);    
        
        String[] selectionArgs = {"1",lgt,ltt};
        String order = "difficulty";
        
		Cursor cursor = getContentResolver().query(
				TrackContentProvider.CONTENT_URI, projection, selection,
				selectionArgs, order);
		if (cursor != null)
		{
			Log.w("fr.eurecom.hikingit","cursor "+ cursor.toString() + " nb answers " + cursor.getCount());
		}
        
		CursorLoader cursorLoader = new CursorLoader(this,
				TrackContentProvider.CONTENT_URI, projection, selection, selectionArgs, order);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// data is not available anymore, delete reference
		adapter.swapCursor(null);
	}
	
	private class mylocationlistener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			if (location.getLatitude() > (latitude + marginRefresh)
					|| location.getLatitude() < (latitude - marginRefresh)
					|| location.getLongitude() > (longitude + marginRefresh)
					|| location.getLongitude() < (longitude - marginRefresh)) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
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

}