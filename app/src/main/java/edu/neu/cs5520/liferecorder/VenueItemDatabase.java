package edu.neu.cs5520.liferecorder;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {VenueItem.class}, version = 3, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class VenueItemDatabase extends RoomDatabase {

    public abstract VenueItemDao venueItemDao();

    private static VenueItemDatabase INSTANCE;

    static VenueItemDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (VenueItemDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            VenueItemDatabase.class,
                            "VenueItemDatabase"
                    ).fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
