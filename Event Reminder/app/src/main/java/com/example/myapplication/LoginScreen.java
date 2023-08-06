package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginScreen extends AppCompatActivity {
private EditText login_email,login_Password;
private Button btn_login;

private FirebaseAuth firebaseAuth;
private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        login_email=findViewById(R.id.login_email);
        login_Password=findViewById(R.id.login_Password);
        btn_login = findViewById(R.id.btn_login);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Please Wait.....");
        progressDialog.setCanceledOnTouchOutside(false);

        TextView forgetpass= (TextView) findViewById(R.id.forgetpassword);
        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getApplicationContext(),ForgetPass.class);
                startActivity(i);
            }
        });

        TextView register= (TextView)findViewById(R.id.Notamember);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(getApplicationContext(),user_register.class);
                startActivity(a);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent o = new Intent(getApplicationContext(), event_choice.class);
                startActivity(o); loginuser();
            }
        });
    }
String email, Password;
    private void loginuser() {
        email= login_email.getText().toString();
        Password = login_Password.getText().toString();

        progressDialog.setMessage("Logging In");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,Password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginScreen.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void makeOnline() {
        progressDialog.setMessage("Checking User .....");

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("online","true");

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginScreen.this,""+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

//    private void checkUserType() {
//        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
//        ref.orderByChild("id").equalTo(firebaseAuth.getUid())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                        for(DataSnapshot ds: snapshot.getChildren())
//                        {
//
//                            String accountType= ""+ds.child("Account_Type").getValue();
//                            if(accountType.equals("Seller_Account"))
//                            {
//                                progressDialog.dismiss();
//                                startActivity(new Intent(LoginScreen.this, event_list.class));
//                                finish();
//                            }
//                            else
//                            {
//                                progressDialog.dismiss();
//                                startActivity(new Intent(LoginScreen.this, create_event.class));
//                                finish();
//                            }
//                        }
//                    }

//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }
}