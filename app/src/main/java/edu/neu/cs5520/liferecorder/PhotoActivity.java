package edu.neu.cs5520.liferecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static edu.neu.cs5520.liferecorder.Constants.TYPE_PHOTO;

public class PhotoActivity extends AppCompatActivity {

    private static final int CAMERA_CAPTURE = 102;
    private File imageToSave;
    private String TAG = "photoActivity";
    private String pictureFilePath;
    private Uri pictureUri;

    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<VenueItem> pictures;

    private String placeAddress;
    private String placeName;
    private VenueItem venueItem;
    private VenueItemRepository repository;

    private FusedLocationProviderClient locationProviderClient;
    private Location lastLocation;

    public void setLocationListener() {
        locationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known locationTextView. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle locationTextView object
                            Log.e(TAG, " " + location.getLatitude() + " " + location.getLongitude());
                            lastLocation = location;
//                            double lng = lastLocation.getLongitude();
//                            double lat = lastLocation.getLatitude();
//                            VenueItem venueItem = new VenueItem(null, null, lat, lng, null, pictureUri, TYPE_PHOTO);
//                            insertVenueItem(venueItem);
//                            toastMakeText("Image saved to:\n" +
//                                    pictureFilePath);
                        } else {
                            Log.e(TAG, "Location is null");
                        }
                    }
                });


    }

//    private void insertVenueItem(VenueItem venueItem) {
//        this.repository.insertVenueItem(venueItem);
//    }
//
//    private void toastMakeText(String s) {
//        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Log.e(TAG, "created");

        Button openCameraButton = findViewById(R.id.takePhoto);

        if (!hasCamera())
            openCameraButton.setEnabled(false);

        MainActivity.VenueItemRepositoryAsyncTask task = new MainActivity.VenueItemRepositoryAsyncTask();
        task.execute(this);
        try {
            this.repository = task.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

//        File dir= getExternalFilesDir(Environment.DIRECTORY_PICTURES);

//        if (dir.listFiles() != null) {
//            pictures = Arrays.asList(dir.listFiles());
//        } else {
//            pictures = new ArrayList<>();
//        }

        pictures = this.repository.getAllPhotoItems();
        Log.e(TAG, "size of pictures: " + pictures.size());
//        for (int i = 0; i <= pictures.size(); i++) {
//            Log.e(TAG, pictures.get(i).lat + " " + pictures.get(i).lng + " " + pictures.get(i).photoUri + " " + pictures.get(i).itemType);
//        }
        mAdapter = new RecyclerAdapter(pictures);
        recyclerView.setAdapter(mAdapter);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setLocationListener();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.buttonNavigationBar);
        bottomNavigationView.setSelectedItemId(R.id.action_camera);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        startMainActivity();
                        break;
                    case R.id.action_camera:
                        break;
                    case R.id.action_log:
                        startLogActivity();
                        break;
                }
                return true;
            }
        });

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void startLogActivity() {
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
    }


    private boolean hasCamera() {
        return (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY));
    }


    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == CAMERA_CAPTURE) {
            if (resultCode == RESULT_OK) {
//                Intent galleryIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                Uri picUri = Uri.fromFile(imageToSave);
//                galleryIntent.setData(picUri);
//                this.sendBroadcast(galleryIntent);
//

                Log.e(TAG, "picture path: " + pictureFilePath);


                Intent intent = new Intent(this, FetchAddressIntentService.class);
                ResultReceiver resultReceiver = new PhotoAddressResultReceiver(new Handler(),this);
                intent.putExtra(Constants.RECEIVER, resultReceiver);
                intent.putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation);
                startService(intent);

                Toast.makeText(this, "Image saved to:\n" +
                        pictureFilePath, Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Image capturing cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to capture image",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void openCamera(View view) {
        imageToSave = createImageFile();
        pictureFilePath = imageToSave.getAbsolutePath();
        pictureUri = FileProvider.getUriForFile(this, "edu.neu.cs5520.android.fileprovider", imageToSave);
        Log.e(TAG, "picture file path: " + pictureFilePath);
        Log.e(TAG, "picture uri: " + pictureUri.toString());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (imageToSave != null && intent.resolveActivity(getPackageManager()) != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
            startActivityForResult(intent, CAMERA_CAPTURE);
        } else {
            Toast.makeText(this,
                    "No file specified",
                    Toast.LENGTH_SHORT).show();
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



    class PhotoAddressResultReceiver extends ResultReceiver {
        private Context context;


        public PhotoAddressResultReceiver(Handler handler, Context context ) {
            super(handler);
            this.context = context;
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
                return;
            }

            placeAddress = addressOutput.split("\n")[1];
            placeName = addressOutput.split("\n")[0];

            double lng = lastLocation == null ? -1 : lastLocation.getLongitude();
            double lat = lastLocation == null ? -1 : lastLocation.getLatitude();

            VenueItem venueItem = new VenueItem(placeName, placeAddress, lat, lng, "Photo taken", pictureUri, TYPE_PHOTO);
            repository.insertVenueItem(venueItem);
            mAdapter.add(mAdapter.getItemCount(), venueItem);
            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                Toast.makeText(getApplicationContext(), "address_found: " + addressOutput, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
