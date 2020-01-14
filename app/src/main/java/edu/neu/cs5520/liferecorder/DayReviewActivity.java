package edu.neu.cs5520.liferecorder;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Date;
import java.util.List;

public class DayReviewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_review);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.buttonNavigationBar);
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

        Intent intent = getIntent();
        try {
            Date startDate = Constants.DATE_FORMAT.parse(intent.getStringExtra("startDate"));
            Date endDate = new Date (startDate.getTime() + Constants.DAY_IN_MILLIS);
            DayReviewModel dayReviewModel = new DayReviewModel(getApplication(), startDate, endDate);
            List<VenueItem> logList = dayReviewModel.getLogList();
            DayReviewAdapter dayReviewAdapter = new DayReviewAdapter(this, logList);
            TextView dateReviewDate = findViewById(R.id.dayReviewDate);
            dateReviewDate.setText(intent.getStringExtra("startDate"));
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.dayListView);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(dayReviewAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
            System.out.println("recyclerView set!!!!!!!!!!");
        }catch (Exception e){
            e.printStackTrace();
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
