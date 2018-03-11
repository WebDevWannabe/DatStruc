package com.softeng.chelp.chelp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.softeng.chelp.chelp.R;
import com.softeng.chelp.chelp.model.Emergency;
import com.softeng.chelp.chelp.model.UserLocation;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UserHomeActivity extends AppCompatActivity {

    TextView textName, textEmail;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference emergencyDatabase;
    private Button medic, police, firefighter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        firebaseAuth = FirebaseAuth.getInstance();
        emergencyDatabase = FirebaseDatabase.getInstance().getReference("emergencies");

        medic = findViewById(R.id.btnMedic);
        police = findViewById(R.id.btnPolice);
        firefighter = findViewById(R.id.btnFirefighter);

        //Not included
        textName = findViewById(R.id.textViewName);
        textEmail = findViewById(R.id.textViewEmail);


        FirebaseUser user = firebaseAuth.getCurrentUser();

        String userName = user.getDisplayName();
        int indexOfSpace = userName.indexOf(' ');

        textName.setText(userName.substring(0, indexOfSpace));

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        String time = timeFormat.format(calendar.getTime());
        String date = dateFormat.format(calendar.getTime());

        textEmail.setText(time + " "+ date);
        //Up to here

        onEmergencyClick(medic, "Medic");
        onEmergencyClick(police, "Police");
        onEmergencyClick(firefighter, "Firefighter");
    }

    private void onEmergencyClick(Button button, final String responseTeamType) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                callForHelp(responseTeamType);

                //TODO: Progressbar, saying 'Please wait while being assigned to a response team'
                //TODO: Change intent
            }
        });
    }

    private void callForHelp(String responseTeamTypeTemp) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");

        String displayName = user.getDisplayName();
        int indexOfSpace = displayName.indexOf(' ');

        String dateReported = dateFormat.format(calendar.getTime());
        String name = displayName.substring(0, indexOfSpace);
        String responseTeam = "Not yet assigned";
        String responseTeamType = responseTeamTypeTemp;
        String status = "On Queue";
        String timeReported = timeFormat.format(calendar.getTime());

        //TODO: get user's current location
        double latitude = 14.6639;
        double longitude = 121.0575;

        String uniqueKey = emergencyDatabase.push().getKey();

        Emergency emergency = new Emergency(dateReported, name, responseTeam, responseTeamType, status, timeReported);
        UserLocation userLocation = new UserLocation(latitude, longitude);

        emergencyDatabase.child(uniqueKey).setValue(emergency);
        emergencyDatabase.child(uniqueKey).child("userLocation").setValue(userLocation);

        Toast.makeText(UserHomeActivity.this, "Request for Help sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //if the user is not logged in
        //opening the login activity
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
