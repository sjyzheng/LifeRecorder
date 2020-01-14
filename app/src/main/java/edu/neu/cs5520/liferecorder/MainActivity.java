package edu.neu.cs5520.liferecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionBarContextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Geocoder;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener {

    private ToggleButton autoMode;

    private FusedLocationProviderClient fusedLocationClient;
    private Location lastLocation;
    private LocationCallback locationCallback;
    private boolean mLocationPermissionGranted = false;
    private AddressResultReceiver foreGroundResultReceiver;
    private AddressResultReceiver backGroundResultReceiver;
    private LocationRequest locationRequest;
    private boolean auto_enabled = false;
    private int record_interval = 1000 * 30 * 60;
    private int fast_interval =  1000 * 30 * 60;

    private GoogleMap gMap;

    private final static int LOCATION_ACCESS_CODE = 123;
    public final static int PLACE_CHECK_IN_ACTIVITY = 222;

    private final static String TAG = "MainActivity";

    private final static int AUTOCOMPLETE_REQUEST_CODE = 111;


    private VenueItemRepository repository;


    public static class VenueItemRepositoryAsyncTask extends AsyncTask<Context, Void, VenueItemRepository> {
        @Override
        protected VenueItemRepository doInBackground(Context... context) {
            VenueItemRepository repository = new VenueItemRepository(context[0]);
            return repository;
        }
    }

    private void setLocation(double longitude, double latitude){
        Log.i(TAG, "locationTextView updated: " + Double.toString(longitude) +","+Double.toString(latitude));
        startFetchAddressIntentService(backGroundResultReceiver);
    }

    private void setLocationCallback(){
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                if(locationResult == null){
                    Log.i(TAG, "locationTextView result is null");
                    return;
                }
                for(Location location: locationResult.getLocations()){
                    lastLocation = location;
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    setLocation(longitude, latitude);
                    Log.i(TAG, "new locationTextView set");
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))      // Sets the center of the map to Mountain View
                            .zoom(17)                   // Sets the zoom
                            .build();
                    gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    break;
                }
            }
        };
    }

    protected LocationRequest createLocationRequest(){
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(record_interval);
        locationRequest.setFastestInterval(fast_interval);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }


    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());

    }

    public static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }


    public void setMarker(LatLng latLng, String name,  String date, Uri imageUri) {

        Bitmap resizedBitmap = null;
        BitmapDescriptor icon = null;
        if(imageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                resizedBitmap = resize(bitmap, 100, 100);
                bitmap.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(resizedBitmap!= null ) {
            icon = BitmapDescriptorFactory.fromBitmap(resizedBitmap);
        }
        gMap.addMarker(new MarkerOptions().position(latLng).title(name + "(" + date+")").icon(icon));
    }


    private void moveToLast() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known locationTextView. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle locationTextView object
                            lastLocation = location;
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))      // Sets the center of the map to Mountain View
                                    .zoom(17)                   // Sets the zoom
                                    .build();                   // Creates a CameraPosition from the builder
                            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        VenueItemRepositoryAsyncTask task = new VenueItemRepositoryAsyncTask();
        task.execute(this);
        try {
            this.repository = task.get();
            List<VenueItem> items = repository.getAllItems();
            for(VenueItem item: items){
                System.out.println(item.address);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        foreGroundResultReceiver = new AddressResultReceiver(new Handler(), this, false);
        backGroundResultReceiver = new AddressResultReceiver(new Handler(),this, true);



        // set up locationTextView service
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        moveToLast();
        setLocationCallback();
        createLocationRequest();


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        autoMode = findViewById(R.id.autoMode);
        autoMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoMode.isChecked()) {
                    Toast.makeText(MainActivity.this, "Auto Mode On", Toast.LENGTH_LONG).show();
                    startLocationUpdates();
                } else {
                    Toast.makeText(MainActivity.this,"Auto Mode off!",Toast.LENGTH_SHORT).show();
                    fusedLocationClient.removeLocationUpdates(locationCallback);
                }
            }
        });



        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.action_settings:
