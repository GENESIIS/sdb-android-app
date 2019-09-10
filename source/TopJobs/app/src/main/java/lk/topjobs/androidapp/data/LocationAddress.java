package lk.topjobs.androidapp.data;

/*
* 20190515 PS SDB-954 Get the Address from the passing latitude and longitude.
* */

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationAddress {

    public String locationStr;
    public static LocationAddress locationAddress = null;

    private LocationAddress(){
        locationStr = null;
    }

    public static LocationAddress getInstance(){
        if (locationAddress == null)
            locationAddress = new LocationAddress();
        return locationAddress;
    }

    @SuppressLint("MissingPermission")
    public static void getAddressFromLocation(Context context, LocationManager locationManager) throws Exception{
        try {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());

            List<Address> addressList = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                sb.append(address.getLocality());
                LocationAddress.getInstance().locationStr = sb.toString();
            }
        } catch (IOException e) {
            LocationAddress.getInstance().locationStr = null;
        } catch (Exception ex){
            Log.e("LocationAddress No GPS;",ex.toString());
        }
    }

}
