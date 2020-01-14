package edu.neu.cs5520.liferecorder;

import java.text.SimpleDateFormat;

public final class Constants {
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "edu.neu.cs5520.liferecorder";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";

    public static final String TYPE_VENUE = "venue";
    public static final String TYPE_PHOTO = "photo";
    public static final String LOCATION_DATA = PACKAGE_NAME +".LOCATION_DATA";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    public static final int DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
}
