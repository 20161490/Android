package com.example.safefamily;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.safefamily.fragment.AccountFragment;
import com.example.safefamily.fragment.ChatFragment;
import com.example.safefamily.fragment.DangerFragment;
import com.example.safefamily.fragment.PeopleFragment;
import com.example.safefamily.R;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.inactivity_bottomnavigationview);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_people:

                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new PeopleFragment()).commit();

                        return true;

                    case R.id.action_chat:
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new ChatFragment()).commit();
                        return true;

                    case R.id.action_account:
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new AccountFragment()).commit();
                        return true;

                    case R.id.action_danger:
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout,new DangerFragment()).commit();
                        return true;


                }
                return false;
            }
        });
        passPushTokenToServer();
    }
    void passPushTokenToServer(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Task<String> token = FirebaseMessaging.getInstance().getToken();
        Map<String,Object> map = new HashMap<>();
        map.put("pushToken",token);
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
    }




}