package com.example.david.takeatrip.Utilities;

import com.example.david.takeatrip.R;

/**
 * Created by Giacomo on 29/02/2016.
 */
public class Constants {

    //users account
    public static final int DEFAULT_COD_ACCOUNT = 0;
    public static final int FACEBOOK_COD_ACCOUNT = 1;
    public static final int GOOGLE_COD_ACCOUNT = 2;

    //privacy levels
    public static final int PUBLIC_PRIVACY = 0;
    public static final int TRAVEL_PRIVACY = 1;
    public static final int STOP_PRIVACY = 2;
    public static final int CUSTOM_PRIVACY = 3;

    public static int[] privacy_images = {R.drawable.ic_public_black_36dp, R.drawable.ic_people_black_36dp,
            R.drawable.ic_person_pin_circle_black_36dp, R.drawable.ic_settings_black_36dp};

    //php files
    public static final String ADDRESS_PRELIEVO = "http://www.musichangman.com/TakeATrip/InserimentoDati/";
    public static final String ERROR_TAPPA_NON_INSERITA = "";



    public static final String PREFIX_GOOGLE = "google";
    public static final String PREFIX_FACEBOOK = "facebo";


    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_PICK = 2;
    public static final int REQUEST_COVER_IMAGE_CAPTURE = 3;
    public static final int REQUEST_COVER_IMAGE_PICK = 4;


    public static final int WIDTH_LAYOUT_PROPRIETARI_ITINERARI = 20;
    public static final int HEIGH_LAYOUT_PROPRIETARI_ITINERARI = 80;


}
