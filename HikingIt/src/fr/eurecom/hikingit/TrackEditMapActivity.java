package fr.eurecom.hikingit;

import java.util.Vector;

import android.app.Dialog;
import android.content.ContentValues;
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

import fr.eurecom.hikingit.contentprovider.TrackContentProvider;
import fr.eurecom.hikingit.database.TrackTable;
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

public class TrackEditMapActivity extends FragmentActivity implements
		LocationListener, OnMapClickListener, OnMapLongClickListener,
		OnMarkerClickListener {

	static final LatLng Location1 = new LatLng(43.60191559, 7.100901604);
	static final LatLng Location2 = new LatLng(43.60331405, 7.101652622);

	GoogleMap googleMap;
	TextView tvLocInfo;
	boolean markerClicked = false;
	Polyline polyline;
	PolylineOptions rectOptions;
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

			googleMap.setOnMapClickListener(this);
			googleMap.setOnMapLongClickListener(this);
			googleMap.setOnMarkerClickListener(this);

			LocationListener locationListener = new LocationListener() {

				public void onLocationChanged(Location location) {
					// redraw the marker when get location update.
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
				drawOtherMarkers();
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
	
	public void myFinishedTrack(View v){
		
		if (!vectorLoc.isEmpty())
		{
			String position = "";
			int NbCoords = vectorLoc.size();

			for (int j = 0; j < NbCoords; j++) {
				position += "(";
				position += Double.toString(vectorLoc.get(j).latitude);

				position += ";";
				position += Double.toString(vectorLoc.get(j).longitude);
				position += ")";
			}

			String startX = position.substring(1, position.indexOf(";"));;
			String startY = position.substring(position.indexOf(";")+1, position.indexOf(")"));
			String difficulty = "1";
			String coords = position;
			int nbcoords = NbCoords;
			String title = "new Track";
			String summary = "summary";
			String duration = "60";
			String visibility = "1";
			String reputation = "0;0";
			String pictures = "picture_path";
			String score = "1";
			
			ContentValues values = new ContentValues();
			values.put(TrackTable.COLUMN_TITLE, title);
			values.put(TrackTable.COLUMN_SUMMARY, summary);
			values.put(TrackTable.COLUMN_DURATION, duration);
			values.put(TrackTable.COLUMN_DIFFICULTY, difficulty);
			values.put(TrackTable.COLUMN_NBCOORDS, nbcoords);
			values.put(TrackTable.COLUMN_COORDS, position);
			values.put(TrackTable.COLUMN_STARTX, startX);
			values.put(TrackTable.COLUMN_STARTY, startY);
			values.put(TrackTable.COLUMN_FLAGS, visibility);
			values.put(TrackTable.COLUMN_SCORE, score);
			values.put(TrackTable.COLUMN_REP, reputation);
			values.put(TrackTable.COLUMN_PIC, pictures);
			
			Uri trackUri = getContentResolver().insert(
					TrackContentProvider.CONTENT_URI, values);
			
		}
		else
		{
			Toast.makeText(TrackEditMapActivity.this,"No location set" , Toast.LENGTH_LONG).show();			
		}
	}

	public void onMapLongClick(LatLng point) {
		if (!ButtonEnableMarkClicked)
			return;
		vectorLoc.add(point);
		tvLocInfo.setText("New marker added@" + point.toString());
		googleMap.addMarker(new MarkerOptions().position(point).title(
				point.toString()));
		Log.w("fr.eurecom.hikingit", point.toString());
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
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				.title("ME"));
	}

	private void drawOtherMarkers() {

		googleMap.addMarker(new MarkerOptions()
				.position(Location1)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				.title("1"));
		googleMap.addMarker(new MarkerOptions()
				.position(Location2)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				.title("2"));

		PolylineOptions line = new PolylineOptions().add(Location1, Location2)
				.width(2).color(Color.RED);

		googleMap.addPolyline(line);

		if (vectorLoc.size() != 0) {
			for (int i = 0; i < vectorLoc.size(); i++) {
				String s = "num:" + i;
				googleMap
						.addMarker(new MarkerOptions()
								.position(vectorLoc.get(i))
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
								.title(s));
				if (i != vectorLoc.size() - 1) {
					PolylineOptions line2 = new PolylineOptions()
							.add(vectorLoc.get(i), vectorLoc.get(i + 1))
							.width(3).color(Color.YELLOW);
					googleMap.addPolyline(line2);
				}
			}

		}
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

}
