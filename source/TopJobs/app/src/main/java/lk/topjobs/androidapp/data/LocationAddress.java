package lk.topjobs.androidapp.data;

/*
* 20190515 PS SDB-954 Get the Address from the passing latitude and longitude.
* */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.RemoteException;
import android.provider.Settings;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import lk.topjobs.androidapp.activities.JobCategoryListActivity;

public class LocationAddress {

    public String locationStr;
    public static LocationAddress locationAddress = null;

    private LocationAddress(){
        locationStr = "ALL";
    }

    public static LocationAddress getInstance(){
        if (locationAddress == null)
            locationAddress = new LocationAddress();
        return locationAddress;
    }

    @SuppressLint("MissingPermission")
    public static void getAddressFromLocation(Context context, LocationManager locationManager) {
        try {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());

            List<Address> addressList = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                sb.append(address.getLocality() + address.getPostalCode());
                LocationAddress.getInstance().locationStr = sb.toString();
            }
        } catch (IOException e) {
            LocationAddress.getInstance().locationStr = null;
        } catch (Exception ex){
            showSettingsAlert(context);
        }
    }

    public static void showSettingsAlert(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                context);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Internet and GPS! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_SETTINGS);
                        context.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
}
