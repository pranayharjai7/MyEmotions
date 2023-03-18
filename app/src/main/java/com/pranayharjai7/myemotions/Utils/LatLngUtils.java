package com.pranayharjai7.myemotions.Utils;

import com.google.android.gms.maps.model.LatLng;

public class LatLngUtils {

    public static String convertLatLngToString(LatLng latLng) {
        return latLng.latitude + "," + latLng.longitude;
    }

    public static LatLng convertStringToLatLng(String location) {
        double lat = Double.parseDouble(location.split(",")[0]);
        double lng = Double.parseDouble(location.split(",")[1]);
        return new LatLng(lat, lng);
    }
}
