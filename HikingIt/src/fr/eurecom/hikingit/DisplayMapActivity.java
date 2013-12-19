package fr.eurecom.hikingit;

import java.lang.reflect.Array;
import java.util.Vector;

import fr.eurecom.hikingit.R;
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

	private double nonRefreshArea[] = {0,0,0,0};
	private double longitude;
	private double latitude;

	private double marginRefresh = 10;
	private double margin = 5;
	
	Vector<Vector<LatLng>> listVect = new Vector<Vector<LatLng>>();
	Vector<LatLng> vectorLoc = new Vector<LatLng>();
	Vector<LatLng> markerTrack = new Vector<LatLng>();
	
	GoogleMap googleMap;
	
	Vector<Vector<Polyline>> polyline = new Vector<Vector<Polyline>>();
	PolylineOptions rectOptions;

	// blue, purple, green, orange, red - first marker - second thread
	int[] colors = { 0xFF0099CC, 0xFF33B5E5, 0xFF9933CC, 0xFFAA66CC,
			0xFF669900, 0xFF99CC00, 0xFFFF8800, 0xFFBB33, 0xFFCC0000,
			0xFFFF4444 };


	TextView tvLocInfo;
	boolean markerClicked = true;
	boolean ButtonEnableMarkClicked = true;

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
				nonRefreshArea[0] = latitude - marginRefresh;
				nonRefreshArea[1] = latitude + marginRefresh;
				nonRefreshArea[2] = longitude - marginRefresh;
				nonRefreshArea[3] = longitude + marginRefresh;

				googleMap.setOnMapClickListener(this);
				googleMap.setOnMapLongClickListener(this);
				googleMap.setOnMarkerClickListener(this);

				drawMarker(location);

				LocationListener locationListener = new LocationListener() {

					public void onLocationChanged(Location location) {
						Log.w("fr.eurecom.hikingit"," onLocationChanged");
						drawMarker(location);
						Log.w("fr.eurecom.hikingit","my position pinned");
						// redraw the marker when get location update.
						if (location.getLatitude() > nonRefreshArea[0]
								|| location.getLatitude() < nonRefreshArea[1]
								|| location.getLongitude() > nonRefreshArea[2]
								|| location.getLongitude() < nonRefreshArea[3]) {
							
							latitude = location.getLatitude();
							longitude = location.getLongitude();
							
							nonRefreshArea[0] = latitude - marginRefresh;
							nonRefreshArea[1] = latitude + marginRefresh;
							nonRefreshArea[2] = longitude - marginRefresh;
							nonRefreshArea[3] = longitude + marginRefresh;
							
							fillData();
						}
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

				locationManager.requestLocationUpdates(provider, 5000, 0,
						locationListener);
			} else {
				Toast.makeText(getApplicationContext(), "No location",
						Toast.LENGTH_LONG).show();
			}
		}

	}

	
	@Override
	public void onLocationChanged(Location location) {
		// redraw the marker when get location update.
		Log.w("fr.eurecom.hikingit"," onLocationChanged override");
		if (location.getLatitude() < nonRefreshArea[0]
				|| location.getLatitude() > nonRefreshArea[1]
				|| location.getLongitude() < nonRefreshArea[2]
				|| location.getLongitude() > nonRefreshArea[3]) {
			
			latitude = location.getLatitude();
			Log.w("fr.eurecom.hikingit", "lat "+ latitude);
			longitude = location.getLongitude();
			Log.w("fr.eurecom.hikingit", "long "+ longitude);
			
			nonRefreshArea[0] = latitude - marginRefresh;
			nonRefreshArea[1] = latitude + marginRefresh;
			nonRefreshArea[2] = longitude - marginRefresh;
			nonRefreshArea[3] = longitude + marginRefresh;
			
			fillData();
		}
		drawMarker(location);
	}
	
	@Override
	public void onMapClick(LatLng point) {

		tvLocInfo.setText(point.toString());
		googleMap.animateCamera(CameraUpdateFactory.newLatLng(point));
		markerClicked = false;
	}/*
	
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

	*/
	public void onMapLongClick(LatLng point) {
		if (!ButtonEnableMarkClicked)
			return;
		vectorLoc.add(point);
		tvLocInfo.setText("New marker added@" + point.toString());
		googleMap.addMarker(new MarkerOptions().position(point).title(
				point.toString()));

		markerClicked = false;
	}
	

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

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/

	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO Auto-generated method stub
		return false;
	}

	private void fillData() {
		
		if (polyline!=null && !polyline.isEmpty())
		{
			polyline.clear();
			Log.w("fr.eurecom.hikingit", "polyline cleared "+ polyline.toString());
		}
		if (markerTrack!= null && !markerTrack.isEmpty())
		{
			markerTrack.clear();
			Log.w("fr.eurecom.hikingit", "markerTrack cleared "+ markerTrack.toString());
		}
		if (listVect!= null && !listVect.isEmpty())
		{
			listVect.clear();
			Log.w("fr.eurecom.hikingit", "listVect cleared "+ listVect.toString());
		}
		
		
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
		Log.w("fr.eurecom.fr", "latitude max : " + lttdMax);

		double limitMinX = latitude - margin;
		String lttdMin = String.valueOf(limitMinX);
		Log.w("fr.eurecom.fr", "latitude min : " + lttdMin);

		double limitMaxY = longitude + margin;
		String lgtdMax = String.valueOf(limitMaxY);
		Log.w("fr.eurecom.fr","longitude max : " + lgtdMax);

		double limitMinY = longitude - margin;
		String lgtdMin = String.valueOf(limitMinY);
		Log.w("fr.eurecom.fr","longitude min : " + lgtdMin);

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
				Log.w("fr.eurecom.fr", "cursor position" + cursor.getPosition() + "sur" + Integer.toString(cursor.getCount()-1));
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
			addTracks();
		} else {
			Log.w("fr.eurecom.fr", "no cursor");
			Toast.makeText(DisplayMapActivity.this, "No cursor",
					Toast.LENGTH_LONG).show();
		}
	}

	// set an idea on the marker
	// onclick marker => intent
	public void addTracks() {
		Log.w("fr.eurecom.fr", "liste de " + listVect.size() + " track");
		for (int m = 0; m < listVect.size(); m++) {
			Log.w("fr.eurecom.fr",
					"track " + m + " sur "
							+ Integer.toString(listVect.size() - 1));
			LatLng point = listVect.get(m).get(0);
			Log.w("fr.eurecom.fr", "marker en " + point.toString());
			googleMap.addMarker(new MarkerOptions().position(point).title(
					point.toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.hiking2)));
			markerTrack.add(listVect.get(m).get(0));
			drawLine(m);
			
		}
		Log.w("fr.eurecom.hikingit", "markerTrack created "+ markerTrack.toString());
	}

	public void drawLine(int a) {
		Log.w("fr.eurecom.fr", "drawline, vecteur de taille  "
				+ listVect.get(a).size());
		if (listVect.get(a).size() < 2)
			return;
		else {
			Vector<Polyline> vect = new Vector<Polyline>();
			polyline.addElement(vect);
			int colorThread = colors[2 * a % colors.length];
			Log.w("fr.eurecom.fr", "drawline, couleur  "
					+ colorThread);

			for (int m = 0; m < listVect.get(a).size() - 1; m++) {
				
				LatLng Location1 = listVect.get(a).get(m);
				LatLng Location2 = listVect.get(a).get(m+1);
				
				Log.w("fr.eurecom.fr",
						"draw line point " + m + Integer.toString(m + 1));
				Log.w("fr.eurecom.fr",
						"draw line between " + Location1.toString() + " et "
								+ Location2.toString());
				PolylineOptions line = new PolylineOptions()
						.add(Location1, Location2).width(3).color(colorThread);
				polyline.lastElement().add(googleMap.addPolyline(line));
			}
			Log.w("fr.eurecom.hikingit", "polyline created "+ polyline.toString());
		}
	}

}
