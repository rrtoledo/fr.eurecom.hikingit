package fr.eurecom.hikingit;

import java.lang.reflect.Array;
import java.util.Vector;

import android.app.Dialog;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import fr.eurecom.hikingit.contentprovider.TrackContentProvider;
import fr.eurecom.hikingit.database.TrackTable;

public class DisplayMapActivity extends FragmentActivity implements
		LocationListener, OnMapClickListener, OnMapLongClickListener,
		OnMarkerClickListener {

	private double longitude;
	private double latitude;

	private double marginRefresh = 10;
	private double margin = 5;

	// blue, purple, green, orange, red - first marker - second thread
	int[] colors = { 0xFF0099CC, 0xFF33B5E5, 0xFF9933CC, 0xFFAA66CC,
			0xFF669900, 0xFF99CC00, 0xFFFF8800, 0xFFBB33, 0xFFCC0000,
			0xFFFF4444 };

	GoogleMap googleMap;
	TextView tvLocInfo;
	boolean markerClicked = false;
	Polyline polyline;
	PolylineOptions rectOptions;

	Vector<Vector<LatLng>> listVect = new Vector<Vector<LatLng>>();
	Vector<LatLng> vectorLoc = new Vector<LatLng>();

	boolean ButtonEnableMarkClicked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tvLocInfo = (TextView) findViewById(R.id.tv_location);

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
			SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);

			// Getting GoogleMap object from the fragment
			googleMap = fm.getMap();

			// Enabling MyLocation Layer of Google Map
			googleMap.setMyLocationEnabled(true);

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
				fillData();

				googleMap.setOnMapClickListener(this);
				googleMap.setOnMapLongClickListener(this);
				googleMap.setOnMarkerClickListener(this);

				drawMarker(location);

				LocationListener locationListener = new LocationListener() {

					public void onLocationChanged(Location location) {
						// redraw the marker when get location update.
						if (location.getLatitude() > (latitude + marginRefresh)
								|| location.getLatitude() < (latitude - marginRefresh)
								|| location.getLongitude() > (longitude + marginRefresh)
								|| location.getLongitude() < (longitude - marginRefresh)) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
							fillData();
						}
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

				if (location != null) {
					// PLACE THE INITIAL MARKER
					drawMarker(location);
				}

				locationManager.requestLocationUpdates(provider, 3000, 0,
						locationListener);
			} else {
				Toast.makeText(getApplicationContext(), "No location",
						Toast.LENGTH_LONG).show();
			}
		}

	}

	@Override
	public void onMapClick(LatLng point) {

		tvLocInfo.setText(point.toString());
		googleMap.animateCamera(CameraUpdateFactory.newLatLng(point));
		markerClicked = false;
	}

	public void myClickHandler(View target) {
		Button buttonAddTrack = (Button) findViewById(R.id.b_addmark);

		if (target.getId() == R.id.b_addmark) {
			if (ButtonEnableMarkClicked == false) {
				ButtonEnableMarkClicked = true;
				buttonAddTrack.setText("Stop Track");
			} else {
				ButtonEnableMarkClicked = false;
				buttonAddTrack.setText("Add Mark");
			}
		}
	}

	public void onMapLongClick(LatLng point) {
		if (!ButtonEnableMarkClicked)
			return;
		vectorLoc.add(point);
		tvLocInfo.setText("New marker added@" + point.toString());
		googleMap.addMarker(new MarkerOptions().position(point).title(
				point.toString()));

		markerClicked = false;
	}

	/*
	 * public boolean onMarkerClick(Marker marker) {
	 * 
	 * if(markerClicked){
	 * 
	 * if(polyline != null){ polyline.remove(); polyline = null; }
	 * 
	 * rectOptions.add(marker.getPosition()); rectOptions.color(Color.RED);
	 * polyline =googleMap.addPolyline(rectOptions); }else{ if(polyline !=
	 * null){ polyline.remove(); polyline = null; }
	 * 
	 * rectOptions = new PolylineOptions().add(marker.getPosition());
	 * markerClicked = true; }
	 * 
	 * return true; }
	 */

	private void drawMarker(Location location) {
		googleMap.clear();
		LatLng currentPosition = new LatLng(location.getLatitude(),
				location.getLongitude());
		googleMap.addMarker(new MarkerOptions()
				.position(currentPosition)
				.snippet(
						"Lat:" + location.getLatitude() + "Lng:"
								+ location.getLongitude())
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
				.title("ME"));
	}

	private void drawOtherMarkers() {
	}

	@Override
	public void onLocationChanged(Location location) {

		TextView tvLocation = (TextView) findViewById(R.id.tv_location);

		// Getting latitude of the current location
		double latitude = location.getLatitude();

		// Getting longitude of the current location
		double longitude = location.getLongitude();

		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);

		// Showing the current location in Google Map
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

		// Zoom in the Google Map
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

		// Setting latitude and longitude in the TextView tv_location
		tvLocation.setText("Latitude:" + latitude + ", Longitude:" + longitude);

		if (location.getLatitude() > (latitude + marginRefresh)
				|| location.getLatitude() < (latitude - marginRefresh)
				|| location.getLongitude() > (longitude + marginRefresh)
				|| location.getLongitude() < (longitude - marginRefresh)) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			fillData();
		}
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
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO Auto-generated method stub
		return false;
	}

	private void fillData() {
		String[] projection = { TrackTable.COLUMN_TITLE,
				TrackTable.COLUMN_SUMMARY, TrackTable.COLUMN_DURATION,
				TrackTable.COLUMN_DIFFICULTY, TrackTable.COLUMN_NBCOORDS,
				TrackTable.COLUMN_COORDS, TrackTable.COLUMN_STARTX,
				TrackTable.COLUMN_STARTY, TrackTable.COLUMN_FLAGS,
				TrackTable.COLUMN_SCORE, TrackTable.COLUMN_REP,
				TrackTable.COLUMN_PIC };

		String selection = "flags = ? AND startX < ? AND startX > ? AND startY < ? AND startY > ? ";

		double limitMaxX = latitude + margin;
		String lttdMax = String.valueOf(limitMaxX);
		Toast.makeText(DisplayMapActivity.this, "latitude max : " + lttdMax,
				Toast.LENGTH_LONG).show();

		double limitMinX = latitude - margin;
		String lttdMin = String.valueOf(limitMinX);
		Toast.makeText(DisplayMapActivity.this, "latitude min : " + lttdMin,
				Toast.LENGTH_LONG).show();

		double limitMaxY = longitude + margin;
		String lgtdMax = String.valueOf(limitMaxY);
		Toast.makeText(DisplayMapActivity.this, "longitude max : " + lgtdMax,
				Toast.LENGTH_LONG).show();

		double limitMinY = longitude - margin;
		String lgtdMin = String.valueOf(limitMinY);
		Toast.makeText(DisplayMapActivity.this, "longitude min : " + lgtdMin,
				Toast.LENGTH_LONG).show();

		String[] selectionArgs = { "1", lttdMax, lttdMin, lgtdMax, lgtdMin };
		String order = "";
		Cursor cursor = getContentResolver().query(
				TrackContentProvider.CONTENT_URI, projection, selection,
				selectionArgs, order);
		if (cursor != null && cursor.moveToFirst()) {
			cursor.moveToFirst();
			Log.w("fr.eurecom.fr", "cursor " + cursor.toString());
			Log.w("fr.eurecom.fr", "cursor nb rows " + cursor.getCount());
			for (int i = cursor.getPosition(); i < cursor.getCount(); i++) {
				int index = 0;
				int index2 = 0;

				int NbCoords = Integer.valueOf(cursor.getString(cursor
						.getColumnIndexOrThrow(TrackTable.COLUMN_NBCOORDS)));
				Log.w("fr.eurecom.fr", "cursor position " + Integer.toString(i)
						+ " sur " + Integer.toString(cursor.getCount() - 1));

				String Coords = cursor.getString(cursor
						.getColumnIndexOrThrow(TrackTable.COLUMN_COORDS));
				Log.w("fr.eurecom.fr", Coords);

				Vector<LatLng> vect = new Vector<LatLng>();
				listVect.add(vect);

				for (int j = 0; j < NbCoords; j++) {
					Log.w("fr.eurecom.fr", "position latlong " + j + " sur "
							+ Integer.toString(NbCoords - 1));
					index2 = Coords.indexOf(";", index);

					Log.w("fr.eurecom.fr",
							"index " + index + " sur "
									+ Integer.toString(Coords.length() - 1));
					Log.w("fr.eurecom.fr", "index2 " + index2 + " sur "
							+ Integer.toString(Coords.length() - 1));
					Log.w("fr.eurecom.fr",
							"lat : " + Coords.substring(index + 1, index2));

					double lat = Double.valueOf(Coords.substring(index + 1,
							index2));
					index = Coords.indexOf("(", index2);
					if (index == -1)
						index = Coords.length();
					Log.w("fr.eurecom.fr", "new index : " + index + " sur "
							+ Integer.toString(Coords.length() - 1));
					Log.w("fr.eurecom.fr",
							"long : " + Coords.substring(index2 + 1, index - 1));

					double lgt = Double.valueOf(Coords.substring(index2 + 1,
							index - 1));
					LatLng latlong = new LatLng(lat, lgt);
					Log.w("fr.eurecom.fr", "latlong : " + latlong.toString());
					listVect.get(i).add(latlong);

				}
				Log.w("fr.eurecom.fr", "vector : " + listVect.get(i).toString());
				if (i < cursor.getCount() - 1) {
					cursor.moveToNext();
				}
			}

			// always close the cursor
			Log.w("fr.eurecom.fr", "vector of vectors : " + listVect.toString());
			cursor.close();
			addMarkers();
		} else {
			Toast.makeText(DisplayMapActivity.this, "No cursor",
					Toast.LENGTH_LONG).show();
		}
	}

	// set an idea on the marker
	// onclick marker => intent
	public void addMarkers() {
		Log.w("fr.eurecom.fr", "liste de " + listVect.size() + " track");
		for (int m = 0; m < listVect.size(); m++) {
			Log.w("fr.eurecom.fr",
					"track " + m + " sur "
							+ Integer.toString(listVect.size() - 1));
			LatLng point = listVect.get(m).get(0);
			Log.w("fr.eurecom.fr", "marker en " + point.toString());
			googleMap.addMarker(new MarkerOptions().position(point).title(
					point.toString()));
			drawLine(m);
		}
	}

	public void drawLine(int a) {
		Log.w("fr.eurecom.fr", "drawline, vecteur de taille  "
				+ listVect.get(a).size());
		if (listVect.get(a).size() < 2)
			return;
		else {
			LatLng Location1 = listVect.get(a).get(0);
			LatLng Location2 = listVect.get(a).get(1);

			int colorThread = colors[2 * a % colors.length];

			for (int m = 0; m < listVect.get(a).size() - 1; m++) {
				Log.w("fr.eurecom.fr",
						"draw line point " + m + Integer.toString(m + 1));
				Log.w("fr.eurecom.fr",
						"draw line between " + Location1.toString() + " et "
								+ Location2.toString());
				PolylineOptions line = new PolylineOptions()
						.add(Location1, Location2).width(3).color(colorThread);

				googleMap.addPolyline(line);

				Location1 = listVect.get(a).get(m + 1);
				if (m + 2 < listVect.get(a).size()) {
					Location2 = listVect.get(a).get(m + 2);
				}
			}
		}
	}

}
