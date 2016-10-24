package com.takeatrip.Utilities;

import com.takeatrip.R;

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


//    public static int[] privacy_images = {R.drawable.ic_public_black_36dp, R.drawable.ic_people_black_36dp,
//            R.drawable.ic_person_pin_circle_black_36dp, R.drawable.ic_settings_black_36dp};

    //i nuovi livelli sono: public, followers, travel, private
    public static int[] privacy_images = {R.drawable.ic_public_black_36dp, R.drawable.ic_people_black_36dp,
            R.drawable.ic_person_pin_circle_black_36dp, R.drawable.ic_lock_black_36dp};


    //php files


    public static final String ADDRESS_TAT = "http://ec2-54-194-7-136.eu-west-1.compute.amazonaws.com/";
    public static final String PREFIX_ADDRESS = ADDRESS_TAT + "InserimentoDati/";


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
    public static final int AUDIO_FILE = 3;
    public static final int NOTE_FILE = 4;


    public static final int WIDTH_LAYOUT_PROPRIETARI_ITINERARI = 20;
    public static final int HEIGH_LAYOUT_PROPRIETARI_ITINERARI = 80;
    public static final int DEFAULT_ZOOM_MAP = 10;

    public static final String WEBAPP_ID = "854482298731-9maeevj20buep03pjefnvvdq4ch0q19j.apps.googleusercontent.com";
    public static final String AMAZON_POOL_ID = "eu-west-1:9a6dd1d8-f20f-4bc3-8af6-edab26a39164";
    public static final String BUCKET_NAME = "takeatripusers";
    public static final String BUCKET_TRAVELS_NAME = "takeatriptravels";


    public static final String TRAVEL_COVER_IMAGE_LOCATION = "coverTravelImages";
    public static final String PROFILE_PICTURES_LOCATION = "profilePictures";
    public static final String COVER_IMAGES_LOCATION = "coverImages";
    public static final String TRAVEL_IMAGES_LOCATION = "travelImages";
    public static final String TRAVEL_VIDEOS_LOCATION = "travelVideo";
    public static final String TRAVEL_AUDIO_LOCATION = "travelAudio";




    public static final int VIBRATION_MILLISEC = 100;


    //MAPS
    public static final int MAP_POLYLINE_THICKNESS = 12;
    public static final String GOOGLE_MAPS_BLUE = "#05b1fb";
    public static final int LATLNG_BOUNDS_PADDING = 100;


    public static final int MAX_RECORDING_TIME_IN_MILLISEC = 120000;    //2 MINUTES
    public static final int ONE_SEC_IN_MILLISEC = 1000;

    //TODO verificare il valore corretto
    public static final int NOTE_MAX_LENGTH = 1000;


    public static final int QUALITY_PHOTO = 60;


    public static final String NAME_IMAGES_PROFILE_DEFAULT = "profileTAT.jpg";
    public static final String NAME_IMAGES_COVER_DEFAULT = "coverTAT.jpg";
    public static final String NAME_IMAGES_TRAVEL_DEFAULT = "imageTravel.jpg";


    public static final String DISPLAYED_DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATABASE_DATE_FORMAT = "yyyy-MM-dd";
    public static final String CURRENT_DATE_ID = "currentDate";
    public static final String DATE_FORMAT_ID = "dateFormat";
    public static final String FILE_NAME_TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss_SSS";


    public static final int LIMIT_NAMES_PROFILE = 18;

    public static final int ONE_HOUR_IN_MILLISEC = 1000 * 60 * 60;


    public static final String COUNT_FOLLOW_ID = "count(*)";
    public static final String IMAGE_EXT = ".jpg";
    public static final String VIDEO_EXT = ".3gp";
    public static final String AUDIO_EXT = ".3gp";


    public static final String QUERY_TRAVEL_IMAGES = "QueryImagesOfTravel.php";
    public static final String QUERY_TRAVEL_VIDEOS = "QueryVideosOfTravel.php";
    public static final String QUERY_TRAVEL_AUDIO = "QueryAudioOfTravel.php";
    public static final String QUERY_TRAVEL_NOTES = "QueryNotesOfTravel.php";

    public static final String QUERY_STOP_IMAGES = "QueryImagesOfStop.php";
    public static final String QUERY_STOP_VIDEOS = "QueryVideosOfStop.php";
    public static final String QUERY_STOP_AUDIO = "QueryAudioOfStop.php";
    public static final String QUERY_STOP_NOTES = "QueryNoteTappa.php";

    public static final String QUERY_DEL_IMAGE = "DeleteStopImage.php";
    public static final String QUERY_DEL_VIDEO = "DeleteStopVideo.php";
    public static final String QUERY_DEL_AUDIO = "DeleteStopAudio.php";
    public static final String QUERY_DEL_NOTE = "DeleteStopNote.php";

    public static final int BASE_DIMENSION_OF_IMAGE_PARTICIPANT = 100;
    public static final int BASE_DIMENSION_OF_SPACE = 20;


    public static final String DEVICE_DIR_ROOT = "TakeATrip";
    public static final String DEVICE_DIR_IMAGES = "TakeATrip Images";
    public static final String DEVICE_DIR_VIDEOS = "TakeATrip Videos";
    public static final String DEVICE_DIR_AUDIO = "TakeATrip Audio";



}
