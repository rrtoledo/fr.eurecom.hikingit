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
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

	private double marginRefresh;
	private double margin;

	// blue, purple, green, orange, red - first marker - second thread
	int[] colors = { 0xFF0099CC, 0xFF33B5E5, 0xFF9933CC, 0xFFAA66CC,
			0xFF669900, 0xFF99CC00, 0xFFFF8800, 0xFFBB33,
			0xFFCC0000,	0xFFFF4444 };

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
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			fillData();

			addMarkers();

			googleMap.setOnMapClickListener(this);
			googleMap.setOnMapLongClickListener(this);
			googleMap.setOnMarkerClickListener(this);

			LocationListener locationListener = new LocationListener() {

				public void onLocationChanged(Location location) {
					// redraw the marker when get location update.
					if (location.getLatitude() > (latitude + marginRefresh)
							|| location.getLongitude() > (longitude + marginRefresh)) {
						latitude = location.getLatitude();
						longitude = location.getLongitude();
						fillData();
					}
					drawMarker(location);
					drawOtherMarkers();
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
		String[] projection = { TrackTable.COLUMN_DIFFICULTY,
				TrackTable.COLUMN_TITLE, TrackTable.COLUMN_SUMMARY,
				TrackTable.COLUMN_NBCOORDS, TrackTable.COLUMN_DURATION,
				TrackTable.COLUMN_STARTX, TrackTable.COLUMN_STARTY,
				TrackTable.COLUMN_COORDS, TrackTable.COLUMN_FLAGS,
				TrackTable.COLUMN_SCORE, TrackTable.COLUMN_PIC };

		String selection = "flags=? AND startX<? AND startY<?";

		double limitX = latitude + margin;
		String lgtd = String.valueOf(limitX);

		double limitY = longitude + margin;
		String lttd = String.valueOf(limitY);

		String[] selectionArgs = { "1", lttd, lgtd };
		String order = "";
		Cursor cursor = getContentResolver().query(
				TrackContentProvider.CONTENT_URI, projection, selection,
				selectionArgs, order);
		if (cursor != null) {
			cursor.moveToFirst();
			int index = 0;
			int index2 = 0;
			for (int i = cursor.getPosition(); i <= cursor.getCount(); i++) {
				int NbCoords = Integer.valueOf(cursor.getString(cursor
						.getColumnIndexOrThrow(TrackTable.COLUMN_NBCOORDS)));

				String Coords = cursor.getString(cursor
						.getColumnIndexOrThrow(TrackTable.COLUMN_COORDS));

				Vector<LatLng> vect = new Vector<LatLng>();
				listVect.add(vect);

				for (int j = 0; j <= NbCoords; j++) {
					index2 = Coords.indexOf(";", index);
					double lat = Double.valueOf(Coords.substring(index + 1,
							index2 - 1));
					index = Coords.indexOf("(", index2);
					double lgt = Double.valueOf(Coords.substring(index2 + 1,
							index - 2));
					LatLng latlong = new LatLng(lat, lgt);
					listVect.get(i).add(latlong);
				}
				cursor.moveToNext();
			}

			// always close the cursor
			cursor.close();
		}
	}

	//set an idea on the marker
	//onclick marker => intent
	public void addMarkers() {
		for (int m = 0; m <= listVect.size(); m++) {
			LatLng point = listVect.get(m).get(1);
			googleMap.addMarker(new MarkerOptions().position(point).title(
					point.toString()));
			drawLine(m);
		}
	}

	public void drawLine(int a) {

		if (listVect.get(a).size() < 2)
			return;
		else {
			LatLng Location1 = listVect.get(a).get(0);
			LatLng Location2 = listVect.get(a).get(1);
			
			int colorThread = colors[2*a % colors.length];

			for (int m = 0; m < listVect.get(a).size()-1; m++) {
				PolylineOptions line = new PolylineOptions()
						.add(Location1, Location2).width(2).color(colorThread);

				googleMap.addPolyline(line);
				
				Location1 = listVect.get(a).get(m);
				Location2 = listVect.get(a).get(m+1);
			}
		}
	}
	
}
