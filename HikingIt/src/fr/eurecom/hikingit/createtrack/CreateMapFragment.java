package fr.eurecom.hikingit.createtrack;

import java.util.ArrayList;
import java.util.Vector;

import fr.eurecom.hikingit.R;
import fr.eurecom.hikingit.R.drawable;
import fr.eurecom.hikingit.R.id;
import fr.eurecom.hikingit.R.layout;
import fr.eurecom.hikingit.R.menu;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class CreateMapFragment extends Fragment implements OnMapClickListener,
		OnMarkerClickListener, OnMapLongClickListener {
	LocationListener locationListener;
	LocationManager locationManager;
	GoogleMap googleMap;

	Polyline polyline;
	PolylineOptions rectOptions;
	ArrayList<Polyline> polylines = new ArrayList<Polyline>();

	boolean markerClicked = false;
	Marker MyMarker;
	boolean ButtonEnableMarkClicked = false;
	public Vector<LatLng> vectorLoc = new Vector<LatLng>();
	ArrayList<Marker> markers = new ArrayList<Marker>();

	int i = 0;

	AlertDialog.Builder alertDialogBuilder;

	OnFragmentClickListener mListener;
	public Location location;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.createmap_fragment, container,
				false);
		setHasOptionsMenu(true);

		googleMap = ((SupportMapFragment) getFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		googleMap.setMyLocationEnabled(true);

		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.w("fr.eurecom.hikingit", "On CreateView, no manager");
			mListener.OnFragmentClick(-1, "No GPS");
		} else
			Log.w("fr.eurecom.hikingit", "On CreateView, manager "
					+ locationManager.toString());

		// Creating a criteria object to retrieve provider
		Criteria criteria = new Criteria();

		// Getting the name of the best provider
		String provider = locationManager.getBestProvider(criteria, true);
		Log.w("fr.eurecom.hikingit", "provider " + provider);

		// Getting Current Location
		location = locationManager.getLastKnownLocation(provider);
		Log.w("fr.eurecom.hikingit", "first location " + location);
		if (location != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.w("fr.eurecom.hikingit", "location found");

			double latitude = location.getLatitude();

			// Getting longitude of the current location
			double longitude = location.getLongitude();

			// Creating a LatLng object for the current location
			LatLng latLng = new LatLng(latitude, longitude);

			// Showing the current location in Google Map
			googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

			// Zoom in the Google Map
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(4));

			googleMap.setOnMapClickListener(this);
			googleMap.setOnMapLongClickListener(this);
			googleMap.setOnMarkerClickListener(this);
			googleMap.setOnMarkerDragListener(new OnMarkerDragListener() {
				int MarkerIndex;

				@Override
				public void onMarkerDragStart(Marker marker) { // TODO

					// Toast.makeText(getApplicationContext(),
					// "I started dragging ;)", Toast.LENGTH_SHORT).show();
					System.out.println("------START----" + MarkerIndex);

					MarkerIndex = markers.indexOf(marker);
					System.out.println("------START@----" + MarkerIndex);

				}

				@Override
				public void onMarkerDragEnd(Marker marker) { // TODO
					System.out.println("------STOP----" + MarkerIndex);
				}

				@Override
				public void onMarkerDrag(Marker marker) {

					System.out.println("------I am HERE----" + MarkerIndex);
					// update list of markers and vector lines,
					// Marker markerTest = m.next();
					// we update markers
					// we update lines if there are more than 2 markers

					markers.get(MarkerIndex).setPosition(marker.getPosition());
					System.out.println("------After change----" + MarkerIndex);
					vectorLoc.set(MarkerIndex, marker.getPosition());

					if (markers.size() != 1) {
						if (MarkerIndex == 0) {
							polylines.get(0).remove();
							polylines.remove(0);
							Polyline linetemp = googleMap
									.addPolyline(new PolylineOptions()
											.add(vectorLoc.get(0),
													vectorLoc.get(1)).width(3)
											.color(Color.RED));
							polylines.add(0, linetemp);
						} else {
							if (MarkerIndex == markers.size() - 1) {
								polylines.get(MarkerIndex - 1).remove();
								polylines.remove(MarkerIndex - 1);
								Polyline linetemp = googleMap.addPolyline(new PolylineOptions()
										.add(vectorLoc.get(MarkerIndex - 1),
												vectorLoc.get(MarkerIndex))
										.width(3).color(Color.RED));
								polylines.add(MarkerIndex - 1, linetemp);
							} else {
								System.out
										.println("size of polylineeeeeeeeeeee"
												+ polylines.size());
								polylines.get(MarkerIndex).remove();
								polylines.remove(MarkerIndex);
								System.out.println("valueeeeeeeeeeee"
										+ polylines.get(0));
								polylines.get(MarkerIndex - 1).remove();
								polylines.remove(MarkerIndex - 1);
								System.out
										.println("size of polylineeeeeeeeeeee"
												+ polylines.size());

								Polyline linetemp1 = googleMap.addPolyline(new PolylineOptions()
										.add(vectorLoc.get(MarkerIndex - 1),
												vectorLoc.get(MarkerIndex))
										.width(3).color(Color.RED));
								polylines.add(MarkerIndex - 1, linetemp1);

								Polyline linetemp2 = googleMap.addPolyline(new PolylineOptions()
										.add(vectorLoc.get(MarkerIndex),
												vectorLoc.get(MarkerIndex + 1))
										.width(3).color(Color.RED));
								polylines.add(MarkerIndex, linetemp2);

							}
						}
					}
				}
			});

			drawMarker(location);
		}
		return rootView;
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

	private void drawMarker(Location location) {

		LatLng currentPosition = new LatLng(location.getLatitude(),
				location.getLongitude());
		MyMarker = googleMap.addMarker(new MarkerOptions()
				.position(currentPosition)
				.snippet(
						"Lat:" + location.getLatitude() + "Lng:"
								+ location.getLongitude())
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				.title("ME"));
	}

	@Override
	public void onMapClick(LatLng point) {
		googleMap.animateCamera(CameraUpdateFactory.newLatLng(point));
		markerClicked = false;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main, menu);
		super.onCreateOptionsMenu(menu, inflater);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.item1:

			if (ButtonEnableMarkClicked == false) {
				ButtonEnableMarkClicked = true;
				Toast.makeText(getActivity().getApplicationContext(),
						"Edit Map", Toast.LENGTH_SHORT).show();
				item.setIcon(R.drawable.tick);
				if (markers.size() != 0) {
					for (i = 0; i <= markers.size() - 1; i++) {
						markers.get(i).setVisible(true);
						markers.get(i)
								.setIcon(
										BitmapDescriptorFactory
												.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
						markers.get(i).setDraggable(true);
					}
				}
			} else {
				item.setIcon(R.drawable.track);
				Toast.makeText(getActivity().getApplicationContext(), "Done",
						Toast.LENGTH_SHORT).show();
				if (markers.size() > 0) {

					markers.get(0).setIcon(
							BitmapDescriptorFactory
									.fromResource(R.drawable.startline));
					markers.get(0).setTitle("START");

					if (markers.size() > 1) {
						markers.get(markers.size() - 1).setIcon(
								BitmapDescriptorFactory
										.fromResource(R.drawable.finish_flag));
						markers.get(markers.size() - 1).setTitle("END");

						for (i = 1; i < markers.size() - 1; i++) {
							markers.get(i).setVisible(false);
						}
						for (i = 0; i <= markers.size() - 1; i++) {
							markers.get(i).setDraggable(false);
						}
					}

				}

				ButtonEnableMarkClicked = false;
			}
			return true;

		case R.id.item2:
			// Toast.makeText(getActivity().getApplicationContext(),
			// "Clear map",Toast.LENGTH_SHORT).show();

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getActivity());
			alertDialogBuilder.setTitle("Really?");
			alertDialogBuilder.setMessage("Are you sure?");
			// null should be your on click listener
			alertDialogBuilder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(
									getActivity().getApplicationContext(),
									"Reset map", Toast.LENGTH_LONG).show();
							googleMap.clear();
							drawMarker(location);
							markers.clear();
							vectorLoc.clear();
							dialog.dismiss();
						}
					});

			alertDialogBuilder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

			alertDialogBuilder.create();
			alertDialogBuilder.show();
			return true;

		case R.id.item3:
			if (ButtonEnableMarkClicked == false) {
				Log.w("fr.eurecom.hikingit", "vectorLoc = " + vectorLoc);
				if (vectorLoc.isEmpty()) {
					Log.w("fr.eurecom.hikingit",
							"Sending error from CreateMapFragment");
					mListener.OnFragmentClick(0, "Markers");
					Log.w("fr.eurecom.hikingit",
							"Error sent from CreateMapFragment");
				} else {
					String position = "";
					int NbCoords = vectorLoc.size();

					for (int j = 0; j < NbCoords; j++) {
						position += "(";
						position += Double.toString(vectorLoc.get(j).latitude);

						position += ";";
						position += Double.toString(vectorLoc.get(j).longitude);
						position += ")";
					}
					Log.w("fr.eurecom.hikingit", "Data parsed, position : "
							+ position);
					String startX = position
							.substring(1, position.indexOf(";"));
					;
					String startY = position.substring(
							position.indexOf(";") + 1, position.indexOf(")"));

					String data = "[" + Integer.toString(NbCoords) + "]" + "["
							+ position + "]" + "[" + startX + "]" + "["
							+ startY + "]";

					Log.w("fr.eurecom.hikingit",
							"Sending data from CreateMapFragment "
									+ data.toString());
					mListener.OnFragmentClick(2, data);
					Log.w("fr.eurecom.hikingit",
							"Coords sent from CreateMapFragment");
				}
			} else
				Toast.makeText(getActivity(), "Finish editing marker",
						Toast.LENGTH_LONG).show();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onMapLongClick(LatLng point) {
		if (!ButtonEnableMarkClicked)
			return;

		vectorLoc.add(point);
		int a = vectorLoc.size() - 1;
		String s = "num:" + a;
		// tvLocInfo.setText("New marker added@" + point.toString());
		Marker marker = googleMap.addMarker(new MarkerOptions()
				.position(point)
				.draggable(true)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
				.title(s));
		markers.add(marker);

		if (vectorLoc.size() != 0) {

			/*
			 * googleMap.addMarker(new MarkerOptions()
			 * .position(vectorLoc.get(i)) .icon(BitmapDescriptorFactory
			 * .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)) .title(s));
			 */
			if (vectorLoc.size() > 1) {
				Polyline line2 = googleMap.addPolyline(new PolylineOptions()
						.add(vectorLoc.get(vectorLoc.size() - 2),
								vectorLoc.get(vectorLoc.size() - 1)).width(3)
						.color(Color.RED));
				polylines.add(line2);

			}
		}
		markerClicked = false;
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if (!ButtonEnableMarkClicked) {
			marker.getTitle();
			return false;
		}
		marker.remove();
		Toast.makeText(getActivity().getApplicationContext(),
				"Marker removed from the list", Toast.LENGTH_SHORT).show();

		for (int m = 0; m < markers.size(); m++) {
			// Marker markerTest = m.next();
			System.out.println("m = " + m + "  value= " + markers.get(m)
					+ "    marker = " + marker);

			if (markers.get(m).getPosition().equals(marker.getPosition())) {
				Toast.makeText(getActivity().getApplicationContext(),
						"It is removed, m=" + m, Toast.LENGTH_SHORT).show();
				int index = m;// markers.indexOf(m);
				System.out.println("indexxxxxxx" + index);
				if (markers.size() != 1) {
					if (index == 0) {
						polylines.get(0).remove();
						polylines.remove(0);
					} else {
						if (index == markers.size() - 1) {
							polylines.get(index - 1).remove();
							polylines.remove(index - 1);
						} else {
							System.out.println("size of polylineeeeeeeeeeee"
									+ polylines.size());
							polylines.get(index).remove();
							polylines.remove(index);
							System.out.println("valueeeeeeeeeeee"
									+ polylines.get(0));

							polylines.get(index - 1).remove();
							polylines.remove(index - 1);
							System.out.println("size of polylineeeeeeeeeeee"
									+ polylines.size());
							Polyline linetemp = googleMap
									.addPolyline(new PolylineOptions()
											.add(vectorLoc.get(index - 1),
													vectorLoc.get(index + 1))
											.width(3).color(Color.RED));
							polylines.add(index - 1, linetemp);

						}
					}
				} else {
					vectorLoc.remove(0);
					markers.remove(0);
					break;
				}
				vectorLoc.remove(vectorLoc.get(m));
				markers.remove(markers.get(m));
			}
		}
		markerClicked = true;
		return true;
	}

}