package edu.neu.cs5520.liferecorder;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DayReviewModel extends AndroidViewModel {


    private VenueItemRepository venueItemRepository;
    private List<VenueItem> logList;

    private Date startDate, endDate;

    public DayReviewModel(Application application, Date startDate, Date endDate) {
        super(application);
        MainActivity.VenueItemRepositoryAsyncTask task = new MainActivity.VenueItemRepositoryAsyncTask();
        this.startDate = startDate;
        this.endDate = endDate;


        logList = new ArrayList<>();
        task.execute(getApplication());
        try {
            venueItemRepository = task.get();
            logList = venueItemRepository.getItemsOnDates(startDate,  endDate);
            for (VenueItem item : logList) {
                System.out.println("dayreview: "+item.name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<VenueItem> getLogList() {return logList;}
}
