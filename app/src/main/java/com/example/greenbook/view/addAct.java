package com.example.greenbook.view;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.greenbook.R;
import com.example.greenbook.adapter.ItemAdapter;
import com.example.greenbook.databinding.ActAddBinding;
import com.example.greenbook.model.Item;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class addAct extends AppCompatActivity {

    final Calendar myCalendar = Calendar.getInstance();
    private ActAddBinding binding;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    Context context = this;
    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    DatePickerDialog.OnDateSetListener date;
    ArrayList<Item> itemArrayList;
    ItemAdapter itemAdapter;
    Intent intent;
    String info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActAddBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        //----------------------------------------------------------------

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
        itemAdapter = new ItemAdapter(itemArrayList);
        registerLauncher();

        intent = getIntent();
        info = intent.getStringExtra("info");

        if(info.equals("detail")){

            String itemId = intent.getStringExtra("itemId");
            try {

                firebaseFirestore.collection("Items").whereEqualTo("downloadurl",itemId).addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                                binding.editTextDate.setText(date);
                                binding.editTextTextMultiLine.setText(comment);
                                binding.editTextTextPersonName.setText(title);
                                binding.addBtn.setVisibility(View.INVISIBLE);
                                binding.cancelBtn.setVisibility(View.INVISIBLE);
                                binding.saveBtn.setVisibility(View.INVISIBLE);
                                binding.editTextTextPersonName.setEnabled(false);
                                binding.imageView.setClickable(false);
                                binding.editTextTextMultiLine.setEnabled(false);
                                binding.editTextDate.setEnabled(false);
                                Picasso.get().load(downloadurl).into(binding.imageView);

                            }


                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }else {
            binding.editActionBtn.setVisibility(View.INVISIBLE);
            binding.deleteActionBtn.setVisibility(View.INVISIBLE);
            binding.saveBtn.setVisibility(View.INVISIBLE);

        }




        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR,year);
                myCalendar.set(Calendar.MONTH,monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateLabel();
            }
        };


    }

    public void editButtonClicked(View view){
        binding.cancelBtn.setVisibility(View.VISIBLE);
        binding.saveBtn.setVisibility(View.VISIBLE);
        binding.editTextTextPersonName.setEnabled(true);
        binding.imageView.setClickable(true);
        binding.editTextTextMultiLine.setEnabled(true);
        binding.editTextDate.setEnabled(true);
        binding.deleteActionBtn.setVisibility(View.INVISIBLE);
        binding.editActionBtn.setVisibility(View.INVISIBLE);
        binding.imageView.setImageDrawable(getDrawable(R.drawable.selectimage));
    }

    public void deleteButtonClicked(View view){
        Snackbar.make(view,"Are you sure that delete this story?",Snackbar.LENGTH_INDEFINITE).setAction("Delete", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("Items").whereEqualTo("downloadurl",intent.getStringExtra("itemId")).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Toast.makeText(context,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }

                        if(value != null){

                            for(DocumentSnapshot snapshot : value.getDocuments()){

                                firebaseFirestore.collection("Items").document(snapshot.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context,"Deleted!",Toast.LENGTH_LONG).show();
                                        itemAdapter.notifyDataSetChanged();
                                        Intent intent = new Intent(context,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });

                            }


                        }
                    }
                });
            }
        }).show();
    }

    public void saveButtonClicked(View view){

        if(imageData != null && textIsEmpty(binding.editTextDate) && textIsEmpty(binding.editTextTextMultiLine) && textIsEmpty(binding.editTextTextPersonName)){

            firebaseFirestore.collection("Items").whereEqualTo("downloadurl",intent.getStringExtra("itemId")).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null){
                        Toast.makeText(context,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }

                    if(value != null){

                        String downloadUrl = imageData.toString();
                        String comment = binding.editTextTextMultiLine.getText().toString();
                        String title = binding.editTextTextPersonName.getText().toString();
                        String date = binding.editTextDate.getText().toString();

                        HashMap<String, Object> postData = new HashMap<>();
                        postData.put("downloadurl",downloadUrl);
                        postData.put("comment",comment);
                        postData.put("title",title);
                        postData.put("date",date);

                        for(DocumentSnapshot snapshot : value.getDocuments()){

                            firebaseFirestore.collection("Items").document(snapshot.getId()).update(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context,"Saved!",Toast.LENGTH_LONG).show();
                                    itemAdapter.notifyDataSetChanged();
                                    Intent intent = new Intent(context,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                }
                            });

                        }


                    }
                }
            });

        }else{
            Toast.makeText(context,"Please fill all blanks!!",Toast.LENGTH_LONG).show();
        }
    }

    public void addButton(View view){

        if(imageData != null && textIsEmpty(binding.editTextDate) && textIsEmpty(binding.editTextTextMultiLine) && textIsEmpty(binding.editTextTextPersonName)){

            UUID uuid = UUID.randomUUID();
            String imageName = "Images/" + uuid + ".jpg";

            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //Download url
                    StorageReference newReference = firebaseStorage.getReference(imageName);
                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();
                            String comment = binding.editTextTextMultiLine.getText().toString();
                            String title = binding.editTextTextPersonName.getText().toString();
                            String date = binding.editTextDate.getText().toString();

                            HashMap<String, Object> postData = new HashMap<>();
                            postData.put("downloadurl",downloadUrl);
                            postData.put("comment",comment);
                            postData.put("title",title);
                            postData.put("date",date);

                            firebaseFirestore.collection("Items").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    Intent intent = new Intent(context,MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();

                                }
                            });

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });

        }else {
            Toast.makeText(context,"Please fill all blanks!!",Toast.LENGTH_LONG).show();
        }

    }

    public void dateClicked(View view){
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public void cancelButton(View view){
        Intent intent = new Intent(context , MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void selectedImage(View view){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Permission needed for gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //ask permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                    }
                }).show();
            }else {
                //ask permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }
    }

    private void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if(result.getResultCode() == RESULT_OK){
                    Intent intentFromResult = result.getData();

                    if(intentFromResult != null){
                        imageData = intentFromResult.getData();
                        binding.imageView.setImageURI(imageData);
                    }
                }

            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);

                } else {
                    Toast.makeText(context,"Permission Needed!",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void updateLabel() {
        try {
            String myFormat = "dd/MM/yy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            binding.editTextDate.setText(sdf.format(myCalendar.getTime()));
        } catch (Exception e) {
            Toast.makeText(context,"Please select a date!",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    private boolean textIsEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return true;

        return false;
    }
}
