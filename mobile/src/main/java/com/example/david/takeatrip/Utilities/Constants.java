package com.example.david.takeatrip.Utilities;

import com.example.david.takeatrip.R;

/**
 * Created by Giacomo on 29/02/2016.
 */
public class Constants {

    //TODO assicurarsi che tutti i codici account siano univoci
    public static final int DEFAULT_COD_ACCOUNT = 0;
    public static final int FACEBOOK_COD_ACCOUNT = 1;
    public static final int GOOGLE_COD_ACCOUNT = 2;

    //TODO assicurarsi che tutti i codici prefissi account siano univoci
    public static final String PREFIX_GOOGLE = "google";
    public static final String PREFIX_FACEBOOK = "facebo";


    //TODO assicurarsi che tutti i codici privacy siano univoci
    public static final int PUBLIC_PRIVACY = 0;
    public static final int TRAVEL_PRIVACY = 1;
    public static final int STOP_PRIVACY = 2;
    public static final int CUSTOM_PRIVACY = 3;
    public static int[] privacy_images = {R.drawable.ic_public_black_36dp, R.drawable.ic_people_black_36dp,
            R.drawable.ic_person_pin_circle_black_36dp, R.drawable.ic_settings_black_36dp};


    //php files
    public static final String ADDRESS_PRELIEVO = "http://www.musichangman.com/TakeATrip/InserimentoDati/";
    public static final String ERROR_TAPPA_NON_INSERITA = "";


    //TODO assicurarsi che tutti i codici request da utilizzare per AcitivityResult siano univoci
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_PICK = 2;
    public static final int REQUEST_COVER_IMAGE_CAPTURE = 3;
    public static final int REQUEST_COVER_IMAGE_PICK = 4;
    public static final int REQUEST_PLACE_PICKER = 5;
    public static final int REQUEST_VIDEO_CAPTURE = 6;
    public static final int REQUEST_VIDEO_PICK = 7;
    public static final int REQUEST_RECORD_CAPTURE = 8;
    public static final int REQUEST_RECORD_PICK = 9;


    //TODO assicurarsi che tutti i codici tipoFile siano univoci
    public static final int IMAGE_FILE = 1;
    public static final int VIDEO_FILE = 2;


    public static final int WIDTH_LAYOUT_PROPRIETARI_ITINERARI = 20;
    public static final int HEIGH_LAYOUT_PROPRIETARI_ITINERARI = 80;
    public static final int DEFAULT_ZOOM_MAP = 10;

    public static final String WEBAPP_ID   = "854482298731-9maeevj20buep03pjefnvvdq4ch0q19j.apps.googleusercontent.com";

    public static final int VIBRATION_MILLISEC = 100;


    //MAPS
    public static final int MAP_POLYLINE_THICKNESS = 12;
    public static final String GOOGLE_MAPS_BLUE = "#05b1fb";
    public static final int LATLNG_BOUNDS_PADDING = 100;


    public static final int MAX_RECORDING_TIME_IN_MILLISEC = 120000;    //2 MINUTES
    public static final int ONE_SEC_IN_MILLISEC = 1000;

    public static final String PATH_AUDIO_FILES = "/TakeATrip/audio";




}
