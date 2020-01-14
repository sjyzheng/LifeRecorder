package edu.neu.cs5520.liferecorder;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static edu.neu.cs5520.liferecorder.Constants.TYPE_VENUE;

public class PlaceCheckInActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Location lastLocation;
    private GoogleMap gMap;

    private VenueItemRepository repository;


    private String placeName;
    private String address;

    private static final String TAG = "PlaceCheckInActivity";

    public static final int CAMERA_CAPTURE_WHEN_CHECK_IN = 333;

    private Uri pictureUri;
    private String pictureFilePath;

    private ImageView image;



    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_place_check_in);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        MainActivity.VenueItemRepositoryAsyncTask task = new MainActivity.VenueItemRepositoryAsyncTask();
        task.execute(this);
        try {
            this.repository = task.get();
        }catch (Exception e){
            e.printStackTrace();
        }

        image = (ImageView) findViewById(R.id.imageView);
        image.setImageResource(R.drawable.ic_photo_camera);


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        lastLocation = intent.getParcelableExtra("current_location");
        System.out.println(lastLocation);
        placeName = intent.getStringExtra("place_name");
        address = intent.getStringExtra("address");


        TextView nameView = findViewById(R.id.nameView);
        nameView.setText(placeName);
        TextView addressView = findViewById(R.id.addressView);
        addressView.setText(address);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.buttonNavigationBar);
        bottomNavigationView.setSelectedItemId(R.id.action_log);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        // TODO: start home activity
                        startMainActivity();
                        break;
                    case R.id.action_camera:
                        // TODO: start camera activity
                        startPhotoActivity();
                        break;
                    case R.id.action_log:
                        // TODO: start log activity
                        startLogActivity();
                        break;
                }
                return true;
            }
        });

    }

    private void mapConfig(){
        UiSettings uiSettings = gMap.getUiSettings();
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setScrollGesturesEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        gMap = map;
        mapConfig();
        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        gMap.addMarker(new MarkerOptions().position(latLng).title("locationTextView"));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(17)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void finish(View view){
        this.finish();
    }

    public void addPlace(View view){
        TextView text = findViewById(R.id.descriptionText);
        VenueItem venueItem = new VenueItem(placeName, address, lastLocation.getLatitude(), lastLocation.getLongitude(), text.getText().toString(), pictureUri, TYPE_VENUE);
        repository.insertVenueItem(venueItem);
        Intent intent = new Intent();
        intent.putExtra("lat", venueItem.lat);
        intent.putExtra("lng", venueItem.lng);
        intent.putExtra("placeName", venueItem.name);
        intent.putExtra("address", venueItem.address);
        intent.putExtra("created", venueItem.created.toString());
        intent.putExtra("photoPath", pictureUri);
        setResult(MainActivity.PLACE_CHECK_IN_ACTIVITY, intent);
        finish();
    }

    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if (requestCode == CAMERA_CAPTURE_WHEN_CHECK_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Image saved to:\n" +
                        pictureUri.toString(), Toast.LENGTH_LONG).show();
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageURI(pictureUri);
            } else if (resultCode == RESULT_CANCELED) {
                pictureUri = null;
                Toast.makeText(this, "Image capturing cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to capture image",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private File createImageFile() {
        File image = null;
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "LR_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        storageDir.mkdir();
        image = new File(storageDir, pictureFile + ".jpg");
        return image;
    }

    public void takePhoto(View view) {
        File imageToSave = createImageFile();
        pictureFilePath = imageToSave.getAbsolutePath();
        pictureUri = FileProvider.getUriForFile(this, "edu.neu.cs5520.android.fileprovider", imageToSave);
        Log.e(TAG, "picture file path: " + pictureFilePath);
        Log.e(TAG, "picture uri: " + pictureUri.toString());
        Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        if (imageToSave != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
            startActivityForResult(intent, CAMERA_CAPTURE_WHEN_CHECK_IN);
        } else {
            Toast.makeText(this,
                    "No file specified",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void startPhotoActivity() {
        Intent intent = new Intent(this, PhotoActivity.class);
        startActivity(intent);
    }

    public void startLogActivity() {
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
    }


}
