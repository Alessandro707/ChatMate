package com.main.chatmate.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.main.chatmate.FirebaseHandler;
import com.main.chatmate.MyLogger;
import com.main.chatmate.R;
import com.main.chatmate.chat.User;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BioActivity extends AppCompatActivity {
    ImageView avatar;
    EditText nome, info;
    TextView phone;
    Button bottone;

    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data!=null) {
                            Bundle extras = data.getExtras();
                            // TODO: urina = data.getData();
                            if (extras != null) {
                                Bitmap imageBitmap = (Bitmap) extras.get("data");

                                if (imageBitmap != null) {
                                    avatar.setImageBitmap(imageBitmap);
                                }
                            }
                        }
                        else{
                            // l'unico contributo del signor Fiorenses
                           MyLogger.log("INSERISCI UN IMMAGINE COGLIONE BASTARDO FIGLIO DI PUTTANA PEZZO DI MERDA TI STUPRO TUTTO L'ALBERO GENIALOGICO");
                        }
                    }
                }
            });
    ActivityResultLauncher<Intent> cartellaActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data!=null) {
                            Uri urina = data.getData();
                            avatar.setImageURI(urina);
                            MyLogger.log(urina.getPath());
                        }
                        else{
                            MyLogger.log("INSERISCI UN IMMAGINE COGLIONE BASTARDO FIGLIO DI PUTTANA PEZZO DI MERDA TI STUPRO TUTTO L'ALBERO GENIALOGICO");
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio);
        avatar = findViewById(R.id.bio_avatar_imageView);
        nome = findViewById(R.id.bio_name_EditText);
        info = findViewById(R.id.bio_info_EditText);
        phone= findViewById(R.id.bio_TextPhone);
        bottone= findViewById(R.id.bio_conferma_Button);

        phone.setText(String.valueOf(getIntent().getExtras().get("Phone")));

        avatar.setImageResource(R.mipmap.app_icon);
        avatar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(BioActivity.this);
            builder.setTitle("Scegli la tua immagine di profilo :)");
            final CharSequence[] options = {"Fai una foto", "sceglila dalla galleria", "indietro :'("};

            builder.setItems(options, (dialog, item) -> {
                if (options[item].equals("Fai una foto")) {
                    Intent faifoto = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraActivityResultLauncher.launch(faifoto);

                } else if (options[item].equals("sceglila dalla galleria")) {
                    Intent sceglifoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                   cartellaActivityResultLauncher.launch(sceglifoto);

                } else if (options[item].equals("indietro :'(")) {
                    dialog.dismiss();
                }
            });
            builder.show();
        });

        bottone.setOnClickListener((v -> {
            if(!nome.getText().toString().isEmpty()){
                byte[] data = (nome.getText().toString()+"\n"+info.getText().toString()+"\n").getBytes();
                Bitmap imgp= ((BitmapDrawable) avatar.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imgp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imgprofilo = stream.toByteArray();
    
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("users/" + user.getUid());
                Map<String, Object> userData = new HashMap<>();
                userData.put("name", nome.getText().toString());
                userData.put("info", info.getText().toString());
                userData.put("phone", phone.getText().toString());
                databaseRef.updateChildren(userData);
                /*
                 FirebaseHandler.upload(FirebaseStorage.getInstance().getReference().child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+"/info.chatmate"),
                         data,
                         taskSnapshot -> {
                            MyLogger.log("New user data uploaded");
                         },
                         failureExceptions -> {
                            MyLogger.log("Kant upload new user info to database: "+ failureExceptions.getMessage());
                         }
                         );
                */
    
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/img_profilo.jpeg");
                FirebaseHandler.upload(storageRef, imgprofilo,
                        taskSnapshot -> {
                            MyLogger.log("User image profil udated :P");
                            User.get().logIn(nome.getText().toString(), info.getText().toString());
                            Intent mainActivity = new Intent(BioActivity.this, MainActivity.class);
                            startActivity(mainActivity);
                        },
                        failureExceptions -> {
                            MyLogger.log(":( user Image profil not uptaded: "+ failureExceptions.getMessage());
                        }
                );
                Intent loadingActivity = new Intent(BioActivity.this, LoadingActivity.class);
                startActivity(loadingActivity);
            }
            else{
                nome.setHint("COMPLETA QUESTO CAMPO!!!");
                nome.setHintTextColor(Color.RED);
            }
        }));
    }

}
