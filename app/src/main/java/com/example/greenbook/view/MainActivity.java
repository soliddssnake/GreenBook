package com.example.greenbook.view;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.example.greenbook.databinding.ActivityMainBinding;
import com.example.greenbook.model.Item;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseFirestore firebaseFirestore;
    Context context = this;
    ArrayList<Item> itemArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firebaseFirestore = FirebaseFirestore.getInstance();

        getData();

        itemArrayList = new ArrayList<>();
    }

    public void fabButtonClicked(View view){
        Intent intent = new Intent(context , addAct.class);
        startActivity(intent);
    }

    private void getData(){

        firebaseFirestore.collection("Items").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error != null){
                    Toast.makeText(context,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }

                if(value != null){

                    for(DocumentSnapshot snapshot : value.getDocuments()){

                        Map<String, Object> data = snapshot.getData();

                        //casting
                        String title = (String) data.get("title");
                        String date = (String) data.get("date");
                        String downloadurl = (String) data.get("downloadurl");
                        String comment = (String) data.get("comment");

                        Item item = new Item(title,comment,downloadurl,date);
                        itemArrayList.add(item);
                    }
                }

            }
        });

    }

}