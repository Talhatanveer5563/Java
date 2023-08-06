package com.example.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class user_register extends AppCompatActivity implements LocationListener {
    private TextView back_btn;
    private ImageButton get_location;
    private CircleImageView Profile_image;
    private EditText user_registeration_name, user_registeration_email, user_registeration_password,
            user_registeration_cpassword, Country, State, City, Postal_code,
            user_registeration_address, Birth_Date, Birth_Month, Birth_Year;
    private Button user_register_register;
    //Permission Constant
    private final static int LOCATION_REQUEST_CODE = 100;
    private final static int CAMERA_REQUEST_CODE = 200;
    private final static int STORAGE_REQUEST_CODE = 300;

    //image pick constants
    private final static int IMAGE_PICK_GALLERY_CODE = 400;
    private final static int IMAGE_PICK_CAMERA_CODE = 500;

    //Permission array
    private String[] locationPermission;
    private String[] cameraPermission;
    private String[] storagePermission;

    //image pick uri
    private Uri image_uri;

    private double latitude,longitude;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        get_location = findViewById(R.id.get_location);
        back_btn = findViewById(R.id.back_btn);
        user_registeration_name = findViewById(R.id.user_registeration_name);
        user_registeration_email = findViewById(R.id.user_registeration_email);
        user_registeration_password = findViewById(R.id.user_registeration_password);
        user_registeration_cpassword = findViewById(R.id.user_registeration_cpassword);
        Country = findViewById(R.id.country);
        State = findViewById(R.id.State);
        City = findViewById(R.id.City);
        Postal_code = findViewById(R.id.Postal_code);
        user_registeration_address = findViewById(R.id.user_registeration_address);
        Birth_Date = findViewById(R.id.Birth_Date);
        Birth_Month = findViewById(R.id.Birth_Month);
        Birth_Year = findViewById(R.id.Birth_Year);
        Profile_image=findViewById(R.id.Profile_image);
        user_register_register = findViewById(R.id.user_register_register);

        locationPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait.....");
        progressDialog.setCanceledOnTouchOutside(false);
        
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        user_register_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
        Profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(user_register.this,"clicked",Toast.LENGTH_LONG).show();
                showImagePickDialog();
            }
        });

        get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()) {
                    //allowed
                    detectLocation();
                } else {
                    requestLocationPermission();
                }
            }
        });
    }
    String name,email,pass,cpass,country,state,city,postal_code,address,birth_date,birth_month,birth_year;
    private void inputData() {
        name = user_registeration_name.getText().toString();
        email = user_registeration_email.getText().toString().trim();
        pass = user_registeration_password.getText().toString();
        cpass = user_registeration_cpassword.getText().toString();
        country = Country.getText().toString();
        state = State.getText().toString();
        city = City.getText().toString();
        postal_code = Postal_code.getText().toString();
        address = user_registeration_address.getText().toString();
        birth_date = Birth_Date.getText().toString();
        birth_month = Birth_Month.getText().toString();
        birth_year = Birth_Year.getText().toString();

        if(TextUtils.isEmpty(name))
        {
            user_registeration_name.setError("Enter Name");
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            user_registeration_email.setError("Enter Valid  Email");
        }
        else if(TextUtils.isEmpty(pass) || pass.length() >7)
        {
            user_registeration_password.setError("Password not less than 8 characters");
        }
        else if(!cpass.equals(pass))
        {
            user_registeration_cpassword.setError("Password not match");
        }
        else if(longitude == 0.0 || latitude == 0.0)
        {
            Toast.makeText(this,"Click Gpd Button to get location",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(country))
        {
            Country.setError("Please Enter Country Name");
        }
        else if(TextUtils.isEmpty(city))
        {
            City.setError("Please Enter City Name");
        }
        else if(TextUtils.isEmpty(state))
        {
            State.setError("Please Enter State Name");
        }
//        else if(TextUtils.isEmpty(postal_code))
//        {
//            Postal_code.setError("Please Enter Postal Code");
//        }
        else if(TextUtils.isEmpty(address))
        {
            user_registeration_address.setError("Please Enter Address");
        }
        else if(TextUtils.isEmpty(birth_year))
        {
            Birth_Year.setError("Please Enter Birth Year");
        }
        else if(TextUtils.isEmpty(birth_month))
        {
            Birth_Month.setError("Please Enter Your Birth Month");
        }
        else if(TextUtils.isEmpty(birth_date))
        {
            Birth_Date.setError("Please Enter Your Date of Birth");
        }
        createAccount();
    }

    private void createAccount() {
        progressDialog.setMessage("Acount Creating ......");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        savedata();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(user_register.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void savedata() {
        progressDialog.setMessage("Saving Information");
        String timestamp=""+System.currentTimeMillis();
        if(image_uri == null)
        {
            HashMap<String, Object> hashMap= new HashMap<>();
            hashMap.put("id",""+firebaseAuth.getUid());
            hashMap.put("name",""+name);
            hashMap.put("Email",""+email);
            hashMap.put("password",""+pass);
            hashMap.put("country",""+country);
            hashMap.put("city",""+city);
            hashMap.put("state",""+state);
            hashMap.put("postal_code",""+postal_code);
            hashMap.put("Birth_Year",""+birth_year);
            hashMap.put("Birth_Month",""+birth_month);
            hashMap.put("Birth_Date",""+birth_date);
            hashMap.put("Account Type","Seller Account");
            hashMap.put("Longitude",""+longitude);
            hashMap.put("Latitude",""+latitude);
            hashMap.put("Profile_Image","");
            hashMap.put("online","true");
            hashMap.put("Avaiable","True");
            hashMap.put("timestamp",""+timestamp);

            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startActivity(new Intent(user_register.this,LoginScreen.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(user_register.this,"Unable to Connect"+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
        }
        else{
            String imagePath = "Profile_Images/" + firebaseAuth.getUid();

            StorageReference storageReference= FirebaseStorage.getInstance().getReference(imagePath);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            HashMap <String, Object> hashMap= new HashMap<>();
                            hashMap.put("id",""+firebaseAuth.getUid());
                            hashMap.put("name",""+name);
                            hashMap.put("Email",""+email);
                            hashMap.put("password",""+pass);
                            hashMap.put("country",""+country);
                            hashMap.put("city",""+city);
                            hashMap.put("state",""+state);
                            hashMap.put("postal_code",""+postal_code);
                            hashMap.put("Birth_Year",""+birth_year);
                            hashMap.put("Birth_Month",""+birth_month);
                            hashMap.put("Birth_Date",""+birth_date);
                            hashMap.put("Account Type","Seller Account");
                            hashMap.put("Longitude",""+longitude);
                            hashMap.put("Latitude",""+latitude);
                            hashMap.put("Profile_Image",""+downloadImageUri);
                            hashMap.put("online","true");
                            hashMap.put("Avaiable","True");
                            hashMap.put("timestamp",""+timestamp);

                            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("User");
                            reference.child(firebaseAuth.getUid()).setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startActivity(new Intent(user_register.this,LoginScreen.class));
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(user_register.this,"Unable to Connect"+e.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(user_register.this,"Unable to save Data"+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void showImagePickDialog() {
        Toast.makeText(user_register.this,"Image Picked method", Toast.LENGTH_LONG).show();
        String[] options = {"Camera","Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    if(checkcameraPermission())
                    {
                        pickfromCamera();
                    }
                    else{
                        requestCameraPermission();
                    }
                }
                else {
                    if(checkstoragePermission())
                    {
                        pickFromGallery();
                    }
                    else
                    {
                        requestStoragePermission();
                    }
                }
            }
        }).show();
    }
    private void pickFromGallery()
    {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickfromCamera()
    {
        ContentValues contentValues=new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Description");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(i,IMAGE_PICK_CAMERA_CODE);
    }
    private void requestStoragePermission()
    {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }
    private void requestCameraPermission()
    {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }
    private boolean checkstoragePermission()
    {
       boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return  result1;
    }

    private boolean checkcameraPermission()
    {
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermission, LOCATION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {
                        //permission
                        detectLocation();
                    } else {
                        Toast.makeText(this, "Location Required", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        //permission
                        pickfromCamera();
                    } else {
                        Toast.makeText(this, "Camera Permission is Required", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        //permission
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Storage Permission is  Required", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK)
        {
            if(requestCode == IMAGE_PICK_GALLERY_CODE)
            {
                image_uri = data.getData();
                Profile_image.setImageURI(image_uri);
            }
            else if(requestCode == IMAGE_PICK_CAMERA_CODE)
            {
                Profile_image.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void detectLocation() {
        Toast.makeText(this, "Please Wait .....", Toast.LENGTH_LONG).show();
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
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude= location.getLongitude();

        findaddress();
    }

    private void findaddress() {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder= new Geocoder(this, Locale.getDefault());


        try{
            addresses = geocoder.getFromLocation(latitude,longitude,1);
            String country = addresses.get(0).getCountryName();
            String city = addresses.get(0).getLocality();
            String state= addresses.get(0).getAdminArea();
            String address = addresses.get(0).getAddressLine(0);

            //set address

            Log.i("Find Country", "findaddress:"+country);
            Country.setText(country);
            City.setText(city);
            State.setText(state);
            Postal_code.setText("postalcode");
            user_registeration_address.setText(address);

        }
        catch (Exception e)
        {
            Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(this, "Enable Location", Toast.LENGTH_LONG).show();
    }
}