package com.example.safefamily;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.safefamily.MainActivity;
import com.example.safefamily.R;
import com.example.safefamily.fragment.PeopleFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.TimerTask;

import static com.google.firebase.remoteconfig.FirebaseRemoteConfig.TAG;

public class Login extends AppCompatActivity {

    private EditText id;
    private EditText password;

    private Button login;
    private Button signup;
    private FirebaseRemoteConfig FirebaseRemoteConfig;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

        String splash_background = FirebaseRemoteConfig.getString(getString(R.string.rc_color));
        getWindow().setStatusBarColor(Color.parseColor(splash_background));

        id = (EditText)findViewById(R.id.login_edttext_id);
        password = (EditText)findViewById(R.id.login_edttext_password);
        login = (Button) findViewById(R.id.login_button_login);
        signup = (Button) findViewById(R.id.login_button_join);
        login.setBackgroundColor(Color.parseColor(splash_background));
        signup.setBackgroundColor(Color.parseColor(splash_background));
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginEvent();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,Join.class));
            }
        });
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){ //로그인
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(Login.this,"로그인에 성공하셧습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }else{ //로그아웃

                }
//                user.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
//                    @Override
//                    public void onComplete(@NonNull @NotNull Task<GetTokenResult> task) {
//                        if(task.isSuccessful()) {
//                            String idToken = task.getResult().getToken();
//                            Log.d(TAG,"아이디 토큰 = " + idToken);
//                            Intent homeMove_intent = new Intent(getApplicationContext(), MainActivity.class);
//                            startActivity(homeMove_intent);
//                        }
//                    }
//                });

            }
        };


    }
    void loginEvent(){
        firebaseAuth.signInWithEmailAndPassword(id.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    //로그인 실패한 부분
                    Toast.makeText(Login.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}