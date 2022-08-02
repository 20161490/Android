package com.example.safefamily.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.safefamily.Join;
import com.example.safefamily.Login;
import com.example.safefamily.MainActivity;
import com.example.safefamily.R;
import com.example.safefamily.model.ChatModel;
import com.example.safefamily.model.Usermodel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelectFriendActivity extends AppCompatActivity {
    ChatModel chatModel = new ChatModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.selectFriendActivity_recyclerview);
        recyclerView.setAdapter(new SelectFriendRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Button button = (Button) findViewById(R.id.selectFriendActivity_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                chatModel.users.put(myUid,true);

                FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel);
                Intent intent = new Intent(SelectFriendActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }
    class SelectFriendRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        List<Usermodel> usermodels;

        public SelectFriendRecyclerViewAdapter() {
            usermodels = new ArrayList<>();
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("users").child("가족지킴이 어플").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    usermodels.clear();
                    for(DataSnapshot dataSnapshot :snapshot.getChildren()){


                        Usermodel usermodel  = dataSnapshot.getValue(Usermodel.class);

                        if(usermodel.uid.equals(myUid)){
                            continue;
                        }
                        usermodels.add(usermodel);
                    }
                    notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_select,parent,false);


            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            Glide.with
                    (holder.itemView.getContext())
                    .load(usermodels.get(position).profileImageUrl)
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder)holder).imageView);
            ((CustomViewHolder)holder).textView.setText(usermodels.get(position).userName);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), Message.class);
                    intent.putExtra("destinationUid",usermodels.get(position).uid);
                    ActivityOptions activityOptions = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fromright,R.anim.toleft);
                        startActivity(intent,activityOptions.toBundle());
                    }

                }
            });

            if(usermodels.get(position).comment != null){
                ((CustomViewHolder) holder).textView_comment.setText(usermodels.get(position).comment);
            }
            ((CustomViewHolder) holder).checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //체크 된 상태
                    if(isChecked){
                        chatModel.users.put(usermodels.get(position).uid,true);
                        //체크 취소 상태
                    }else{
                        chatModel.users.remove(usermodels.get(position));
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return usermodels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;
            public TextView textView_comment;
            public CheckBox checkBox;

            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.frienditem_imageview);
                textView = (TextView) view.findViewById(R.id.frienditem_textview);
                textView_comment = (TextView)view.findViewById(R.id.frienditem_textview_comment);
                checkBox = (CheckBox)view.findViewById(R.id.friendItem_checkbox);
            }
        }
    }
}