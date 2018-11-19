package task.gooded.goodedtask.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import task.gooded.goodedtask.Adapters.PlacesAdapter;
import task.gooded.goodedtask.Location.LocationFused;
import task.gooded.goodedtask.Network.ApiClient;
import task.gooded.goodedtask.Place;
import task.gooded.goodedtask.R;
import task.gooded.goodedtask.Utils.Constants;
import task.gooded.goodedtask.Utils.Loader;
import task.gooded.goodedtask.storage.MySharedPreferences;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    SupportMapFragment mapFragment;
    MySharedPreferences sp;
    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    GoogleMap googleMap;

    ArrayList<Place> list=new ArrayList<>();

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.drag_me)
    ImageView drag_me;
    PlacesAdapter adapter;
    @BindView(R.id.card )
    CardView card;
    BottomSheetBehavior bottomSheetBehavior;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sp = MySharedPreferences.getInstance(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setTitle("Places Near You..");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new PlacesAdapter(this,list);
        recyclerView.setAdapter(adapter);


        bottomSheetBehavior = BottomSheetBehavior.from(card);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheetBehavior.setHideable(false);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState==BottomSheetBehavior.STATE_COLLAPSED)
                {
                    drag_me.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.ic_keyboard_arrow_up_black_24dp));
                }
                else
                {
                    drag_me.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.ic_keyboard_arrow_down_black_24dp));

                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        drag_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_COLLAPSED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                else
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else
            super.onBackPressed();

    }

    @Override
    public void onMapReady(GoogleMap googleMap1) {

        this.googleMap=googleMap1;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map));

            if (!success) {
            }
        } catch (Resources.NotFoundException e) {
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Place place=list.get(Integer.parseInt(marker.getTag().toString()));
                Log.i("", "onMarkerClick: "+place.getName());
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                recyclerView.scrollToPosition(Integer.parseInt(marker.getTag().toString()));
                return false;
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        if (googleMap!=null)
        googleMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);

        getList(location.getLatitude()+","+ location.getLongitude());


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void getList(String ll)
    {
        ApiClient.getApiClient(this).getList(ll,Constants.CLIENT_ID,Constants.CLIENT_SECRET,"20181119","50000")
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {

                            assert response.body() != null;
                            JSONObject object=new JSONObject(response.body().string());
                            Log.d("", "onResponse: "+object.toString());

                            JSONArray array=object.getJSONObject("response").getJSONArray("groups").getJSONObject(0)
                                    .getJSONArray("items");

                            for (int i = 0; i <array.length() ; i++) {
                                JSONObject placeobject=array.getJSONObject(i);
                                Place place=new Place();
                                place.setId(placeobject.getJSONObject("venue").getString("id"));
                                place.setName(placeobject.getJSONObject("venue").getString("name"));
                                place.setLat(placeobject.getJSONObject("venue").getJSONObject("location").getString("lat"));
                                place.setLng(placeobject.getJSONObject("venue").getJSONObject("location").getString("lng"));
                                place.setCategory(placeobject.getJSONObject("venue").getJSONArray("categories").getJSONObject(0)
                                        .getString("name"));
                                place.setimage(placeobject.getJSONObject("venue").getJSONArray("categories").getJSONObject(0)
                                        .getJSONObject("icon").getString("prefix")+"44"+placeobject.getJSONObject("venue").getJSONArray("categories").getJSONObject(0)
                                        .getJSONObject("icon").getString("suffix"));
                                list.add(place);
                                googleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(Double.parseDouble(list.get(i).getLat()), Double.parseDouble(list.get(i).getLng())))
                                            .title(list.get(i).getName())).setTag(i);

                            }

                            adapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }


}
