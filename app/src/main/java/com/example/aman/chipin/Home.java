package com.example.aman.chipin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser mUser;
    private SharedPreferences pref;
    private Context _context;
    private SharedPreferences.Editor editor;
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private FirebaseAuth.AuthStateListener mAuthListener;

    private TextView mWelcome;

    // Shared pref mode
    int PRIVATE_MODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                if(mAuth.getCurrentUser() == null){
                    startActivity(new Intent(Home.this, Login.class));
                }
            }
        };
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());

        mWelcome=   (TextView) findViewById(R.id.tvWelcome);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mWelcome.setText("Welcome  "+dataSnapshot.child("name").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    public void createLoginSession(String name, String email) {

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public void checkLogin() {

        if (!this.isLoggedIn()) {

            Intent i = new Intent(_context, Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public boolean isLoggedIn() {

        return pref.getBoolean(IS_LOGIN, false);
    }
    private void checkUserExist() {

        final String user_id = mAuth.getCurrentUser().getUid();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(user_id)) {

                    Intent mainIntent = new Intent(Home.this, Login.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                    //Toast.makeText(MainActivity.this, "Logged in", Toast.LENGTH_LONG).show();
                    finish();


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                showToast("Settings Clicked");
                return true;
            case R.id.action_logout:
                mAuth.signOut();
               // Intent intent = new Intent(Home.this, Login.class);
               //startActivity(intent);
               // finish();
               // return true;
            case R.id.action_add_contact:
                showToast("Add Contact Clicked");
                return true;
            case R.id.action_add:
                startActivity(new Intent(Home.this,AddTransaction.class));
                finish();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }
    public void showToast(String message)
    {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
