package fr.eurecom.hikingit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import fr.eurecom.hikingit.contentprovider.TrackContentProvider;
import fr.eurecom.hikingit.database.TrackTable;

public class EditInfoFragment extends Fragment {
	private Spinner mDifficulty;
	private EditText mTitleText;
	private EditText mSummaryText;
	private EditText mDuration;

	private Uri trackUri;

	private OnFragmentClickListener mListener;
	private String title = "";
	private String summary = "";
	private String difficulty = "";
	private String pics = "";
	private String duration = "";
	private int id;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.editinfo_fragment, container,
				false);
		mDifficulty = (Spinner) rootView.findViewById(R.id.difficulty);
		mTitleText = (EditText) rootView.findViewById(R.id.track_edit_title);
		mSummaryText = (EditText) rootView.findViewById(R.id.tdSummary);
		mDuration = (EditText) rootView.findViewById(R.id.tdDuration);

		setHasOptionsMenu(true);

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

		return rootView;
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.info_fragment, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implements listener");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.w("fr.eurecom.hikingit", "Title " + mTitleText.getText());
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.item1:
			if (TextUtils.isEmpty(mTitleText.getText().toString())
					|| TextUtils.isEmpty(mSummaryText.getText().toString())
					|| TextUtils.isEmpty(mDuration.getText().toString())) {

				Log.w("fr.eurecom.hikingit",
						"Sending error from CreateInfoFragment");
				mListener.OnFragmentClick(0, "Information");

				Log.w("fr.eurecom.hikingit",
						"Error sent from CreateInfoFragment");
			} else {

				title = mTitleText.getText().toString();
				summary = mSummaryText.getText().toString();
				difficulty = (String) mDifficulty.getSelectedItem();
				duration = mDuration.getText().toString();
				pics = "";

				if (pics.length() == 0) {
					Log.w("fr.eurecom.hikingit",
							"Sending info from CreateInfoFragment, no pic");
					String info = "[" + title + "]" + "[" + summary + "]" + "["
							+ difficulty + "]" + "[" + duration + "]" + "[ ]"+"["+id+"]";
					mListener.OnFragmentClick(1, info);
				} else {
					Log.w("fr.eurecom.hikingit",
							"Sending info from CreateInfoFragment");
					String info[] = { title, summary, difficulty, duration,
							pics };
					mListener.OnFragmentClick(1, info);
				}
				Log.w("fr.eurecom.hikingit",
						"Info sent from CreateInfoFragment");
			}
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void fillData(Uri uri) {
		String[] projection = { TrackTable.COLUMN_ID, TrackTable.COLUMN_TITLE,
				TrackTable.COLUMN_SUMMARY, TrackTable.COLUMN_DURATION,
				TrackTable.COLUMN_DIFFICULTY, TrackTable.COLUMN_NBCOORDS,
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
			
			id = Integer.valueOf(cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_ID)));

			mTitleText.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_TITLE)));

			mSummaryText.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_SUMMARY)));

			mDuration.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_DURATION)));

			// mPicture.setText(cursor.getString(cursor
			// .getColumnIndexOrThrow(TrackTable.COLUMN_PICS)));

			// always close the cursor
			cursor.close();
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
	}

}