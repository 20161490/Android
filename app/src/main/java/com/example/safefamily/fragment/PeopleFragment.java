package com.example.safefamily.fragment;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.safefamily.R;
import com.example.safefamily.Splash;
import com.example.safefamily.chat.MEssage;
import com.example.safefamily.model.Usermodel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PeopleFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people,container,false);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.peoplefragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new PeopleFragmentRecyclerViewAdapter());

        FloatingActionButton floatingActionButton = (FloatingActionButton)view.findViewById(R.id.peoplefragment_floatingButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view.getContext(),SelectFriendActivity.class));
            }
        });

        return view;
    }

    class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        List<Usermodel>usermodels;
        public PeopleFragmentRecyclerViewAdapter(){
            usermodels = new ArrayList<>();
            String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("users").child("가족지킴이 어플").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    usermodels.clear();
                    for(DataSnapshot dataSnapshot :snapshot.getChildren()){
                        Usermodel usermodel =  dataSnapshot.getValue(Usermodel.class);

                        if(usermodel.uid.equals(myUid)){
                            continue;
                        }
                        usermodels.add(usermodel);

                    }
                    notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend,parent,false);
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
                    Intent intent= new Intent(view.getContext(), MEssage.class);
                    intent.putExtra("destinationUid",usermodels.get(position).uid);
                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(),R.anim.fromright,R.anim.toleft);
                    startActivity(intent,activityOptions.toBundle());
                }
            });
            if(usermodels.get(position).comment != null){
                ((CustomViewHolder) holder).textView_comment.setText(usermodels.get(position).comment);
            }



        }

        @Override
        public int getItemCount() {
            return usermodels.size();
        }
        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;
            public TextView textView_comment;
            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.frienditem_imageview);
                textView = (TextView) view.findViewById(R.id.frienditem_textview);
                textView_comment = (TextView)view.findViewById(R.id.frienditem_textview_comment);

            }
        }
    }
}
