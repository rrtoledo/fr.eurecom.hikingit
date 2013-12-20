package fr.eurecom.hikingit;

import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
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

public class PickMapFragment extends Fragment implements OnMapClickListener,
		OnMarkerClickListener, OnMapLongClickListener {

	private double marginRefresh = 5;
	private double margin = 10;

	private double nonRefreshArea[] = { 0, 0, 0, 0 };
	
	private Location location;
	private LatLng latLng;
	private double longitude;
	private double latitude;
	private Marker myMarker;
	
	LocationManager locationManager;
	LocationListener locationListener;

	Vector<Vector<LatLng>> listVect = new Vector<Vector<LatLng>>();
	// Vector<LatLng> vectorLoc = new Vector<LatLng>();
	// Vector<LatLng> markerTrack = new Vector<LatLng>();
	// Vector<Integer> idTrack = new Vector<Integer>();

	GoogleMap googleMap;

	Vector<Vector<Polyline>> polyline = new Vector<Vector<Polyline>>();
	PolylineOptions rectOptions;

	// blue, purple, green, orange, red - first marker - second thread
	int[] colors = { 0xFF0099CC, 0xFF33B5E5, 0xFF9933CC, 0xFFAA66CC,
			0xFF669900, 0xFF99CC00, 0xFFFF8800, 0xFFBB33, 0xFFCC0000,
			0xFFFF4444 };

	TextView tvLocInfo;
	boolean markerClicked = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.pickmap_fragment,
				container, false);

		googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.dmf_map)).getMap();
		
		googleMap.setMyLocationEnabled(true);

		locationManager = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);

		// Creating a criteria object to retrieve provider
		Criteria criteria = new Criteria();

		// Getting the name of the best provider
		String provider = locationManager.getBestProvider(criteria, true);

		// Getting Current Location
		location = locationManager.getLastKnownLocation(provider);

		googleMap.setOnMapClickListener(this);
		googleMap.setOnMapLongClickListener(this);
		googleMap.setOnMarkerClickListener(this);

		if (location != null) {
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			latLng = new LatLng(latitude, longitude);
			googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(8));
			
			nonRefreshArea[0] = latitude - marginRefresh;
			nonRefreshArea[1] = latitude + marginRefresh;
			nonRefreshArea[2] = longitude - marginRefresh;
			nonRefreshArea[3] = longitude + marginRefresh;

			googleMap.setOnMapClickListener(this);
			googleMap.setOnMapLongClickListener(this);
			googleMap.setOnMarkerClickListener(this);

			myMarker = googleMap.addMarker(new MarkerOptions()
					.position(new LatLng(latitude, longitude))
					.snippet(
							"Lat:" + location.getLatitude() + "Lng:"
									+ location.getLongitude())
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
					.title("ME"));

			locationListener = new LocationListener() {

				public void onLocationChanged(Location location) {
					Log.w("fr.eurecom.hikingit", " onLocationChanged");

					Log.w("fr.eurecom.hikingit", "my position pinned");
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
						googleMap.clear();
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

			Log.w("fr.eurecom.hikingit", "MapFragment first lookUp 5000");
			locationManager.requestLocationUpdates(provider, 5000, 0,
					locationListener);
		} else {
			Toast.makeText(getActivity().getApplicationContext(), "No location",
					Toast.LENGTH_LONG).show();
		}
		
    	return rootView;
	}

	public void onLocationChanged(Location location) {
		// redraw the marker when get location update.
		Log.w("fr.eurecom.hikingit", " onLocationChanged override");
		if (location.getLatitude() < nonRefreshArea[0]
				|| location.getLatitude() > nonRefreshArea[1]
				|| location.getLongitude() < nonRefreshArea[2]
				|| location.getLongitude() > nonRefreshArea[3]) {

			latitude = location.getLatitude();
			Log.w("fr.eurecom.hikingit", "lat " + latitude);
			longitude = location.getLongitude();
			Log.w("fr.eurecom.hikingit", "long " + longitude);

			nonRefreshArea[0] = latitude - marginRefresh;
			nonRefreshArea[1] = latitude + marginRefresh;
			nonRefreshArea[2] = longitude - marginRefresh;
			nonRefreshArea[3] = longitude + marginRefresh;
			googleMap.clear();
			fillData();
		}
		drawMarker(location);
	}

	@Override
	public void onMapClick(LatLng point) {

		googleMap.animateCamera(CameraUpdateFactory.newLatLng(point));
		markerClicked = false;
	}

	public void myClickHandler(View target) {
	}

	public void onMapLongClick(LatLng point) {
	}

	private void drawMarker(Location loc) {
		Log.w("fr.eurecom.hikingit", "draw marker called");
		Log.w("fr.eurecom.hikingit", "draw marker first loc "
				+ myMarker.getPosition().toString());
		myMarker.remove();
		Log.w("fr.eurecom.hikingit", "myMarker removed");
		LatLng currentPosition = new LatLng(loc.getLatitude(),
				loc.getLongitude());
		myMarker = googleMap.addMarker(new MarkerOptions()
				.position(currentPosition)
				.snippet(
						"Lat:" + loc.getLatitude() + "Lng:"
								+ loc.getLongitude())
				.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				.title("ME"));
		Log.w("fr.eurecom.hikingit", "draw marker second loc "
				+ myMarker.getPosition().toString());
		Log.w("fr.eurecom.hikingit", "myMarker added");
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		// TODO Auto-generated method stub
		Log.w("fr.eurecom.hikingit",
				"appel onMarkerClick marker = " + marker.toString());
		// Log.w("fr.eurecom.hikngit","idTracker :" + idTrack.toString());
		Log.w("fr.eurecom.hikingit", "snipset " + marker.getSnippet());
		String idMark = marker.getSnippet();
		Log.w("fr.eurecom.hikingit",
				"id " + idMark + " " + idMark.subSequence(0, 1));
		int test = idMark.subSequence(0, 1).toString().compareTo("L");
		if (test != 0) {
			Intent i = new Intent(getActivity(), TrackDetailActivity.class);
			Uri trackUri = Uri.parse(TrackContentProvider.CONTENT_URI + "/"
					+ idMark);
			Log.w("fr.eurecom.hikingit", "trackuri : " + trackUri);
			i.putExtra(TrackContentProvider.CONTENT_ITEM_TYPE, trackUri);
			startActivity(i);
			return true;
		} else {
			Toast.makeText(getActivity().getApplicationContext(), "Press a track",
					Toast.LENGTH_LONG).show();
			return false;
		}
	}

	private void fillData() {

		if (polyline != null && !polyline.isEmpty()) {
			polyline.clear();
			Log.w("fr.eurecom.hikingit",
					"polyline cleared " + polyline.toString());
		}
		if (listVect != null && !listVect.isEmpty()) {
			listVect.clear();
			Log.w("fr.eurecom.hikingit",
					"listVect cleared " + listVect.toString());
		}

		String[] projection = { TrackTable.COLUMN_ID, TrackTable.COLUMN_TITLE,
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
		Log.w("fr.eurecom.fr", "longitude max : " + lgtdMax);

		double limitMinY = longitude - margin;
		String lgtdMin = String.valueOf(limitMinY);
		Log.w("fr.eurecom.fr", "longitude min : " + lgtdMin);

		String[] selectionArgs = { "0", lttdMax, lttdMin, lgtdMax, lgtdMin };
		String order = "";
		Cursor cursor = getActivity().getContentResolver().query(
				TrackContentProvider.CONTENT_URI, projection, selection,
				selectionArgs, order);
		if (cursor == null)
			Log.w("fr.eurecom.fr", "No cursor at all");
		if (cursor != null && cursor.moveToFirst()) {
			cursor.moveToFirst();
			Log.w("fr.eurecom.fr", "cursor " + cursor.toString());
			Log.w("fr.eurecom.fr", "cursor nb rows " + cursor.getCount());
			for (int i = cursor.getPosition(); i < cursor.getCount(); i++) {
				Log.w("fr.eurecom.fr", "cursor position" + cursor.getPosition()
						+ "sur" + Integer.toString(cursor.getCount() - 1));
				int index = 0;
				int index2 = 0;

				int id = Integer.valueOf(cursor.getString(cursor
						.getColumnIndexOrThrow(TrackTable.COLUMN_ID)));
				Log.w("fr.eurecom.fr", "track id " + id);
				// idTrack.add(id);

				String titleTrack = cursor.getString(cursor
						.getColumnIndexOrThrow(TrackTable.COLUMN_TITLE));
				Log.w("fr.eurecom.fr", titleTrack);

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

					if (j == 0) {
						Log.w("fr.eurecom.hikingit", j + " marker numero " + i);
						googleMap.addMarker(new MarkerOptions()
								.position(latlong).title(titleTrack)
								.icon(BitmapDescriptorFactory.fromResource(R.drawable.displaymarker))
								.snippet(Integer.toString(id)));
						Log.w("fr.eurecom.hikingit", "marker ajoute");
					}

				}
				Log.w("fr.eurecom.hikingit", " appel drawline pour " + i);
				drawLine(i);
				Log.w("fr.eurecom.fr", "vector : " + listVect.get(i).toString());
				if (i < cursor.getCount() - 1) {
					cursor.moveToNext();
				}
			}
			// always close the cursor
			Log.w("fr.eurecom.fr", "vector of vectors : " + listVect.toString());
			cursor.close();
		} else {
			Log.w("fr.eurecom.fr", "no cursor");
			Toast.makeText(getActivity(), "No cursor",
					Toast.LENGTH_LONG).show();
		}
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
			Log.w("fr.eurecom.fr", "drawline, couleur  " + colorThread);

			for (int m = 0; m < listVect.get(a).size() - 1; m++) {

				LatLng Location1 = listVect.get(a).get(m);
				LatLng Location2 = listVect.get(a).get(m + 1);

				Log.w("fr.eurecom.fr",
						"draw line point " + m + Integer.toString(m + 1));
				Log.w("fr.eurecom.fr",
						"draw line between " + Location1.toString() + " et "
								+ Location2.toString());
				PolylineOptions line = new PolylineOptions()
						.add(Location1, Location2).width(3).color(colorThread);
				polyline.lastElement().add(googleMap.addPolyline(line));
			}
			Log.w("fr.eurecom.hikingit",
					"polyline created " + polyline.toString());
		}
	}
	
	public void onPause(){
		super.onPause();
		Log.w("fr.eurecom.hiking","Map onPause");
	}
	
	public void onStop(){
		super.onStop();
		Log.w("fr.eurecom.hiking","Map onStop");
		locationManager.removeUpdates(locationListener);
	}
	
}