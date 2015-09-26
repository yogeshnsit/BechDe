package com.olx.bechde;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,LocationListener,GoogleApiClient.OnConnectionFailedListener {

    public static final int REQUEST_CODE_PICK_IMAGE_FROM_GALLERY=1;
    private static final String TAG="MainActivity";
    ArrayList<String> productTypes;
    SharedPreferences myPrefs;

    TextView tvHeader,tvLocation,save;
    EditText EtDetails;
    ImageView ivImage;
    GoogleApiClient client;
    Spinner spinner;
    Button uploadButton;
    String userProductType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvHeader=(TextView)findViewById(R.id.header);
        tvLocation=(TextView)findViewById(R.id.tvLocation);
        EtDetails=(EditText)findViewById(R.id.EtDetails);
        ivImage=(ImageView)findViewById(R.id.ivImage);
        productTypes=new ArrayList<String>();
        productTypes.add("bicycle");
        productTypes.add("Shoes");
        productTypes.add("Others");
        client=new GoogleApiClient.Builder(this).
                addConnectionCallbacks(this).addOnConnectionFailedListener(this).
                addApi(LocationServices.API).build();

        final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.action_bar_ad,
                null);
        save = (TextView) actionBarLayout.findViewById(R.id.action_bar_save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myPrefs=MainActivity.this.getSharedPreferences("user_details",MODE_PRIVATE);
                Boolean isdetailsSet=myPrefs.getBoolean("IsDetailsSet",false);
                if(isdetailsSet!=true)
                {
                    Intent details=new Intent(MainActivity.this,DetailsActivity.class);
                    startActivityForResult(details,2);

                }
                else{
                    String name=myPrefs.getString("Name",null);
                    String phone=myPrefs.getString("Phone",null);
                    String email=myPrefs.getString("email",null);
                    // post ad on the server with details using Async task
                }

            }
        });


        spinner= (Spinner)findViewById(R.id.locationSpinner);

        String[] spinnerArray={"Delhi","Mumbai","Kolkata","Chennai"};

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary_dark_material_light)));
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(actionBarLayout);

        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , REQUEST_CODE_PICK_IMAGE_FROM_GALLERY);
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_PICK_IMAGE_FROM_GALLERY:
                if(resultCode== Activity.RESULT_OK)
                {
                    //Upload the image to server nad get some TAG using the reverse image search
                    Uri selectedImage = data.getData();
                    ivImage.setImageURI(selectedImage);

                    String productType=getTpye(selectedImage);

                    if(productTypes.contains(productType)){
                        //set Product type as product type in details
                        userProductType=productType;
                    }
                    else
                    {
                        userProductType="others";
                    }




                    // Fetch location using google service


                    client.connect();

                    // Do rest of the work for lcoation in onConnected callback



                    //Fetch data using Google reverse engine
                }
                else
                {
                    // Do nothing

                }
                break;

            case 2:
                String email=data.getStringExtra("Email");
                String phone=data.getStringExtra("Phone");
                String Name=data.getStringExtra("Name");

                //put data in shared prefs for next use
                SharedPreferences.Editor editor = getSharedPreferences("user_details", MODE_PRIVATE).edit();
                editor.putString("Name", Name);
                editor.putString("email", email);
                editor.putString("Phone",phone);
                editor.commit();

        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        String lat,longi;
        Location location = LocationServices.FusedLocationApi.getLastLocation(client);
        if (location != null) {
            lat = (String.valueOf(location.getLatitude()));
            longi = (String.valueOf(location.getLongitude()));
            Log.d(TAG,lat+":"+longi);
        }


        try {
            Toast.makeText(MainActivity.this, "Location is " + location.getLatitude() + ":" + location.getLongitude(), Toast.LENGTH_SHORT).show();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            System.out.println(addresses.get(0).getLocality());
            Log.d(TAG, addresses.get(0).getLocality());
        }





    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        client.connect();

    }

    public String getTpye(Uri uri){
        //upload the image to sever and get the type of product
        String type="bicycle";
        return type;
    }
}
