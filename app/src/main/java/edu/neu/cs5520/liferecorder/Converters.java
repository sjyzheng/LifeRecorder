package edu.neu.cs5520.liferecorder;


import android.net.Uri;

import androidx.room.TypeConverter;

import java.util.Date;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String fromUri(Uri photoURI) {
        return photoURI == null ? null : photoURI.toString();
    }

    @TypeConverter
    public static Uri stringToUri(String s) {
        return s == null ? null : Uri.parse(s);
    }
}
