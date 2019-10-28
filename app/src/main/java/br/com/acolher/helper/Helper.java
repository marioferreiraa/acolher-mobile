package br.com.acolher.helper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.security.Key;
import java.util.List;

import br.com.acolher.R;
import br.com.acolher.chamaTelas;

public class Helper implements OnMapReadyCallback{

    private static GoogleMap mMap;
    private static ProgressDialog progressDialog;
    private static LatLng latLng = null;
    static Marker markerLocale = null;

    public static void setSharedPreferences(String key, Object value, Integer type, Context context){
        SharedPreferences sharedPreferences = initSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (type){
            case 1:
                editor.putInt(key, (Integer) value);
                break;
            case 2:
                editor.putString(key, (String) value);
                break;
            case 3:
                editor.putFloat(key, (Float) value);
                break;
            case 4:
                editor.putBoolean(key, (Boolean) value);
                break;
            case 5:
                editor.putLong(key, (Long) value);
                break;
            default:
                break;
        }

        editor.apply();

    }

    public static Object getSharedPreferences(String key, Object defaultValue, Integer type, Context context){
        SharedPreferences sharedPreferences = initSharedPreferences(context);
        switch (type){
            case 1:
                return sharedPreferences.getInt(key, (Integer) defaultValue);
            case 2:
                return sharedPreferences.getString(key, (String) defaultValue);
            case 3:
                return sharedPreferences.getFloat(key, (Float) defaultValue);
            case 4:
                return sharedPreferences.getBoolean(key, (Boolean) defaultValue);
            case 5:
                return sharedPreferences.getLong(key, (Long) defaultValue);
            default:
                return "";
        }
    }

    public static void removeSharedPreferences(String key, Context context){
        SharedPreferences sharedPreferences = initSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    public static SharedPreferences initSharedPreferences(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("USERDATA", Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public static void openProgressDialog(String text, Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(text);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void closeProgressDialog(){
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    public static void openModalMap(Context context, LatLng coordinates) throws InterruptedException {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        View viewDialog = View.inflate(context, R.layout.custom_dialog_map, null);
        mBuilder.setView(viewDialog);
        AlertDialog dialog = mBuilder.create();
        dialog.setCanceledOnTouchOutside(false);

        final MapView mapView = viewDialog.findViewById(R.id.mapLatLon);
        final Button btnPoint = viewDialog.findViewById(R.id.usePoint);

        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                mMap = googleMap;
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        Log.d("map", "CLicou");
                        if(markerLocale != null){
                            markerLocale.remove();
                        }
                        double lat = latLng.latitude;
                        double lon = latLng.longitude;
                        markerLocale = mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .draggable(true)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.person_pin))
                        );

                    }
                });

                if(coordinates != null){
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 13.0f));
                }
            }
        });

        btnPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(markerLocale != null){
                    Helper.setSharedPreferences("LAT_END", String.valueOf(markerLocale.getPosition().latitude), 2, context);
                    Helper.setSharedPreferences("LON_END", String.valueOf(markerLocale.getPosition().longitude), 2, context);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    public static LatLng getAddressFromLocationName(String locationName, Context c){
        Geocoder coder = new Geocoder(c);
        List<Address> addresses;
        LatLng p1 = null;

        try {
            addresses = coder.getFromLocationName(locationName, 1);
            if(addresses == null){
                return null;
            }
            Address location = addresses.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        }catch (Exception e){
            e.printStackTrace();
        }
        return p1;
    }

    public static LatLng getAddressForLocationName(String locationName, Context context){
        Geocoder coder = new Geocoder(context);
        List<Address> addresses;
        LatLng coordinates = null;
        try {
            addresses = coder.getFromLocationName("Pernambuco, Cajueiro seco", 5);
            if(addresses != null){
                Address location = addresses.get(0);
                coordinates = new LatLng(location.getLatitude(), location.getLongitude());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return coordinates;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

}
