package fr.eurecom.hikingit;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import fr.eurecom.hikingit.contentprovider.TrackContentProvider;
import fr.eurecom.hikingit.database.TrackTable;

public class CreateInfoFragment extends Fragment {
	private Spinner mDifficulty;
	// private Spinner mNbCoords;
	private EditText mTitleText;
	private EditText mSummaryText;
	private EditText mDuration;
	// private EditText mPos;
	private EditText mVis;

	private Uri trackUri;

	OnFragmentClickListener mListener;
	String title = new String();
	String summary = new String();
	String difficulty = new String();
	String pics = new String();
	String duration = new String();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_info, container,
				false);
		mDifficulty = (Spinner) getActivity().findViewById(R.id.difficulty);
		// mNbCoords = (Spinner) getActivity().findViewById(R.id.nbCoords);
		mTitleText = (EditText) getActivity().findViewById(
				R.id.track_edit_title);
		mSummaryText = (EditText) getActivity().findViewById(R.id.tdSummary);
		mDuration = (EditText) getActivity().findViewById(R.id.tdDuration);
		// mPos = (EditText) getActivity().findViewById(R.id.realposition);
		mVis = (EditText) getActivity().findViewById(R.id.tVis);

		Bundle extras = getActivity().getIntent().getExtras();

		// check from the saved Instance
		trackUri = (savedInstanceState == null) ? null
				: (Uri) savedInstanceState
						.getParcelable(TrackContentProvider.CONTENT_ITEM_TYPE);

		// Or passed from the other activity
		if (extras != null) {
			trackUri = extras
					.getParcelable(TrackContentProvider.CONTENT_ITEM_TYPE);

			fillData(trackUri);
		}
		Button confirmButton = (Button) getActivity().findViewById(
				R.id.track_edit_button);
		/*
		 * confirmButton.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View view) { if
		 * (TextUtils.isEmpty(mTitleText.getText().toString()) ||
		 * TextUtils.isEmpty(mSummaryText.getText().toString()) ) { makeToast();
		 * } else { saveData(); getActivity().setResult(-1);
		 * getActivity().finish(); } } });
		 */
		return rootView;
	}

	private void fillData(Uri uri) {
		String[] projection = { TrackTable.COLUMN_TITLE,
				TrackTable.COLUMN_SUMMARY, TrackTable.COLUMN_DURATION,
				TrackTable.COLUMN_DIFFICULTY, TrackTable.COLUMN_NBCOORDS,
				TrackTable.COLUMN_COORDS, TrackTable.COLUMN_STARTX,
				TrackTable.COLUMN_STARTY, TrackTable.COLUMN_FLAGS,
				TrackTable.COLUMN_SCORE, TrackTable.COLUMN_REP,
				TrackTable.COLUMN_PIC };

		Cursor cursor = getActivity().getContentResolver().query(uri,
				projection, null, null, null);
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

			/*
			 * String nbCoordonates = cursor.getString(cursor
			 * .getColumnIndexOrThrow(TrackTable.COLUMN_NBCOORDS));
			 * 
			 * for (int j = 0; j < mNbCoords.getCount(); j++) {
			 * 
			 * String str = (String) mNbCoords.getItemAtPosition(j); if
			 * (str.equalsIgnoreCase(nbCoordonates)) {
			 * mNbCoords.setSelection(j); } }
			 */

			mTitleText.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_TITLE)));

			mSummaryText.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_SUMMARY)));

			mDuration.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_DURATION)));

			/*
			 * mPos.setText(cursor.getString(cursor
			 * .getColumnIndexOrThrow(TrackTable.COLUMN_COORDS)));
			 */

			mVis.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_FLAGS)));

			// always close the cursor
			cursor.close();
		}
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.info_fragment, menu);
		super.onCreateOptionsMenu(menu, inflater);

	}
	
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			mListener = (OnFragmentClickListener) activity;
		}
		catch (ClassCastException e){
			throw new ClassCastException(activity.toString()+ "must implements listener");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Handle item selection
		switch (item.getItemId()) {
		case R.id.item1:
			
			if ( title.length() ==0 ||
			     summary.length() ==0 ||
			     duration.length() ==0 )
			{ 
				Toast.makeText(getActivity(),
						 "Fill info", Toast.LENGTH_LONG).show();
			}
			else{
				Log.w("fr.eurecom.hikingit","Sending info from CreateInfoFragment");
				if (pics.length() == 0)
					{
					String info[] = {title, summary, difficulty, duration, ""};
					mListener.OnFragmentClick(1, info);
					}
				else 
					{
					String info[] = {title, summary, difficulty, duration, pics};
					mListener.OnFragmentClick(1, info);
					}
				Log.w("fr.eurecom.hikingit","Info sent from CreateInfoFragment");
			}
			
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(TrackContentProvider.CONTENT_ITEM_TYPE, trackUri);
	}

	@Override
	public void onPause() {
		super.onPause();
		saveState();
	}

	private void saveState() {
	};

	/*
	 * private void saveData() { String difficulty = (String)
	 * mDifficulty.getSelectedItem(); //String nbcoords = (String)
	 * mNbCoords.getSelectedItem(); String title =
	 * mTitleText.getText().toString(); String summary =
	 * mSummaryText.getText().toString(); String duration =
	 * mDuration.getText().toString(); //String startX =
	 * mPos.getText().toString(); String startY = mPos.getText().toString();
	 * String rep = "0;0"; String score = Integer.toString(
	 * mDifficulty.getSelectedItemPosition() *
	 * Integer.valueOf(mDuration.getText().toString()) / 60 ); String pics =
	 * "picture_path";
	 * 
	 * int indexX = startX.indexOf(";"); Toast.makeText(getActivity(),
	 * "indexX : " + indexX, Toast.LENGTH_LONG).show(); if (indexX!=-1) { startX
	 * = startX.substring(1, indexX); } else startX = startX.substring(0, 2);
	 * 
	 * Toast.makeText(getActivity(), "StartX : " + startX,
	 * Toast.LENGTH_LONG).show();
	 * 
	 * int indexY = startY.indexOf(")"); Toast.makeText(getActivity(),
	 * "indexY : " + indexY, Toast.LENGTH_LONG).show(); if (indexY!=-1) { startY
	 * = startY.substring(indexX+1, indexY); } else startY = startY.substring(0,
	 * 2);
	 * 
	 * Toast.makeText(getActivity(), "StartY : " + startY,
	 * Toast.LENGTH_LONG).show();
	 * 
	 * String coords = mPos.getText().toString(); String flags =
	 * mVis.getText().toString();
	 * 
	 * 
	 * // only save if either summary or description // is available
	 * 
	 * if (title.length() == 0 && summary.length() == 0) { return; }
	 * ContentValues values = new ContentValues();
	 * 
	 * values.put(TrackTable.COLUMN_TITLE, title);
	 * values.put(TrackTable.COLUMN_SUMMARY, summary);
	 * values.put(TrackTable.COLUMN_DURATION, duration);
	 * values.put(TrackTable.COLUMN_DIFFICULTY, difficulty);
	 * values.put(TrackTable.COLUMN_NBCOORDS, nbcoords);
	 * values.put(TrackTable.COLUMN_COORDS, coords);
	 * values.put(TrackTable.COLUMN_STARTX, startX);
	 * values.put(TrackTable.COLUMN_STARTY, startY);
	 * values.put(TrackTable.COLUMN_FLAGS, flags);
	 * values.put(TrackTable.COLUMN_SCORE, score);
	 * values.put(TrackTable.COLUMN_REP, rep); values.put(TrackTable.COLUMN_PIC,
	 * pics);
	 * 
	 * Toast.makeText(getActivity(),"Uri : "+ trackUri + "values "+ values ,
	 * Toast.LENGTH_LONG).show();
	 * 
	 * 
	 * if (trackUri == null) { // New track trackUri =
	 * getActivity().getContentResolver().insert(
	 * TrackContentProvider.CONTENT_URI, values);
	 * Toast.makeText(getActivity(),"new Uri : "+ trackUri + "values "+values ,
	 * Toast.LENGTH_LONG).show();
	 * 
	 * 
	 * } else { // Update track Toast.makeText(getActivity(),"Uri : "+trackUri,
	 * Toast.LENGTH_LONG).show();
	 * 
	 * getActivity().getContentResolver().update(trackUri, values, null, null);
	 * } }
	 */
	private void makeToast() {
		Toast.makeText(getActivity(), "Please maintain a title and a summary",
				Toast.LENGTH_LONG).show();
	}

}
