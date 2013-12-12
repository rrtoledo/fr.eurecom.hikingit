package fr.eurecom.hikingit;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
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
	private TextView mVis;

	private Uri trackUri;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.track_detail);

		mDifficulty = (TextView) findViewById(R.id.difficulty);
		mNbCoords = (TextView) findViewById(R.id.nbCoords);
		mTitleText = (TextView) findViewById(R.id.track_edit_title);
		mSummaryText = (TextView) findViewById(R.id.track_edit_summary);
		mDuration = (TextView) findViewById(R.id.duration);
		mPos = (TextView) findViewById(R.id.tvpos);
		mVis = (TextView) findViewById(R.id.tvvis);

		Bundle extras = getIntent().getExtras();

		// check from the saved Instance
		trackUri = (bundle == null) ? null : (Uri) bundle
				.getParcelable(TrackContentProvider.CONTENT_ITEM_TYPE);

		// Or passed from the other activity
		if (extras != null) {
			trackUri = extras
					.getParcelable(TrackContentProvider.CONTENT_ITEM_TYPE);

			fillData(trackUri);
		}
	}

	private void fillData(Uri uri) {
		String[] projection = { TrackTable.COLUMN_DIFFICULTY,
				TrackTable.COLUMN_TITLE, TrackTable.COLUMN_SUMMARY,
				TrackTable.COLUMN_NBCOORDS, TrackTable.COLUMN_DURATION,
				TrackTable.COLUMN_COORDS, TrackTable.COLUMN_FLAGS };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {

			cursor.moveToFirst();

			String difficulty = cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_DIFFICULTY));
			mDifficulty.setText(difficulty, null);

			String nbCoordonates = cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_NBCOORDS));
			mNbCoords.setText(nbCoordonates, null);

			String title = cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_TITLE));
			mTitleText.setText(title, null);

			String summary = cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_SUMMARY));
			mSummaryText.setText(summary, null);

			String duration = cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_DURATION));
			mDuration.setText(duration, null);

			
			String coords = cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_COORDS));
			mPos.setText(coords, null);

			String vis = cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_FLAGS));
			mVis.setText(vis, null);
			

			// always close the cursor
			cursor.close();
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