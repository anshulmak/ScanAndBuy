package com.minorproject.scanandbuy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.minorproject.scanandbuy.Models.User;

public class Signup_activity extends AppCompatActivity {

    private TextInputEditText mEmail, mPassword;
    private TextInputLayout mEmail_layout,mPassword_layout;
    private TextInputEditText mName , mConfirmPassword;
    private TextInputLayout mName_layout , mConfirmPassword_layout;
    private Button signup_button;

    private FirebaseAuth mAuth;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_activity);

        mEmail_layout = (TextInputLayout) findViewById(R.id.input_email_signup);
        mPassword_layout = (TextInputLayout) findViewById(R.id.input_password_signup);
        mEmail = (TextInputEditText) findViewById(R.id.inputemailsignup);
        mPassword = (TextInputEditText) findViewById(R.id.inputpasswordsignup);
        mName_layout = (TextInputLayout) findViewById(R.id.input_name_signup);
        mConfirmPassword_layout = (TextInputLayout) findViewById(R.id.input_confirmpassword_signup);
        mName = (TextInputEditText) findViewById(R.id.inputnamesignup);
        mConfirmPassword = (TextInputEditText) findViewById(R.id.inputconfirmpasswordsignup);
        signup_button = (Button) findViewById(R.id.signup_Button);
        mProgressBar = (ProgressBar) findViewById(R.id.signup_progressbar);

        ViewPager viewPagersignup = (ViewPager) findViewById(R.id.viewPagersignup);
        CustomAdapter adapter = new CustomAdapter(getSupportFragmentManager());
        viewPagersignup.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateName() && validateEmail()&&validatePassword()&&validateConfirmpassword()){

                    if (dopasswordmatch(mPassword.getText().toString() , mConfirmPassword.getText().toString())){
                        showDialog();
                        registerNewEmail(mEmail.getText().toString(),mPassword.getText().toString());
                    }

                }
            }
        });
        }

    public void registerNewEmail(final String email, String password){


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            hideDialog();
                            Snackbar.make(findViewById(android.R.id.content), "Sign up is successful", Snackbar.LENGTH_SHORT).show();

                            User user = new User();
                            user.setName(mName.getText().toString());
                            user.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            FirebaseDatabase.getInstance().getReference()
                                    .child("users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Intent intent = new Intent(Signup_activity.this , Main2Activity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                        }
                        if (!task.isSuccessful()) {
                            hideDialog();
                            Snackbar.make(findViewById(android.R.id.content), "Sign up is unsuccessful", Snackbar.LENGTH_SHORT).show();
                        }


                        // ...
                    }
                });
        hideSoftKeyboard();
    }

    private boolean validateName() {
        if (TextUtils.isEmpty(mName.getText().toString())) {
            mName_layout.setError("Cannot be blank");
            return false;
        } else {
            mName_layout.setErrorEnabled(false);
            return true;
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
        else if (mPassword.getText().toString().length()<8 ){
            mPassword_layout.setErrorEnabled(false);
            mPassword_layout.setError("Password must be of 8 characters or more");
            return false;
        }else{
            mPassword_layout.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validateConfirmpassword(){
        if (TextUtils.isEmpty(mConfirmPassword.getText().toString())){
            mConfirmPassword_layout.setError("Cannot be blank");
            return false;
        }
        else{
            mConfirmPassword_layout.setErrorEnabled(false);
            return true;
        }
    }
    private boolean dopasswordmatch(String password,String cpassword){
        if (!password.equals(cpassword)){
            mConfirmPassword_layout.setError("Password do not match");
            return  false;
        }else {
            mConfirmPassword_layout.setErrorEnabled(false);
            return true;
        }
    }
    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    private void hideDialog(){
        if(mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
    private void showDialog(){
        mProgressBar.setVisibility(View.VISIBLE);

    }
}
