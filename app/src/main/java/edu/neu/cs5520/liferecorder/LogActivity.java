package edu.neu.cs5520.liferecorder;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class LogActivity extends AppCompatActivity {
    private LogListAdapter logListAdapter;
    private LogViewModel logViewModel;
    private RecyclerView recyclerView;
    private Map<Date,List<VenueItem>> logList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.buttonNavigationBar);
        bottomNavigationView.setSelectedItemId(R.id.action_log);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        startMainActivity();
                        break;
                    case R.id.action_camera:
                        startPhotoActivity();
                        break;
                    case R.id.action_log:
                        break;
                }
                return true;
            }
        });

        logViewModel = new LogViewModel(getApplication());
        logList = logViewModel.getItemMap();

        logListAdapter = new LogListAdapter(this, logList);


        recyclerView = (RecyclerView) findViewById(R.id.dayListView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(logListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        logListAdapter.setOnItemClickListener(new LogListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(LogActivity.this, DayReviewActivity.class);
                Date date = logListAdapter.getIthDate(position);
                try {
                    intent.putExtra("startDate", Constants.DATE_FORMAT.format(date));
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

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



//    public void animateIntent(View view) {
//
//        // Ordinary Intent for launching a new activity
//        Intent intent = new Intent(this, YourSecondActivity.class);
//
//        // Get the transition name from the string
//        String transitionName = getString(R.string.transition_string);
//
//        // Define the view that the animation will start from
//        View viewStart = findViewById(R.id.card_view);
//
//        ActivityOptionsCompat options =
//
//                ActivityOptionsCompat.makeSceneTransitionAnimation(this,
//                        viewStart,   // Starting view
//                        transitionName    // The String
//                );
//        //Start the Intent
//        ActivityCompat.startActivity(this, intent, options.toBundle());
//
//    }
}
