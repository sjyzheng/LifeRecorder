package edu.neu.cs5520.liferecorder;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface VenueItemDao {

    @Insert
    void insertVenueItem(VenueItem item);

    @Query("SELECT * FROM venue WHERE lat =:lat and lng =:lng;")
    List<VenueItem> findLinkItem(double lat, double lng);

    @Query("DELETE FROM venue WHERE lat =:lat and lng =:lng;")
    void deleteVenueItem(double lat, double lng);

    @Query("SELECT * FROM venue WHERE item_type = 'venue';")
    List<VenueItem> getAllVenueItems();

    @Query("SELECT * FROM venue WHERE item_type = 'photo' AND photo_uri IS NOT NULL;")
    List<VenueItem> getAllPhotos();

    @Query("SELECT * FROM venue;")
    List<VenueItem> getAllItems();

    @Query("SELECT * from VENUE where created_at BETWEEN :startDate and :endDate;")
    List<VenueItem> getItemsOnDates(Date startDate, Date endDate);

    @Query("SELECT DISTINCT created_at FROM venue;")
    List<Date> getUniqueDates();

    @Query("SELECT address FROM venue ORDER BY id DESC LIMIT 1;")
    String getLastAddress();

}
