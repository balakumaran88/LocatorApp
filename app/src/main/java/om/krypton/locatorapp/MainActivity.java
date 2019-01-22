package om.krypton.locatorapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    public static final String TAG = "LOTE";
    private static final int REQ_CODE = 10000;

    private GoogleApiClient mGoogleApiClient;
    // private Location mLocation;
   // private TextView txtLocation;

    EditText edtAddress;
    EditText edtMilesPerHour;
    EditText edtMetersPerMile;
    TextView txtDistance;
    TextView txtTime;
    Button btnGetData;

    private String destinationLocationAddress = "";
    private TaxiManager mTaxiManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtAddress = findViewById(R.id.editAddress);
        edtMilesPerHour = findViewById(R.id.editMph);
        edtMetersPerMile = findViewById(R.id.editMetersPMile);

        txtDistance = findViewById(R.id.txtDistanceValue);
        txtTime = findViewById(R.id.txtTime);
        btnGetData = findViewById(R.id.btnSubmit);

        mTaxiManager = new TaxiManager();



        //txtLocation = findViewById(R.id.txtLocation);
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addConnectionCallbacks(MainActivity.this)
                .addOnConnectionFailedListener(MainActivity.this)
                .addApi(LocationServices.API).build();
    }

    @Override
    public void onClick(View v) {
        String addressValue = edtAddress.getText().toString();
        boolean isGeoCoding = true;
        Log.d(TAG,"submiited data");
        if(!addressValue.equals(destinationLocationAddress)){
            destinationLocationAddress = addressValue;
            Log.d(TAG,"the address is not same");

            try{
                Geocoder geocoder = new Geocoder(MainActivity.this);
                List<Address> myAddresses = geocoder.getFromLocationName(destinationLocationAddress,4);
                if (myAddresses != null) {
                    double lat = myAddresses.get(0).getLatitude();
                    double lon = myAddresses.get(0).getLongitude();
                    Log.d(TAG,"lat is "+ lat);
                    Location locationAddress = new Location("MyDestination");
                    locationAddress.setLatitude(lat);
                    locationAddress.setLongitude(lon);
                    mTaxiManager.setDestinationLocation(locationAddress);

                }
            }catch(Exception e){
                Toast.makeText(MainActivity.this,"Geo coding error",Toast.LENGTH_SHORT).show();
                isGeoCoding = false;
                Log.d(TAG,e.getStackTrace().toString());
            }
        }

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
                FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
                Location userCurrentLocation = fusedLocationProviderApi.getLastLocation(mGoogleApiClient);
                if( userCurrentLocation != null && isGeoCoding){
                    txtDistance.setText(mTaxiManager.returnTheMilesBewtweenCurrentLocationAndDestinationLocation(userCurrentLocation,
                            Integer.parseInt(edtMetersPerMile.getText().toString())));
                    txtTime.setText(
                            mTaxiManager.returnTheTimeLeftToGetToDestinationLocation(
                                    userCurrentLocation,
                                    Float.parseFloat(edtMilesPerHour.getText().toString()),
                                    Integer.parseInt(edtMetersPerMile.getText().toString()))
                        );
                }

        } else {
            txtDistance.setText("Permission needted to access location");
            ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG,"connected to location api");
        //showUserLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"Connection is suspended!");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG,"Connection is failed!");
        try {
            if(connectionResult.hasResolution()){
                connectionResult.startResolutionForResult(MainActivity.this,REQ_CODE);
            } else {
                Toast.makeText(MainActivity.this,"Google play services is not working", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch(Exception e){
            Log.d(TAG,e.getStackTrace().toString());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE && requestCode == RESULT_OK){
            mGoogleApiClient.connect();
        }
    }

    protected void onStart(){
        super.onStart();

        if(mGoogleApiClient!= null){
            mGoogleApiClient.connect();
        }
    }

    // custom methods

   /* private void showUserLocation(){
        // run time permission
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
            mLocation = fusedLocationProviderApi.getLastLocation(mGoogleApiClient);

            if(mLocation != null){
                double lat = mLocation.getLatitude();
                double lon = mLocation.getLongitude();

                txtLocation.setText(lat+ " , "+ lon);
            } else {
                txtLocation.setText("Location cannot be accessed!");
            }

        } else {
            txtLocation.setText("Permission to loaction is denied. Try again Later!");
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
    }*/
}
