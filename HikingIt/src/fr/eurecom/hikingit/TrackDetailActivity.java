package fr.eurecom.hikingit;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import fr.eurecom.hikingit.contentprovider.TrackContentProvider;
import fr.eurecom.hikingit.database.TrackTable;
import fr.eurecom.hikingit.R;

/*
 * TrackDetailActivity allows to enter a new track item 
 * or to change an existing
 */
public class TrackDetailActivity extends Activity {
	private TextView mDifficulty;
	private TextView mNbCoords;
	private TextView mTitleText;
	private TextView mSummaryText;
	private TextView mDuration;
	private TextView mPos;

	private Uri trackUri;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.track_detail);

		mDifficulty = (TextView) findViewById(R.id.tdDifficulty);
		mNbCoords = (TextView) findViewById(R.id.tdNbCoords);
		mTitleText = (TextView) findViewById(R.id.track_edit_title);
		mSummaryText = (TextView) findViewById(R.id.tdSummary);
		mDuration = (TextView) findViewById(R.id.tdDuration);
		mPos = (TextView) findViewById(R.id.tdPosition);

		Bundle extras = getIntent().getExtras();
		Log.w("fr.eurecom.hikingit",
				"getting extra : " + extras.toString());

		// check from the saved Instance
		trackUri = (bundle == null) ? null : (Uri) bundle
				.getParcelable(TrackContentProvider.CONTENT_ITEM_TYPE);

		// Or passed from the other activity
		if (extras != null) {
			Log.w("fr.eurecom.hikingit","there is extra");
			trackUri = extras
					.getParcelable(TrackContentProvider.CONTENT_ITEM_TYPE);

			Toast.makeText(TrackDetailActivity.this, "extras : " + extras,
					Toast.LENGTH_LONG).show();
			fillData(trackUri);
		}
	}

	private void fillData(Uri uri) {
		Log.w("fr.eurecom.hikingit","in fill data");
		Log.w("fr.eurecom.hikingit",
				"track detail fill uri  : " + uri.toString());
		
	    String[] projection = { TrackTable.COLUMN_TITLE, TrackTable.COLUMN_SUMMARY,
	    		TrackTable.COLUMN_DURATION, TrackTable.COLUMN_DIFFICULTY,
	    		TrackTable.COLUMN_NBCOORDS, TrackTable.COLUMN_COORDS,
	    		TrackTable.COLUMN_STARTX, TrackTable.COLUMN_STARTY,
	    		TrackTable.COLUMN_FLAGS, TrackTable.COLUMN_SCORE,
	    		TrackTable.COLUMN_REP, TrackTable.COLUMN_PIC};
	    
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null && cursor.moveToFirst()) {
			cursor.moveToFirst();
			Log.w("fr.eurecom.hikingit","in cursor");
		
			String title = cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_TITLE));
			mTitleText.setText(title, null);
			
			String summary = cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_SUMMARY));
			mSummaryText.setText(summary, null);
			
			String duration = cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_DURATION));
			mDuration.setText(duration, null);

			String difficulty = cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_DIFFICULTY));
			mDifficulty.setText(difficulty);

			String nbCoordonates = cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_NBCOORDS));
			mNbCoords.setText(nbCoordonates, null);

			String coords = cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_COORDS));
			mPos.setText(coords, null);
			
			// always close the cursor
			cursor.close();
		}
		else
		{
			Toast.makeText(TrackDetailActivity.this, "Null cursor",
					Toast.LENGTH_LONG).show();
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(TrackContentProvider.CONTENT_ITEM_TYPE, trackUri);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	private void saveState() {
	}
}