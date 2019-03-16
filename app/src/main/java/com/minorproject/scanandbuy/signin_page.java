package com.minorproject.scanandbuy;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signin_page extends AppCompatActivity {

    private static final String TAG = "signin_page";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private TextInputEditText mEmail, mPassword;
    private TextInputLayout mEmail_layout,mPassword_layout;
    private TextView signup , forgotpassword;
    private Button login;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mEmail_layout = (TextInputLayout) findViewById(R.id.input_email);
        mPassword_layout = (TextInputLayout) findViewById(R.id.input_password);
        mEmail = (TextInputEditText) findViewById(R.id.inputemail);
        mPassword = (TextInputEditText) findViewById(R.id.inputpassword);
        signup = (TextView) findViewById(R.id.signup);
        login = (Button) findViewById(R.id.signin);
        forgotpassword = (TextView) findViewById(R.id.forgot_password);
        mProgressBar = (ProgressBar) findViewById(R.id.signin_progressbar);

        ViewPager viewPagersignin = (ViewPager) findViewById(R.id.viewPagersignin);
        CustomAdapter adapter = new CustomAdapter(getSupportFragmentManager());
        viewPagersignin.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();


        if (servicesOK()) {
            checkemail();
        }

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signin_page.this, Signup_activity.class);
                startActivity(intent);
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetpasswordlink();
                mPassword.setText("");
            }
        });
    }

    private void checkemail(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateEmail() && validatePassword())
                {

                    showDialog();
                    mAuth.signInWithEmailAndPassword(mEmail.getText().toString(),mPassword.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    hideDialog();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Snackbar.make(findViewById(android.R.id.content),"Login is successful", Snackbar.LENGTH_SHORT).show();
                                    Intent intent = new Intent(signin_page.this , Main2Activity.class);
                                    startActivity(intent);
                                    //finish();
                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(findViewById(android.R.id.content),"Authentication Failed", Snackbar.LENGTH_SHORT).show();
                            hideDialog();
                        }
                    });
                }
            }
        });

    }

    private void resetpasswordlink(){
        if (TextUtils.isEmpty(mEmail.getText().toString())){
            Snackbar.make(findViewById(android.R.id.content) , "Please enter the email.", Snackbar.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mEmail.getText().toString()).matches())
        {
            mEmail_layout.setError("Invalid Email");
        }
        else {
            showDialog();
            mEmail_layout.setErrorEnabled(false);
            FirebaseAuth.getInstance().sendPasswordResetEmail(mEmail.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: Password Reset Email sent.");
                                Toast.makeText(signin_page.this, "Sent Password Reset Link to Email",
                                        Toast.LENGTH_SHORT).show();
                                hideDialog();
                            } else {
                                Log.d(TAG, "onComplete: No user associated with that email.");

                                hideDialog();
                                Toast.makeText(signin_page.this, "No User Associated with that Email.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }

    private boolean validateEmail(){
        if (TextUtils.isEmpty(mEmail.getText().toString())){
            mEmail_layout.setError("Cannot be blank");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(mEmail.getText().toString()).matches())
        {
            mEmail_layout.setErrorEnabled(false);
            mEmail_layout.setError("Invalid Email");
            return false;
        }
        else
        {
            mEmail_layout.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validatePassword(){
        if (TextUtils.isEmpty(mPassword.getText().toString())){
            mPassword_layout.setError("Cannot be blank");
            return false;
        }
        else{
            mPassword_layout.setErrorEnabled(false);
            return true;
        }
    }

    public boolean servicesOK(){
        Log.d(TAG, "servicesOK: Checking Google Services.");

        int isAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(signin_page.this);

        if(isAvailable == ConnectionResult.SUCCESS){
            //everything is ok and the user can make mapping requests
            Log.d(TAG, "servicesOK: Play Services is OK");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(isAvailable)){
            //an error occured, but it's resolvable
            Log.d(TAG, "servicesOK: an error occured, but it's resolvable.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(signin_page.this, isAvailable, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this, "Can't connect to mapping services", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private void hideDialog(){
        if(mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
    private void showDialog(){
        mProgressBar.setVisibility(View.VISIBLE);

    }
    @Override
    public void onBackPressed() {
        Intent i=new Intent(signin_page.this,Login_Activity.class);
        startActivity(i);
        super.onBackPressed();
    }

}

