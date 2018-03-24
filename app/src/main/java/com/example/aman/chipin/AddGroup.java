package com.example.aman.chipin;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddGroup extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EditText groupName,numOfMembers;
    private Button confirm;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsrRef, mDatabaseGrpRef;
    private FirebaseDatabase mDatabase;
    private static String finalUID;
    private String tempUID = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        /*        setContentView(R.layout.activity_add_group_with_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

*/
        groupName = (EditText) findViewById(R.id.gnameEt);
        numOfMembers = (EditText) findViewById (R.id.numEt);
        confirm = (Button) findViewById(R.id.confirm_btn);

        mDatabaseUsrRef= FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseGrpRef = FirebaseDatabase.getInstance().getReference().child("Groups");


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int num = Integer.parseInt(numOfMembers.getText().toString());
                // Dynamically creates the various text fields for the entry of the participants name
                final LinearLayout ll = (LinearLayout) findViewById(R.id.groupLL);
                Display display = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                int width = display.getWidth() / 3;
                final LinearLayout l;

                l = new LinearLayout(AddGroup.this);
                l.setOrientation(LinearLayout.VERTICAL);
                l.removeAllViewsInLayout();
                EditText ed;
                final List<EditText> allEds = new ArrayList<EditText>();

                for (int i = 0; i < num; i++) { // Generation of the various edit text bars
                    ed = new EditText(AddGroup.this);
                    ed.setHint("Username of participant "+ (i+1));
                    allEds.add(ed);
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams(width, WindowManager.LayoutParams.WRAP_CONTENT);
                    l.addView(ed,lp);
                }
                Button bt = new Button(AddGroup.this);
                bt.setText("Confirm Participants");
                final String[] strings = new String[(allEds.size())];

                l.addView(bt);
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for(int i=0; i < allEds.size(); i++){
                            strings[i] = allEds.get(i).getText().toString();
                        }

                        showToast("Button Clicked");
                        String gName = groupName.getText().toString(); // Gets the group name from the text field
                        String numP = numOfMembers.getText().toString(); // Gets the number of members in the group
                        int numM = Integer.parseInt(numP); // Returns int value of the number of participants
                        final DatabaseReference newGroup = mDatabaseGrpRef.push(); // Pushes the group into the database with a unique id
                        newGroup.child("Name").setValue(gName);
                        newGroup.child("Number of Members").setValue(numP);
                        for(int i = 0; i < numM; i++){
                            final String uName = strings[i];

                            // Receiving the UID of the user from the given name

                            mDatabaseUsrRef.orderByChild("name").equalTo(uName).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    if(dataSnapshot.getKey() != null) {

                                        newGroup.child("Users").child(dataSnapshot.getKey()).setValue(uName);
                                        Intent intent = new Intent(AddGroup.this, Home.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                        showToast("No user entry since UID = " + finalUID);
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    finalUID = dataSnapshot.getKey();




                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                    finalUID = dataSnapshot.getKey();



                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                    finalUID = dataSnapshot.getKey();



                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }

                            });
                        }

                    }
                });

                ll.addView(l);


            }
        });
    }


    public  void showToast(String message)
    {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_group) {

        } else if (id == R.id.nav_transaction) {
            Intent addgroupIntent = new Intent(AddGroup.this,AddTransactionWithNav.class);
            startActivity(addgroupIntent);
            finish();
        } else if (id == R.id.nav_settings) {
            Intent settingIntent = new Intent(AddGroup.this,SettingsActivity.class);
            startActivity(settingIntent);
            finish();
        } else if (id == R.id.nav_logout) {
            Intent logoutIntent = new Intent(AddGroup.this,Login.class);
            startActivity(logoutIntent);
            finish();
        } else if (id == R.id.nav_home) {
            Intent homeIntent = new Intent(AddGroup.this,Home.class);
            startActivity(homeIntent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}

