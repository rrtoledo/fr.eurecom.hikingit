package fr.eurecom.hikingit.displaytrack;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import fr.eurecom.hikingit.R;

public class DisplayFilterFragment extends Fragment {
	private OnFragmentFilterListener mListener;
	private LocationManager locationManager;
	private Spinner spinnerCat;
	private Spinner spinnerOrder;
	private RadioGroup radio;
	private RadioButton nearButton;
	private RadioButton anyButton;
	private CheckBox traveled;
	private TextView seekDistanceTxt;
	private SeekBar seekDistance;
	private Switch geoEnable;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.w("fr.eurecom.hikingit", "Filter Create");
		View rootView = inflater.inflate(R.layout.displayfilter_fragment,
				container, false);
		
		spinnerCat = (Spinner) rootView.findViewById(R.id.filterCat);
		spinnerCat.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Log.w("fr.eurecom.hikingit", "Cat " + position + " "
						+ spinnerCat.getSelectedItemPosition());
				Log.w("fr.eurecom.hikingit", "Sending info from filter");
				mListener.OnFragmentFilter(0, Integer.toString(spinnerCat.getSelectedItemPosition()));
				Log.w("fr.eurecom.hikingit", "Info sent from filter");
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		spinnerOrder = (Spinner) rootView.findViewById(R.id.filterOrder);
		spinnerOrder.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Log.w("fr.eurecom.hikingit", "Order " + position + " "
						+ spinnerOrder.getSelectedItemId());
				Log.w("fr.eurecom.hikingit", "Sending info from filter");
				mListener.OnFragmentFilter(1, Integer.toString(spinnerOrder.getSelectedItemPosition()));
				Log.w("fr.eurecom.hikingit", "Info sent from filter");
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		seekDistanceTxt = (TextView) rootView
				.findViewById(R.id.seekDistanceTxt);
		seekDistance = (SeekBar) rootView.findViewById(R.id.seekDistance);
		seekDistance.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar arg0, int progression,
					boolean arg2) {
				// TODO Auto-generated method stub
				seekDistanceTxt.setText(String.valueOf(progression)+" km");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				Log.w("fr.eurecom.hikingit", "Sending info from filter");
				mListener.OnFragmentFilter(2, Integer.toString(seekBar.getProgress()));
				Log.w("fr.eurecom.hikingit", "Info sent from filter");
			}
		});

		nearButton = (RadioButton) rootView.findViewById(R.id.nearMe);
		anyButton = (RadioButton) rootView.findViewById(R.id.any);

		radio = (RadioGroup) rootView.findViewById(R.id.groupDisplay);
		radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				Log.w("fr.eurecom.hikingit",
						"Radio changed " + radio.getCheckedRadioButtonId());
				if (nearButton.isChecked()) {
					Log.w("fr.eurecom.hikingit", "Near me checked");
					seekDistance.setEnabled(true);
					Log.w("fr.eurecom.hikingit", "Sending info from filter");
					mListener.OnFragmentFilter(3, "1");
					Log.w("fr.eurecom.hikingit", "Info sent from filter");
				} else {
					Log.w("fr.eurecom.hikingit", "Any checked");
					seekDistance.setEnabled(false);
					Log.w("fr.eurecom.hikingit", "Sending info from filter");
					mListener.OnFragmentFilter(3, "0");
					Log.w("fr.eurecom.hikingit", "Info sent from filter");
				}
			}

		});
		
		traveled = (CheckBox) rootView.findViewById(R.id.traveled);
		traveled.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (traveled.isChecked())
				{
					Log.w("fr.eurecom.hikingit", "Traveled checked");
					mListener.OnFragmentFilter(4,"1");
					Log.w("fr.eurecom.hikingit", "traveled sent");
				}
				else
				{
					Log.w("fr.eurecom.hikingit", "Traveled unchecked");
					mListener.OnFragmentFilter(4,"0");
					Log.w("fr.eurecom.hikingit", "untraveled sent");
				}
			}
		});
		

		geoEnable = (Switch) rootView.findViewById(R.id.GeoEnable);
		geoEnable.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (geoEnable.isChecked()) {
					Log.w("fr.eurecom.hikingit", "Geo " + geoEnable.isChecked());
					nearButton.setClickable(true);
					nearButton.setChecked(true);
					seekDistance.setEnabled(true);
				} else {
					Log.w("fr.eurecom.hikingit",
							"Geo no " + geoEnable.isChecked());
					nearButton.setClickable(false);
					anyButton.setChecked(true);
					seekDistance.setEnabled(false);
				}
			}

		});		

		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);

		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			geoEnable.setChecked(false);
			nearButton.setClickable(false);
			seekDistance.setClickable(false);
		}

		return rootView;
	}

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentFilterListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implements listener");
		}
	}

	public void onResume() {
		super.onResume();
		Log.w("fr.eurecom.hiking", "------------- Filter onResume");
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			anyButton.setChecked(true);
			nearButton.setClickable(false);
		} else {
			nearButton.setClickable(true);
			nearButton.setChecked(true);
		}
	}

	public void onPause() {
		super.onPause();
		Log.w("fr.eurecom.hiking", "------------- Filter onPause");
	}

	public void onStop() {
		super.onStop();
		Log.w("fr.eurecom.hiking", "------------- Filter onStop");
	}
}