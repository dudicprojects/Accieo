package app;


import dudic.accieo.R;

public class Constants {

    public final static String APPNAME = "ACCEIO";

    public final static String BASEURL = "http://ec2-54-169-174-147.ap-southeast-1.compute.amazonaws.com/";
    //public final static String BASEURL = "http://192.168.0.153:8000/";

    public final static String SIGNUP = BASEURL + "api/auth/signup";
    public static final String LOGIN = BASEURL + "api/auth/login";
    public static final String OTP = BASEURL + "api/auth/validateToken";
    public static final String PLACES = BASEURL + "api/places";
    public static final String ADDPLACES = BASEURL + "api/place/store";
    public static final String GET_PLACES = BASEURL + "api/places_all";
    public static final String PLACE_FILTER = BASEURL + "api/filter_places";


    public final static String LATITUDE = "latitude";
    public final static String LONGITUDE = "longitude";
    public static final String ADDRESS = "actual_address";
    public static final String PLACE_NAME = "name";
    public static final String PLACE_TYPE = "type";
    public static final String ACCESSIBLITY_LEVEL = "rate";


    public static final String NULL = "NULL";
    public static final String STATUS = "status";
    public final static String TOKEN = "token";
    public static final String MESSAGE = "message";
    public static final String MOBILE = "mobile";

    public static final String[] PLACE_TYPE_NAMES = {"Administration Building", "Bank", "Cinema", "Education", "Hospital",
            "Hotel", "Mall", "Metro", "Tourist Place", "Parks", "Railway Station", "Restaurants", "Theatre"};


    public static final int[] PLACE_TYPE_IMAGES = new int[]{R.drawable.administrative_building, R.drawable.bank,
            R.drawable.cinema, R.drawable.education, R.drawable.hospital,
            R.drawable.hotel, R.drawable.mall, R.drawable.metro,
            R.drawable.monument, R.drawable.park,
            R.drawable.railway_station, R.drawable.restaurant, R.drawable.theatre};

    public static final String[] FACILITIES_TYPE_NAMES = new String[]{"Ramp", "Tactile Path", "Toilet",
            "Elevator", "Escalator", "Parking"
    };

    public static final int[] FACILITIES_TYPE_ICONS = new int[]{R.drawable.ic_fac_ramp, R.drawable.ic_fac_tactile_path,
            R.drawable.ic_fac_restroom, R.drawable.ic_fac_elevator,
            R.drawable.ic_fac_escalator, R.drawable.ic_fac_parking};

    public static final int[] FACILITIES_TYPE_SELECTORS = new int[]{R.drawable.selector_ramp, R.drawable.selector_tactile_path,
            R.drawable.selector_toilet, R.drawable.selector_elevator,
            R.drawable.selector_escalator, R.drawable.selector_parking};

    public static final int SOURCE_PICKER_REQUEST = 1;
    public static final int DESTINATION_PICKER_REQUEST = 2;

    public static final String SERVER_API_KEY = "AIzaSyCGjxLlzbi1dxhzooJSHVVC-YcZtFkQpsQ";

    public static final String ACTUAL_ADDRESS = "actual_address";
    public static final String FACILITIES = "fascilities";
    public static final String PLACE_INFO = "PLACE_INFO";
    public static final String PLACE_OBJECT = "PLACE_OBJECT";
    public static final String TOKEN_ID = "token_id";
    public static final String SHOW_TUTORIAL = "SHOW_TUTORIAL";

    public static final String formatResponse(String result) {
        StringBuilder str = new StringBuilder(result);
        int i = result.indexOf("{");
        result = str.substring(i);
        return result;
    }

}

