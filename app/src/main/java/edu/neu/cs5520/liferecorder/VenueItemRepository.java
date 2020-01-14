package edu.neu.cs5520.liferecorder;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class VenueItemRepository {

    private VenueItemDao venueItemDao;
    private final static String TAG = "VenueItemRepository";


    public VenueItemRepository(Context context){
        VenueItemDatabase db = VenueItemDatabase.getDatabase(context);
        venueItemDao = db.venueItemDao();
        Log.i("LinkItemRepository","set up repository");
    }


    public void insertVenueItem(VenueItem venueItem){
        InsertAsyncTask task = new InsertAsyncTask(this.venueItemDao);
        task.execute(venueItem);
    }

    public void deleteLinkItem(double lat, double lng){
        DeleteAsyncTask task = new DeleteAsyncTask(this.venueItemDao);
        task.execute(lat,lng);
    }

    public String getLastAddress() {
        GetLastAddressTask task = new GetLastAddressTask(this.venueItemDao);
        task.execute();
        String ans = "";
        try{
            ans = task.get();
        }catch (Exception e){e.printStackTrace();}
        return ans;
    }



    public List<VenueItem> getAllItems(){
        GetAllItemsTask task = new GetAllItemsTask(this.venueItemDao);
        task.execute();
        List<VenueItem> ans = new ArrayList<>();
        try{
            ans = task.get();
        }catch (Exception e){e.printStackTrace();}
        return ans;
    }

    public List<VenueItem> getAllVenueItems() {
        GetAllVenueItemsTask task = new GetAllVenueItemsTask(this.venueItemDao);
        task.execute();
        List<VenueItem> ans = new ArrayList<>();
        try{
            ans = task.get();
        }catch (Exception e){e.printStackTrace();}
        return ans;
    }

    public List<VenueItem> getAllPhotoItems(){
        GetAllPhotosTask task = new GetAllPhotosTask(this.venueItemDao);
        task.execute();
        List<VenueItem> ans = new ArrayList<>();
        try{
            ans = task.get();
        }catch (Exception e){e.printStackTrace();}
        return ans;
    }
    public List<VenueItem> getItemsOnDates(Date startDate, Date endDate){
        GetItemsOnDatesTask task = new GetItemsOnDatesTask(this.venueItemDao);
        task.execute(startDate,endDate);
        List<VenueItem> ans = new ArrayList<>();
        try{
            ans = task.get();
        }catch (Exception e){e.printStackTrace();}
        return ans;
    }


    public List<Date> getUniqueDates(){
        GetUniqueDatesTask task = new GetUniqueDatesTask(this.venueItemDao);
        task.execute();
        List<Date> ans = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try{
            ans = task.get();
        }catch (Exception e){e.printStackTrace();}
        Set<String> dates = new TreeSet<>();
        for(Date d: ans) {
            dates.add(dateFormat.format(d));
        }
        List<Date> ret = new ArrayList<>();
        try {
            for (String str : dates) {
                ret.add(dateFormat.parse(str));

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }




    private static class GetAllVenueItemsTask extends AsyncTask<Void,Void,List<VenueItem>>{
        private VenueItemDao venueItemDao;
        GetAllVenueItemsTask(VenueItemDao venueItemDao){this.venueItemDao = venueItemDao;}

        @Override
        protected List<VenueItem> doInBackground(final Void ...params){
            return venueItemDao.getAllVenueItems();
        }
    }

    private static class GetAllItemsTask extends AsyncTask<Void,Void,List<VenueItem>>{
        private VenueItemDao venueItemDao;
        GetAllItemsTask(VenueItemDao venueItemDao){this.venueItemDao = venueItemDao;}

        @Override
        protected List<VenueItem> doInBackground(final Void ...params){
            return venueItemDao.getAllItems();
        }
    }

    private static class GetAllPhotosTask extends AsyncTask<Void,Void,List<VenueItem>>{
        private VenueItemDao venueItemDao;
        GetAllPhotosTask(VenueItemDao venueItemDao){this.venueItemDao = venueItemDao;}

        @Override
        protected List<VenueItem> doInBackground(final Void ...params){
            return venueItemDao.getAllPhotos();
        }
    }




    private static class InsertAsyncTask extends AsyncTask<VenueItem,Void,Void> {

        private VenueItemDao venueItemDao;
        InsertAsyncTask(VenueItemDao venueItemDao){
            this.venueItemDao = venueItemDao;
        }


        @Override
        protected Void doInBackground(final VenueItem ...params){
            try {
                venueItemDao.insertVenueItem(params[0]);
            }catch (Exception e){
                e.printStackTrace();
            }
            Log.i(TAG,"Inserted: " + params[0]);
            return null;
        }
    }


    private static class DeleteAsyncTask extends AsyncTask<Double, Void, Void>{
        private VenueItemDao venueItemDao;
        DeleteAsyncTask(VenueItemDao venueItemDao){
            this.venueItemDao = venueItemDao;
        }

        @Override
        protected Void doInBackground(final Double ...params){
            this.venueItemDao.deleteVenueItem(params[0], params[1]);
            return null;
        }
    }

    private static class GetUniqueDatesTask extends AsyncTask<Void,Void,List<Date>>{
        private VenueItemDao venueItemDao;
        GetUniqueDatesTask(VenueItemDao venueItemDao){this.venueItemDao = venueItemDao;}

        @Override
        protected List<Date> doInBackground(final Void ... params){
            return venueItemDao.getUniqueDates();
        }
    }

    private static class GetItemsOnDatesTask extends AsyncTask<Date,Void,List<VenueItem>>{
        private VenueItemDao venueItemDao;
        GetItemsOnDatesTask(VenueItemDao venueItemDao){this.venueItemDao = venueItemDao;}

        @Override
        protected List<VenueItem> doInBackground(final Date ... params){
            assert params.length == 2;
            return venueItemDao.getItemsOnDates(params[0],params[1]);
        }
    }

    private static class GetLastAddressTask extends AsyncTask<Void,Void,String>{
        private VenueItemDao venueItemDao;
        GetLastAddressTask(VenueItemDao venueItemDao){this.venueItemDao = venueItemDao;}

        @Override
        protected String doInBackground(final Void ... params) {
            return venueItemDao.getLastAddress();
        }
    }




}
