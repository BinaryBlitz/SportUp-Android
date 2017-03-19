package ru.binaryblitz.sportup.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ru.binaryblitz.sportup.R;
import ru.binaryblitz.sportup.base.BaseActivity;
import ru.binaryblitz.sportup.custom.RecyclerListView;
import ru.binaryblitz.sportup.utils.LogUtil;

public class SelectCityActivity extends BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private CitiesAdapter adapter;
    private SwipeRefreshLayout layout;
    private static final int LOCATION_PERMISSION = 2;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initList();


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                layout.setEnabled(true);
                load();
            }
        });

        setOnClickListeners();
    }

    @Override
    public void onRefresh() {
        load();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, LOCATION_PERMISSION);
                } else {
                    getLocation();
                }
            } catch (Exception ignored) {
                layout.setRefreshing(false);
            }
        } else {
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    onLocationError();
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        layout.setRefreshing(false);
        onLocationError();
    }

    private void setOnClickListeners() {
        findViewById(R.id.my_loc_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });
    }

    private void initList() {
        RecyclerListView view = (RecyclerListView) findViewById(R.id.recyclerView);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.setItemAnimator(new DefaultItemAnimator());
        view.setHasFixedSize(true);

        adapter = new CitiesAdapter(this);
        view.setAdapter(adapter);

        layout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        layout.setOnRefreshListener(this);
        layout.setColorSchemeResources(R.color.colorAccent);
    }

    private void load(double latitude, double longitude) {
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                adapter.selectCity(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
            } else {
                onLocationError();
            }
        } catch (IOException e) {
            LogUtil.logException(e);
        }
    }

    private void getLocation() {
        layout.setRefreshing(false);
        if (mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                onLocationError();
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                load(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            } else {
                onLocationError();
            }
        } else {
            mGoogleApiClient.connect();
        }
    }
}
