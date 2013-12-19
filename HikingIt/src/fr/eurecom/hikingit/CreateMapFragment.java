package fr.eurecom.hikingit;

import java.util.ArrayList;
import java.util.Vector;

import fr.eurecom.hikingit.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
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
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;

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

import fr.eurecom.hikingit.contentprovider.TrackContentProvider;
import fr.eurecom.hikingit.database.TrackTable;

public class CreateMapFragment extends Fragment implements OnMapClickListener, OnMarkerClickListener, OnMapLongClickListener {
	
	GoogleMap googleMap;
	boolean markerClicked = false;
	Polyline polyline;
	PolylineOptions rectOptions;
	public Vector<LatLng> vectorLoc = new Vector<LatLng>();
	boolean ButtonEnableMarkClicked = false;
	ArrayList<Marker> markers = new ArrayList<Marker>();
	ArrayList<Polyline> polylines = new ArrayList<Polyline>();
	int i = 0;
	public Location location;
	Marker MyMarker;
	AlertDialog.Builder alertDialogBuilder ;
	
	String coords;
	OnFragmentClickListener mListener;
	
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	
        View rootView = inflater.inflate(R.layout.edit_map_fragment, container, false);
        setHasOptionsMenu(true);
        
        
        googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        
        googleMap.setMyLocationEnabled(true);
        
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

		// Creating a criteria object to retrieve provider
		Criteria criteria = new Criteria();

		// Getting the name of the best provider
		String provider = locationManager.getBestProvider(criteria, true);

		// Getting Current Location
		location = locationManager.getLastKnownLocation(provider);

