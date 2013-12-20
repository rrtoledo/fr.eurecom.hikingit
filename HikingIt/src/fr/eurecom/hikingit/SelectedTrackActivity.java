package fr.eurecom.hikingit;

import java.util.Vector;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import fr.eurecom.hikingit.contentprovider.TrackContentProvider;
import fr.eurecom.hikingit.database.TrackTable;

/*
 * TrackDetailActivity allows to enter a new track item 
 * or to change an existing
 */
public class SelectedTrackActivity extends Activity implements LocationListener,
		OnMapClickListener, OnMapLongClickListener, OnMarkerClickListener {

	private TextView mDifficulty;
	private TextView mNbCoords;
	private TextView mTitleText;
	private TextView mSummaryText;
	private TextView mDuration;
	private TextView mPos;
	private Button mButton;

	private Uri trackUri;
	private Integer caller;
	Integer id;

	private LocationListener locationListener;
	private GoogleMap googleMap;
	public Vector<LatLng> vectorLoc = new Vector<LatLng>();
	private LatLng latLng;
	private double latitude;
	private double longitude;
	Marker myMarker;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.selected_track);

		mDifficulty = (TextView) findViewById(R.id.tdDifficulty);
		mTitleText = (TextView) findViewById(R.id.track_edit_title);
		mSummaryText = (TextView) findViewById(R.id.tdSummary);
		mDuration = (TextView) findViewById(R.id.tdDuration);
		mButton = (Button) findViewById(R.id.selectedButton);

		// Getting Google Play availability status
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());

		// Showing status
		if (status != ConnectionResult.SUCCESS) { // Google Play Services are
													// not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();

		} else { // Google Play Services are available

			// Getting reference to the SupportMapFragment of activity_main.xml
			MapFragment fm = (MapFragment) getFragmentManager()
					.findFragmentById(R.id.map);

			// Getting GoogleMap object from the fragment
			googleMap = fm.getMap();

			// Enabling MyLocation Layer of Google Map
			googleMap.setMyLocationEnabled(true);

			locationListener = new LocationListener() {

				public void onLocationChanged(Location location) {
					drawMarker(location);
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
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
					// TODO Auto-generated method stub

				}
			};

			// Getting LocationManager object from System Service
			// LOCATION_SERVICE
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			// Creating a criteria object to retrieve provider
			Criteria criteria = new Criteria();

			// Getting the name of the best provider
			String provider = locationManager.getBestProvider(criteria, true);

			// Getting Current Location
			Location location = locationManager.getLastKnownLocation(provider);

			if (location != null) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();

				googleMap.setOnMapClickListener(this);
				googleMap.setOnMapLongClickListener(this);
				googleMap.setOnMarkerClickListener(this);

				drawMarker(location);

			}

			Bundle extras = getIntent().getExtras();
			Log.w("fr.eurecom.hikingit", "getting extra : " + extras.toString());

			// check from the saved Instance
			trackUri = (bundle == null) ? null : (Uri) bundle
					.getParcelable(TrackContentProvider.CONTENT_ITEM_TYPE);

			caller = extras.getInt("Caller");
			if ( caller != null && caller == 0)
				mButton.setText("Track It!");
			if ( caller != null && caller == 1)
				mButton.setText("Save track");
			
			// Or passed from the other activity
			if (extras != null) {
				Log.w("fr.eurecom.hikingit", "there is extra");
				trackUri = extras
						.getParcelable(TrackContentProvider.CONTENT_ITEM_TYPE);

				Toast.makeText(SelectedTrackActivity.this, "extras : " + extras,
						Toast.LENGTH_LONG).show();
				fillData(trackUri);
			}

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

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onMapLongClick(LatLng arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

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

	private void drawMarker(Location location) {
		if (myMarker == null) {
			LatLng currentPosition = new LatLng(location.getLatitude(),
					location.getLongitude());
			myMarker = googleMap.addMarker(new MarkerOptions()
					.position(currentPosition)
					.snippet(
							"Lat:" + location.getLatitude() + "Lng:"
									+ location.getLongitude())
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
					.title("ME"));
		} else {
			myMarker.remove();
			LatLng currentPosition = new LatLng(location.getLatitude(),
					location.getLongitude());
			myMarker = googleMap.addMarker(new MarkerOptions()
					.position(currentPosition)
					.snippet(
							"Lat:" + location.getLatitude() + "Lng:"
									+ location.getLongitude())
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
					.title("ME"));
		}
	}

	private void fillData(Uri uri) {
		String[] projection = { TrackTable.COLUMN_ID, TrackTable.COLUMN_TITLE,
				TrackTable.COLUMN_SUMMARY, TrackTable.COLUMN_DURATION,
				TrackTable.COLUMN_DIFFICULTY, TrackTable.COLUMN_NBCOORDS,
				TrackTable.COLUMN_COORDS, TrackTable.COLUMN_STARTX,
				TrackTable.COLUMN_STARTY, TrackTable.COLUMN_FLAGS,
				TrackTable.COLUMN_SCORE, TrackTable.COLUMN_REP,
				TrackTable.COLUMN_PIC };

		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null && cursor.moveToFirst()) {
			cursor.moveToFirst();

			id = Integer.valueOf(cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_ID)));
			
			String s = cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_COORDS));

			int nbcoords = Integer.valueOf(cursor.getString(cursor
					.getColumnIndexOrThrow(TrackTable.COLUMN_NBCOORDS)));

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

			int index = 0;
			int index2 = 0;

			for (int j = 0; j < nbcoords; j++) {
				index2 = s.indexOf(";", index + 1);
				double lat = Double.valueOf(s.substring(index + 1, index2));
				index = s.indexOf("(", index2 + 1);
				if (index == -1)
					index = s.length();
				double lgt = Double.valueOf(s.substring(index2 + 1, index - 1));
				LatLng latLng = new LatLng(lat, lgt);
				vectorLoc.add(latLng);

				if (j == 0) {
					// Showing the current location in Google Map
					googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

					// Zoom in the Google Map
					googleMap.animateCamera(CameraUpdateFactory.zoomTo(4));
				}

			}
			drawTrack();
		} else
			Log.w("fr.eurecom.hikingit", "EMF Cursor is null");
	}

	private void drawTrack() {
		Log.w("fr.eurecom.hikingit",
				"DrawTrack, vectorLoc " + vectorLoc.toString());

		for (int i = 0; i < vectorLoc.size(); i++) {
			String s = "num " + i;
			if (i == 0) {
				googleMap.addMarker(new MarkerOptions()
						.position(vectorLoc.get(i))
						.draggable(true)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.startline))
						.title("START"));
			}
			if (i == vectorLoc.size() - 1) {
				googleMap.addMarker(new MarkerOptions()
						.position(vectorLoc.get(i))
						.draggable(true)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.finish_flag))
						.title("END"));
			}

			if (vectorLoc.size() > 1 && i < vectorLoc.size() - 1) {
				googleMap.addPolyline(new PolylineOptions()
						.add(vectorLoc.get(i), vectorLoc.get(i + 1)).width(3)
						.color(Color.RED));
			}
		}
	}
	
	public void selectedAction(View v){
		Log.w("fr.eurecom.hikingit","The caller is "+caller);
		switch (caller){
		case 0:
			//Function to enable energy saving, and other stuff...
			break;
		case 1:
			setSaved();
			break;
		}
	}
	
	private void setSaved(){		
		String where = ""; //"id = ?";
		String[] whereArgs = new String[1] ;//{String.valueOf(id)};
		ContentValues data = new ContentValues();                          
		data.put("flags", "1");
		int i = getContentResolver().update(trackUri, data, where , whereArgs);
		if (i > 0)
			Toast.makeText(this, i+" row(s) updated",
				Toast.LENGTH_LONG).show();
	}
	
}