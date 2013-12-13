package fr.eurecom.hikingit;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import fr.eurecom.hikingit.contentprovider.TrackContentProvider;
import fr.eurecom.hikingit.database.TrackTable;
import fr.eurecom.hikingit.R;

/*
 * TrackDetailActivity allows to enter a new track item 
 * or to change an existing
 */
public class TrackEditDetailActivity extends Activity {
	private Spinner mDifficulty;
	private Spinner mNbCoords;
	private EditText mTitleText;
	private EditText mSummaryText;
	private EditText mDuration;
	private EditText mPos;
	private EditText mVis;

	private Uri trackUri;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.track_edit);

		mDifficulty = (Spinner) findViewById(R.id.difficulty);
		mNbCoords = (Spinner) findViewById(R.id.nbCoords);
		mTitleText = (EditText) findViewById(R.id.track_edit_title);
		mSummaryText = (EditText) findViewById(R.id.track_edit_summary);
		mDuration = (EditText) findViewById(R.id.duration);
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
				if (TextUtils.isEmpty(mTitleText.getText().toString()) || 
					TextUtils.isEmpty(mSummaryText.getText().toString()) ) {
					makeToast();
				} else {
					setResult(RESULT_OK);
					finish();
				}
			}

		});
	}

	private void fillData(Uri uri) {
    String[] projection = { TrackTable.COLUMN_DIFFICULTY,
    		TrackTable.COLUMN_TITLE, TrackTable.COLUMN_SUMMARY,
    		TrackTable.COLUMN_NBCOORDS, TrackTable.COLUMN_DURATION,
    		TrackTable.COLUMN_STARTX, TrackTable.COLUMN_STARTY,
    		TrackTable.COLUMN_COORDS, TrackTable.COLUMN_FLAGS,
    		TrackTable.COLUMN_SCORE, TrackTable.COLUMN_PIC};
    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
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
      
      String nbCoordonates = cursor.getString(cursor
           .getColumnIndexOrThrow(TrackTable.COLUMN_NBCOORDS));

            for (int j = 0; j < mNbCoords.getCount(); j++) {

              String str = (String) mNbCoords.getItemAtPosition(j);
              if (str.equalsIgnoreCase(nbCoordonates)) {
                mNbCoords.setSelection(j);
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
      
      mVis.setText(cursor.getString(cursor
    		  .getColumnIndexOrThrow(TrackTable.COLUMN_FLAGS)));
      
      
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
		String difficulty = (String) mDifficulty.getSelectedItem();
		String nbcoords = (String) mNbCoords.getSelectedItem();
		String title = mTitleText.getText().toString();
		String summary = mSummaryText.getText().toString();
		String duration = mDuration.getText().toString();
		String startX =  mPos.getText().toString();
		String startY =  mPos.getText().toString();
		String score = "";
		String pics = "";
		
		int indexX = startX.indexOf(";");
		if (indexX!=-1)
		{
			startX.substring(1, indexX-1);
		}
		else startX.substring(0, 1);
		
		int indexY = startY.indexOf(")");
		if (indexY!=-1)
		{
			startY.substring(indexX+1, indexY-1);
		}
		else startY.substring(0, 1);
		
		String coords = mPos.getText().toString();
		String visibility = mVis.getText().toString();

		
		// only save if either summary or description
		// is available

		if (title.length() == 0 && summary.length() == 0) {
			return;
		}
		ContentValues values = new ContentValues();
		values.put(TrackTable.COLUMN_DIFFICULTY, difficulty);
		values.put(TrackTable.COLUMN_TITLE, title);
		values.put(TrackTable.COLUMN_SUMMARY, summary);
		values.put(TrackTable.COLUMN_NBCOORDS, nbcoords);
		values.put(TrackTable.COLUMN_DURATION, duration);
		values.put(TrackTable.COLUMN_STARTX, startX);
		values.put(TrackTable.COLUMN_STARTY, startY);
		values.put(TrackTable.COLUMN_COORDS, coords);
		values.put(TrackTable.COLUMN_FLAGS, visibility);
		values.put(TrackTable.COLUMN_SCORE, score);
		values.put(TrackTable.COLUMN_PIC, pics);
		
		if (trackUri == null) {
			// New track
			trackUri = getContentResolver().insert(
					TrackContentProvider.CONTENT_URI, values);
			Toast.makeText(TrackEditDetailActivity.this,"new Uri : "+ trackUri + "values "+values , Toast.LENGTH_LONG).show();

			
		} else {
			// Update track
			Toast.makeText(TrackEditDetailActivity.this,"Uri : "+trackUri, Toast.LENGTH_LONG).show();

			getContentResolver().update(trackUri, values, null, null);
		}
	}

	private void makeToast() {
		Toast.makeText(TrackEditDetailActivity.this, "Please maintain a title and a summary",
				Toast.LENGTH_LONG).show();
	}
}