//                        startSettingsActivity();
//                        break;
//                }
                return true;
            }
        });



        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.buttonNavigationBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        break;
                    case R.id.action_camera:
                        startPhotoActivity();
                        break;
                    case R.id.action_log:
                        startLogActivity();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public void startPhotoActivity() {
        Intent intent = new Intent(this, PhotoActivity.class);
        startActivity(intent);
    }

    public void startLogActivity() {
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
    }

    private void mapConfig() {
        System.out.println("map configed");
        mLocationPermissionGranted = true;
        gMap.setOnMyLocationButtonClickListener(this);
        gMap.setOnMyLocationClickListener(this);
        gMap.setMyLocationEnabled(true);
        UiSettings uiSettings = gMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        uiSettings.setRotateGesturesEnabled(true);

    }


    /*
    there is a problem on rendering my locationTextView button when first time locationTextView permissions are granted
    https://github.com/googlemaps/android-samples/issues/86
     */

    @Override
    public void onMapReady(GoogleMap map) {
        gMap = map;

        gMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(12, 12)));
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.i("main", "setmylocation enabled true");
            mapConfig();
            moveToLast();
        } else {
            Log.i("main", "setmylocation enabled false");
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                    },
                    LOCATION_ACCESS_CODE);

        }
        List<VenueItem> items = this.repository.getAllVenueItems();
        for (VenueItem item : items) {
            setMarker(new LatLng(item.lat, item.lng), item.name,  item.created.toString(), item.photoUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_ACCESS_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    System.out.println("locationTextView permission granted");
                    mapConfig();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Location permissions not approved, app is exiting now", Toast.LENGTH_LONG).show();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == PLACE_CHECK_IN_ACTIVITY) {
            if(data == null) return;
            setMarker(new LatLng(data.getDoubleExtra("lat", 0)
                            , data.getDoubleExtra("lng", 0)), data.getStringExtra("placeName"),
                     data.getStringExtra("created"),(Uri)data.getParcelableExtra("photoPath"));
        }
    }

    private void showAddPlaceDialog(Context context, final String item1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Add places from")
                .setItems(new String[]{item1, "search"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(MainActivity.this, PlaceCheckInActivity.class);
                            intent.putExtra("current_location", lastLocation);
                            String place_name = item1.split("\n")[0];
                            String address = item1.split("\n")[1];
                            intent.putExtra("place_name", place_name);
                            intent.putExtra("address", address);
                            startActivityForResult(intent, PLACE_CHECK_IN_ACTIVITY);
                        } else {
// Set the fields to specify which types of placeTextView data to
// return after the user has made a selection.
                            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

// Start the autocomplete intent.
                            Intent intent = new Autocomplete.IntentBuilder(
                                    AutocompleteActivityMode.FULLSCREEN, fields)
                                    .build(MainActivity.this);
                            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

                        }
                    }
                });

        builder.create().show();
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current locationTextView:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        fusedLocationClient.getLastLocation();
        if(lastLocation != null) {
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastLocation.getLatitude(),
                            lastLocation.getLongitude()), 17));
        }
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;

    }

    protected void startFetchAddressIntentService(AddressResultReceiver resultReceiver) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation);
        startService(intent);
    }

    public void fetchAddressButtonHandler(View view) {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        lastLocation = location;

                        // In some rare cases the locationTextView returned can be null
                        if (lastLocation == null) {
                            return;
                        }

                        if (!Geocoder.isPresent()) {
                            Toast.makeText(MainActivity.this,
                                    "no_geocoder_available",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Start service and update UI to reflect new locationTextView
                        startFetchAddressIntentService(foreGroundResultReceiver);
//                        updateUI();
                    }
                });
    }


    class AddressResultReceiver extends ResultReceiver {
        private Context context;
        private Boolean runInBackground;

        public AddressResultReceiver(Handler handler, Context context, boolean runInBackground ) {
            super(handler);
            this.context = context;
            this.runInBackground = runInBackground;

        }


        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData == null) {
                return;
            }

            // Display the address string
            // or an error message sent from the intent service.
            String addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            if (addressOutput == null) {
                addressOutput = "";
            }

            if(!runInBackground) {
                showAddPlaceDialog(context, addressOutput);
            }else{
                Location location = resultData.getParcelable(Constants.LOCATION_DATA);
                double lat = location != null? location.getLatitude(): -1;
                double lng = location != null? location.getLongitude(): -1;
                VenueItem item = new VenueItem("auto: "+ addressOutput.split("\n")[0],addressOutput,lat,lng,"auto added",null,Constants.TYPE_VENUE);
                repository.insertVenueItem(item);
            }

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                Toast.makeText(getApplicationContext(), "address_found: " + addressOutput, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
