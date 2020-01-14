package edu.neu.cs5520.liferecorder;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

public class LogViewModel extends AndroidViewModel {
    private VenueItemRepository venueItemRepository;
    private Map<Date,List<VenueItem>> logList;


    public LogViewModel(Application application) {
        super(application);
        MainActivity.VenueItemRepositoryAsyncTask task = new MainActivity.VenueItemRepositoryAsyncTask();


        logList = new TreeMap<>();
        task.execute(getApplication());
        try {
            venueItemRepository = task.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<VenueItem> venueItems = venueItemRepository.getAllItems();
        Collections.sort(venueItems,
                new Comparator<VenueItem>() {
                    @Override
                    public int compare(VenueItem o1, VenueItem o2) {
                        return (int) (o1.created.getTime() - o2.created.getTime());
                    }
        });

        for(VenueItem item: venueItems){
            System.out.println(item.name);
        }

        List<Date> dates = venueItemRepository.getUniqueDates();
        int i = 0, j = 0;

        while(i < dates.size()){
            Date curDate = dates.get(i);
            logList.put(curDate,new ArrayList<VenueItem>());
            System.out.println(curDate.getTime());
            while(j < venueItems.size() && venueItems.get(j).created.getTime() >= curDate.getTime()
                    && venueItems.get(j).created.getTime() < curDate.getTime() + Constants.DAY_IN_MILLIS){
                logList.get(curDate).add(venueItems.get(j));
                System.out.println("venue item: " + venueItems.get(j).name);

                j++;
            }
            i++;
        }


        System.out.println(logList);

    }

    public void insert(VenueItem venueItem) {venueItemRepository.insertVenueItem(venueItem);}

    public Map<Date,List<VenueItem>> getItemMap(){return this.logList;}

    public List<VenueItem> getLogList() {return venueItemRepository.getAllItems();}

}
