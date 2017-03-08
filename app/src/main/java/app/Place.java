package app;


import org.parceler.Parcel;

import dudic.accieo.R;

@Parcel
public class Place {

    public String name, address, rate, facilties, type;

    public Place(String name, String address, String rate, String facilties, String type) {
        this.name = name;
        this.address = address;
        this.rate = rate;
        this.facilties = facilties;
        this.type = type;
    }

    public Place() {
    }

    public static int getPlaceDrawable(int type, int level) {
        switch (type + 1) {
            case 1:
                switch (level) {
                    case 0:
                        return R.drawable.administrative_building_red;
                    case 1:
                        return R.drawable.administrative_building_yellow;
                    case 2:
                        return R.drawable.administrative_building_green;
                }
            case 2:
                switch (level) {
                    case 0:
                        return R.drawable.bank_red;
                    case 1:
                        return R.drawable.bank_yellow;
                    case 2:
                        return R.drawable.bank_green;
                }
            case 3:
                switch (level) {
                    case 0:
                        return R.drawable.cinema_red;
                    case 1:
                        return R.drawable.cinema_yellow;
                    case 2:
                        return R.drawable.cinema_green;
                }
            case 4:
                switch (level) {
                    case 0:
                        return R.drawable.education_red;
                    case 1:
                        return R.drawable.education_yellow;
                    case 2:
                        return R.drawable.education_green;
                }
            case 5:
                switch (level) {
                    case 0:
                        return R.drawable.hospital_red;
                    case 1:
                        return R.drawable.hospital_yellow;
                    case 2:
                        return R.drawable.hospital_green;
                }
            case 6:
                switch (level) {
                    case 0:
                        return R.drawable.hotel_red;

                    case 1:
                        return R.drawable.hotel_yellow;

                    case 2:
                        return R.drawable.hotel_green;

                }
            case 7:
                switch (level) {
                    case 0:
                        return R.drawable.mall_red;

                    case 1:
                        return R.drawable.mall_yellow;

                    case 2:
                        return R.drawable.mall_green;

                }
            case 8:
                switch (level) {
                    case 0:
                        return R.drawable.metro_red;

                    case 1:
                        return R.drawable.metro_yellow;

                    case 2:
                        return R.drawable.metro_green;

                }
            case 9:
                switch (level) {
                    case 0:
                        return R.drawable.monument_red;
                    case 1:
                        return R.drawable.monument_yellow;
                    case 2:
                        return R.drawable.monument_green;

                }
            case 10:
                switch (level) {
                    case 0:
                        return R.drawable.park_red;

                    case 1:
                        return R.drawable.park_yellow;

                    case 2:
                        return R.drawable.park_green;

                }
            case 11:
                switch (level) {
                    case 0:
                        return R.drawable.railway_station_red;

                    case 1:
                        return R.drawable.railway_station_yellow;

                    case 2:
                        return R.drawable.railway_station_green;

                }
            case 12:
                switch (level) {
                    case 0:
                        return R.drawable.restaurant_red;

                    case 1:
                        return R.drawable.restaurant_yellow;

                    case 2:
                        return R.drawable.restaurant_green;

                }
            case 13:
                switch (level) {
                    case 0:
                        return R.drawable.theatre_red;

                    case 1:
                        return R.drawable.theatre_yellow;

                    case 2:
                        return R.drawable.theatre_green;

                }
            default:
                return R.drawable.hospital_green;
        }
    }

}
