package fr.eurecom.hikingit;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import fr.eurecom.hikingit.R.id;
import fr.eurecom.hikingit.R.layout;
import fr.eurecom.hikingit.contentprovider.TrackContentProvider;
import fr.eurecom.hikingit.database.TrackTable;
import fr.eurecom.hikingit.R;

/*
 * TrackDetailActivity allows to enter a new track item 
 * or to change an existing
 */
public class TrackEditDetailActivity extends Activity {
	private Spinner mDifficulty;
	private EditText mTitleText;
	private EditText mSummaryText;
	private EditText mDuration;
	private EditText mPos;
	private EditText mVis;
	private String mNbCoords = "";

	private Uri trackUri;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.track_edit);

		mDifficulty = (Spinner) findViewById(R.id.difficulty);
		mTitleText = (EditText) findViewById(R.id.track_edit_title);
		mSummaryText = (EditText) findViewById(R.id.tdSummary);
		mDuration = (EditText) findViewById(R.id.tdDuration);
		mPos = (EditText) findViewById(R.id.realposition);
		mVis = (EditText) findViewById(R.id.tVis);

		Button confirmButton = (Button) findViewById(R.id.track_edit_button);

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

		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (TextUtils.isEmpty(mTitleText.getText().toString())
						|| TextUtils.isEmpty(mSummaryText.getText().toString())) {
					makeToast();
				} else {
					saveData();
					setResult(RESULT_OK);
					finish();
				}
			}

		});
	}

	private void fillData(Uri uri) {
		String[] projection = { TrackTable.COLUMN_TITLE,
				TrackTable.COLUMN_SUMMARY, TrackTable.COLUMN_DURATION,
				TrackTable.COLUMN_DIFFICULTY, TrackTable.COLUMN_NBCOORDS,
				TrackTable.COLUMN_COORDS, TrackTable.COLUMN_STARTX,
				TrackTable.COLUMN_STARTY, TrackTable.COLUMN_FLAGS,
				TrackTable.COLUMN_SCORE, TrackTable.COLUMN_REP,
				TrackTable.COLUMN_PIC };

		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			cursor.moveToFirst();
			String difficulty = cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_DIFFICULTY));

			for (int i = 0; i < mDifficulty.getCount(); i++) {

				String s = (String) mDifficulty.getItemAtPosition(i);
				if (s.equalsIgnoreCase(difficulty)) {
					mDifficulty.setSelection(i);
				}
			}


			mTitleText.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_TITLE)));

			mSummaryText.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_SUMMARY)));

			mDuration.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_DURATION)));

			mPos.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_COORDS)));

			String coords =cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_COORDS));
			
			int index=0;
			int index2=0;
			Integer count=0;
			while (index!=-1 && index2!=-1)
			{
				Log.w("fr.eurecom.hikingit",index+" "+index2+" count "+count);

				count++;
				index2= coords.indexOf(";", index+1);
				index= coords.indexOf("(", index2+1);
			}
			
			Log.w("fr.eurecom.hikingit","count "+count);
			
			mNbCoords = count.toString();
			mVis.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_FLAGS)));

			// always close the cursor
			cursor.close();
			saveData();
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(TrackContentProvider.CONTENT_ITEM_TYPE, trackUri);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void saveData() {
		String difficulty = (String) mDifficulty.getSelectedItem();
		String nbcoords = mNbCoords;
		Log.w("fr.eurecom.hikingit","nbcoords "+nbcoords);

		String title = mTitleText.getText().toString();
		String summary = mSummaryText.getText().toString();
		String duration = mDuration.getText().toString();
		String startX = mPos.getText().toString().substring(
				1, mPos.getText().toString().indexOf(";"));
		String startY = mPos.getText().toString().substring(
				mPos.getText().toString().indexOf(";")+1,
				mPos.getText().toString().indexOf(")"));
		String rep = "0;0";
		String score = Integer.toString(mDifficulty.getSelectedItemPosition()
				* Integer.valueOf(mDuration.getText().toString()) / 60);
		String pics = "picture_path";

		String coords = mPos.getText().toString();
		String flags = mVis.getText().toString();
		
		
		int index=0;
		int index2=0;
		Integer count=0;
		while (index!=-1 && index2!=-1)
		{
			Log.w("fr.eurecom.hikingit",index+" "+index2+" count "+count);

			count++;
			index2= coords.indexOf(";", index+1);
			index= coords.indexOf("(", index2+1);
		}
		
		Log.w("fr.eurecom.hikingit","count "+count);
		
		nbcoords=Integer.toString(count);

		// only save if either summary or description
		// is available

		if (title.length() == 0 && summary.length() == 0) {
			return;
		}
		ContentValues values = new ContentValues();

		values.put(TrackTable.COLUMN_TITLE, title);
		values.put(TrackTable.COLUMN_SUMMARY, summary);
		values.put(TrackTable.COLUMN_DURATION, duration);
		values.put(TrackTable.COLUMN_DIFFICULTY, difficulty);
		values.put(TrackTable.COLUMN_NBCOORDS, nbcoords);
		values.put(TrackTable.COLUMN_COORDS, coords);
		values.put(TrackTable.COLUMN_STARTX, startX);
		values.put(TrackTable.COLUMN_STARTY, startY);
		values.put(TrackTable.COLUMN_FLAGS, flags);
		values.put(TrackTable.COLUMN_SCORE, score);
		values.put(TrackTable.COLUMN_REP, rep);
		values.put(TrackTable.COLUMN_PIC, pics);

		Toast.makeText(TrackEditDetailActivity.this,
				"Uri : " + trackUri + "values " + values, Toast.LENGTH_LONG)
				.show();

		if (trackUri == null) {
			// New track
			trackUri = getContentResolver().insert(
					TrackContentProvider.CONTENT_URI, values);
			Toast.makeText(TrackEditDetailActivity.this,
					"new Uri : " + trackUri + "values " + values,
					Toast.LENGTH_LONG).show();

		} else {
			// Update track
			Toast.makeText(TrackEditDetailActivity.this, "Uri : " + trackUri,
					Toast.LENGTH_LONG).show();

			getContentResolver().update(trackUri, values, null, null);
		}
	}

	private void makeToast() {
		Toast.makeText(TrackEditDetailActivity.this,
				"Please maintain a title and a summary", Toast.LENGTH_LONG)
				.show();
	}
}