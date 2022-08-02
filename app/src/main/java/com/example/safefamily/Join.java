package com.example.safefamily;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.safefamily.model.Usermodel;
import com.example.safefamily.Login;
import com.example.safefamily.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Join extends AppCompatActivity {

    private static final  int PICK_FROM_ALBUM = 10;
    private EditText email;
    private EditText name;
    private EditText password;
    private Button join;
    private String splash_background;
    private ImageView profile;
    private Uri imageUri;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background = mFirebaseRemoteConfig.getString(getString(R.string.rc_color));
        getWindow().setStatusBarColor(Color.parseColor(splash_background));

        profile = (ImageView)findViewById(R.id.join_imageview_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,PICK_FROM_ALBUM);
            }
        });

        email =(EditText)findViewById(R.id.join_edittext);
        name =(EditText)findViewById(R.id.join_edittext_name);
        password =(EditText)findViewById(R.id.join_edittext_password);
        join = (Button)findViewById(R.id.join_button_join);
        join.setBackgroundColor(Color.parseColor(splash_background));

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString()==null||name.getText().toString()==null||password.getText().toString()==null||imageUri==null){
                    Toast.makeText(getApplicationContext(),"프로필 혹은 가입양식 을 다시 확인해주세요.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(Join.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                final String uid = task.getResult().getUser().getUid();

                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString()).build();
                                task.getResult().getUser().updateProfile(userProfileChangeRequest);

                                final StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("userImages").child(uid);

                                profileImageRef.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if(!task.isSuccessful()) {
                                            throw task.getException();
                                        }
                                        return profileImageRef.getDownloadUrl();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if(task.isSuccessful()) {
                                            Uri downUri = task.getResult();
                                            String imageUri = downUri.toString();

                                            Usermodel userModel = new Usermodel();
                                            userModel.userName = name.getText().toString();
                                            userModel.profileImageUrl = imageUri;
                                            userModel.emailId=email.getText().toString();
                                            userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                            FirebaseDatabase.getInstance().getReference().child("users").child("가족지킴이 어플").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Intent intent = new Intent(Join.this, Login.class);
                                                    startActivity(intent);
                                                    Join.this.finish();
                                                }
                                            });
                                            Toast.makeText(Join.this,"회원가입에 성공하셧습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK){
            profile.setImageURI(data.getData()); //가운데 뷰를 바꿈
            imageUri = data.getData();//이미지 경로 원본
        }
    }
}