package fr.eurecom.hikingit;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

/*
 * TracksOverviewActivity displays the existing todo items
 * in a list
 * 
 * You can create new ones via the ActionBar entry "Insert"
 * You can delete existing ones via a long press on the item
 */

public class DisplayAllActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = DELETE_ID + 1;
	// private Cursor cursor;
	private SimpleCursorAdapter adapter;

	// private location

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.track_list);
		this.getListView().setDividerHeight(2);
		fillData();
		registerForContextMenu(getListView());
	}

	// create the menu based on the XML defintion
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listmenu2, menu);
		return true;
	}

	// Reaction to the menu selection
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert:
			createTrack();
			return true;
		case R.id.mvis:
			displayVis();
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
		Intent i = new Intent(this, TrackEditActivity.class);
		startActivity(i);
	}

	private void displayVis() {
		Intent i = new Intent(this, SearchTrackActivity.class);
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
		String[] from = new String[] { TrackTable.COLUMN_TITLE,
				TrackTable.COLUMN_DIFFICULTY };
		// Fields on the UI to which we map
		int[] to = new int[] { R.id.label, R.id.diff };

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
		String[] projection = { TrackTable.COLUMN_ID, TrackTable.COLUMN_TITLE,
				TrackTable.COLUMN_DIFFICULTY };
		CursorLoader cursorLoader = new CursorLoader(this,
				TrackContentProvider.CONTENT_URI, projection, null, null, null);
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

}