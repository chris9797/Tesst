package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.NavBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonRegister;
    private EditText editTextName;
    private EditText editTextAge;
    private EditText editTextEmail;
    private EditText   editTextPassword;
    private EditText editTextConfirmPassword;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private TextView selectdocpat;
    private TextView textViewSignin;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() !=null){
            //profile activity here
            finish();
            String u_id = firebaseAuth.getCurrentUser().getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(u_id).child("Type");;
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String childType = String.valueOf(dataSnapshot.getValue());
                    if(childType.equals("Patient")){
                        startActivity(new Intent(getApplicationContext(), NavBar.class));
                    }
                    else{
                        startActivity(new Intent(getApplicationContext(), doc_nav_bar.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        progressDialog = new ProgressDialog(this);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        editTextName = (EditText) findViewById(R.id.editTextName);

        editTextAge = (EditText) findViewById(R.id.editTextAge);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);

        radioGroup = (RadioGroup) findViewById(R.id.id_radioGroup);

        radioButton = (RadioButton) findViewById(R.id.id_buttonDoc);

        //patbtn = (RadioButton) findViewById(R.id.id_buttonPat);


        textViewSignin = (TextView) findViewById(R.id.textViewSignin);

        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmpassword = editTextConfirmPassword.getText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }


        if (TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            //stopping execution from going further
            return;
        }

        if (!password.equals(confirmpassword)){
            Toast.makeText(this,"Passwords Do Not match", Toast.LENGTH_SHORT).show();
            return;
        }

        //iif validation ok
        //show progress bar

        progressDialog.setMessage("Registering user...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //user is successfully registered and logged in
                            //we will start profile acticity here
                            //only toast
                            if (firebaseAuth.getCurrentUser() != null) {

                                String u_id = firebaseAuth.getCurrentUser().getUid();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(u_id).child("Type");;
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String childType = String.valueOf(dataSnapshot.getValue());
                                        if(childType.equals("Patient")){
                                            finish();
                                            startActivity(new Intent(getApplicationContext(), NavBar.class));
                                        }
                                        else{
                                            startActivity(new Intent(getApplicationContext(), doc_nav_bar.class));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                Toast.makeText(MainActivity.this, "Registeration Failed", Toast.LENGTH_SHORT).show();
                            }

                            String user_id = firebaseAuth.getCurrentUser().getUid();
                            DatabaseReference currrent_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);


                            String name = editTextName.getText().toString().trim();
                            String age = editTextAge.getText().toString().trim();
                            int btnid = radioGroup.getCheckedRadioButtonId();

                            radioButton = findViewById(btnid);

                            String type = radioButton.getText().toString().trim();

                            Map newPost = new HashMap();
                            newPost.put("Name", name);
                            newPost.put("Age", age);
                            newPost.put("Type", type);

                            currrent_user_db.setValue(newPost);
                            }
                            progressDialog.dismiss();
                        }
                    });

    }

    @Override
    public void onClick(View view) {
        if(view == buttonRegister) {
            registerUser();

        }

        if(view == textViewSignin) {
            //will open login activity
            startActivity(new Intent(this,LoginActivity.class));
        }

    }
}
