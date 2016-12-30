package edu.vesit.barclaysapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moxtra.sdk.MXAccountManager;
import com.moxtra.sdk.MXSDKConfig;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.etEmail) EditText mEmailView;
    @BindView(R.id.etFirstName) EditText mFirstNameView;
    @BindView(R.id.etLastName) EditText mLastNameView;
    @BindView(R.id.etPassword) EditText mPasswordView;
    @BindView(R.id.pbRegister) ProgressBar mProgressView;

    private static final String TAG = "RegisterActivity";
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mContext = RegisterActivity.this;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.e("registered ", "yes");
                    saveUser(user.getUid());
                    Toast.makeText(mContext, "Registration successful", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        };
    }

    private void saveUser(String uid) {
        User newUser = new User();
        newUser.setUid(uid);
        newUser.setEmail(mEmailView.getText().toString());
        newUser.setPassword(mPasswordView.getText().toString());
        newUser.setFirstName(mFirstNameView.getText().toString());
        newUser.setLastName(mLastNameView.getText().toString());
        newUser.setAvatarPath("NA");
        newUser.setTwitterToken(null);
        PreferenceUtil.saveUser(mContext,newUser);
        mDatabase.child("users").child(uid).setValue(newUser);
    }

    @OnClick(R.id.btnRegister)
    public void tryRegistration() {
        showProgress(true);
        String enteredEmail = mEmailView.getText().toString();
        String enteredPassword = mPasswordView.getText().toString();
        mAuth.createUserWithEmailAndPassword(enteredEmail, enteredPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()) {
                    Toast.makeText(mContext, "Error ", Toast.LENGTH_SHORT).show();
                    Log.e("registration ", "no");
                }
            }
        });
    }
    public void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }
}