		googleMap.setOnMapClickListener(this);
		googleMap.setOnMapLongClickListener(this);
		googleMap.setOnMarkerClickListener(this);
		
		
		
        
		LocationListener locationListener = new LocationListener() {
			
			@Override
			public void onLocationChanged(Location loc) {
				//MyMarker.remove();
				System.out.println("Location: " + location + " Loc : " + loc);
				// Getting latitude of the current location
				double latitude = loc.getLatitude();

				// Getting longitude of the current location
				double longitude = loc.getLongitude();

				// Creating a LatLng object for the current location
				LatLng latLng = new LatLng(latitude, longitude);

				// Showing the current location in Google Map
				googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

				// Zoom in the Google Map
				googleMap.animateCamera(CameraUpdateFactory.zoomTo(8));
				
				if(location != loc){
					MyMarker.remove();
					//drawMarker(loc);
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

				int a = 0;

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
										.color(Color.YELLOW));
						polylines.add(0, linetemp);
					} else {
						if (MarkerIndex == markers.size() - 1) {
							polylines.get(MarkerIndex - 1).remove();
							polylines.remove(MarkerIndex - 1);
							Polyline linetemp = googleMap.addPolyline(new PolylineOptions()
									.add(vectorLoc.get(MarkerIndex - 1),
											vectorLoc.get(MarkerIndex))
									.width(3).color(Color.YELLOW));
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
									.width(3).color(Color.YELLOW));
							polylines.add(MarkerIndex - 1, linetemp1);

							Polyline linetemp2 = googleMap.addPolyline(new PolylineOptions()
									.add(vectorLoc.get(MarkerIndex),
											vectorLoc.get(MarkerIndex + 1))
									.width(3).color(Color.YELLOW));
							polylines.add(MarkerIndex, linetemp2);

						}
					}
				}

			}

		});

   
		if (location != null) {
			// PLACE THE INITIAL MARKER
			drawMarker(location);
		}
		
	

    	return rootView;
    
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
		public void  onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	 		inflater.inflate(R.menu.main, menu);
			super.onCreateOptionsMenu(menu, inflater);
			
		}
		

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			coords = vectorLoc.toString();
			// Handle item selection
			switch (item.getItemId()) {
			case R.id.item1:
				
				if (ButtonEnableMarkClicked == false) {
					ButtonEnableMarkClicked = true;
					Toast.makeText(getActivity().getApplicationContext(), "Edit Map",
							Toast.LENGTH_SHORT).show();
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
				}else {
					item.setIcon(R.drawable.track);
					Toast.makeText(getActivity().getApplicationContext(), "Done",
							Toast.LENGTH_SHORT).show();
					if (markers.size() > 0) {

						markers.get(0)
								.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.startline));
						markers.get(0).setTitle("START");

						if (markers.size() > 1) {
							markers.get(markers.size() - 1)
									.setIcon(
											BitmapDescriptorFactory.fromResource(R.drawable.finish_flag));
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
				
				//Toast.makeText(getActivity().getApplicationContext(), "Clear map",Toast.LENGTH_SHORT).show();
			
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		        alertDialogBuilder.setTitle("Really?");
		        alertDialogBuilder.setMessage("Are you sure?");
		        //null should be your on click listener
		        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
		        	@Override
		            public void onClick(DialogInterface dialog, int which) {
		        	Toast.makeText(getActivity().getApplicationContext(),"Reset map",Toast.LENGTH_LONG).show();
						googleMap.clear();
						drawMarker(location);
						markers.clear();
						vectorLoc.clear();
		                dialog.dismiss();
		            }
		        });
		        
		        
		        
		        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		                dialog.dismiss();
		            }
		        });


		        alertDialogBuilder.create();
		        alertDialogBuilder.show();

				return true;
				
			case R.id.item3:
				Log.w("fr.eurecom.hikingit","coords = "+ coords);
				Log.w("fr.eurecom.hikingit","length = "+ coords.length());
				if ( coords.length() < 3)
				{
					Toast.makeText(getActivity(),
							 "Add markers", Toast.LENGTH_LONG).show();
				}
				else 
				{
					
					Log.w("fr.eurecom.hikingit","Sending coords from CreateMapFragment");
					mListener.OnFragmentClick(0, coords);
					Log.w("fr.eurecom.hikingit","Coords sent from CreateMapFragment");
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}

		
		
		/*public void myClickHandler(View target) {
			Button buttonAddTrack = (Button) findViewById(R.id.b_addmark);

			if (target.getId() == R.id.b_addmark) {
				if (ButtonEnableMarkClicked == false) {
					ButtonEnableMarkClicked = true;
					buttonAddTrack.setText("Stop Track");

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
					if (markers.size() > 0) {

						markers.get(0)
								.setIcon(
										BitmapDescriptorFactory
												.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
						markers.get(0).setTitle("START");

						if (markers.size() > 1) {
							markers.get(markers.size() - 1)
									.setIcon(
											BitmapDescriptorFactory
													.defaultMarker(BitmapDescriptorFactory.HUE_RED));
							markers.get(markers.size() - 1).setTitle("END");

							for (i = 1; i < markers.size() - 1; i++) {
								//markers.get(i).setVisible(false);
								markers.get(i)
								.setIcon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_RED));
								
							}
							for (i = 0; i <= markers.size() - 1; i++) {
								markers.get(i).setDraggable(false);
							}
						}
						else markers.get(0).setDraggable(false);

					}

					ButtonEnableMarkClicked = false;
					buttonAddTrack.setText("Add Mark");
				}
			}
		}

		public void myFinishedTrack(View v) {

			if (!vectorLoc.isEmpty()) {
				String position = "";
				int NbCoords = vectorLoc.size();

				for (int j = 0; j < NbCoords; j++) {
					position += "(";
					position += Double.toString(vectorLoc.get(j).latitude);

					position += ";";
					position += Double.toString(vectorLoc.get(j).longitude);
					position += ")";
				}
				Log.w("fr.eurecom.hikingit","position : "+position);
				String startX = position.substring(1, position.indexOf(";"));
				;
				String startY = position.substring(position.indexOf(";") + 1,
						position.indexOf(")"));
				String difficulty = "1";
				Object coords = vectorLoc.size();
				String nbcoords = coords.toString();
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

				Uri trackUri = getActivity().getContentResolver().insert(
						TrackContentProvider.CONTENT_URI, values);

			} else {
				Toast.makeText(getActivity().getApplicationContext(), "No location set",
						Toast.LENGTH_LONG).show();
			}
		}
		*/

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
			Toast.makeText(getActivity().getApplicationContext(), "Marker removed from the list",
					Toast.LENGTH_SHORT).show();

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
						googleMap.clear();
						break;
					}
					// vectorLoc.remove(vectorLoc.indexOf(m.getPosition()));
					vectorLoc.remove(vectorLoc.get(m));
					markers.remove(markers.get(m));
					/*
					 * googleMap.clear();
					 * 
					 * for(Polyline poly: polylines){ googleMap.addPolyline(poly); }
					 * int a = 0;
					 * 
					 * for(Marker kk: markers){ String s = "num:" + a;
					 * googleMap.addMarker(new
					 * MarkerOptions().position(kk.getPosition()) .draggable(true)
					 * .icon(BitmapDescriptorFactory
					 * .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))); a++; }
					 */

				}

			}

			markerClicked = true;

			return true;

		}
		/*Context mContext;
		
		public void MyDialogFragment() {
	        mContext = getActivity();
	    }
		
		  
	    
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
	        alertDialogBuilder.setTitle("Really?");
	        alertDialogBuilder.setMessage("Are you sure?");
	        //null should be your on click listener
	        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
	        	@Override
	            public void onClick(DialogInterface dialog, int which) {
	        	Toast.makeText(getActivity().getApplicationContext(),"Reset map",Toast.LENGTH_LONG).show();
					googleMap.clear();
					drawMarker(location);
					markers.clear();
					vectorLoc.clear();
	                dialog.dismiss();
	            }
	        });
	        
	        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	            }
	        });


	        return alertDialogBuilder.create();
	    }*/
		
//		 private void showDialog() {
//		        DialogFragment newFragment = ExampleDialogFragment.newInstance();
//		        newFragment.show(getFragmentManager(), "dialog");
//		    }
//		 
//
//		
//		
//		@SuppressWarnings("deprecation")
//		public void openDialog(View v) {
//				showDialog(1);
//		}
//		
//		protected Dialog onCreateDialog(int id) {
//			switch (id) {
//				case 1:
//				// Create out AlterDialog
//				Builder builder = new AlertDialog.Builder(getActivity());
//				builder.setMessage("Reset markers and lines?");
//				builder.setCancelable(true);
//				builder.setPositiveButton("I agree", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						Toast.makeText(getActivity().getApplicationContext(),"Reset map",Toast.LENGTH_LONG).show();
//						googleMap.clear();
//						drawMarker(location);
//						markers.clear();
//						vectorLoc.clear();
//					}
//				});
//				
//				builder.setNegativeButton("No, nooooo", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						Toast.makeText(getActivity().getApplicationContext(),"Keep my choices",Toast.LENGTH_LONG).show();
//					}
//				});
//				AlertDialog dialog = builder.create();
//				dialog.show();
//				}
//				return super.onCreateDialog(id);
//				}
		


}

	
	




