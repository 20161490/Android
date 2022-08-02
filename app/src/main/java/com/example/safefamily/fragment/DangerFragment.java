package com.example.safefamily.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.safefamily.GpsFind;
import com.example.safefamily.Join;
import com.example.safefamily.Login;
import com.example.safefamily.MapActiviy;
import com.example.safefamily.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class DangerFragment extends Fragment {
    SmsManager send;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_danger,container,false);
                Button messagesendbutton  = view.findViewById(R.id.Messagesendbutton);
                Button buttongps = view.findViewById(R.id.gpsbutton);

        messagesendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GpsFind.class);
                startActivity(intent);
            }
        });

        buttongps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapActiviy.class);
                startActivity(intent);
            }
        });
        return view;
    }


}
