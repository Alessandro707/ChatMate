package com.main.chatmate;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
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
import com.google.firebase.storage.FirebaseStorage;
import com.main.chatmate.activities.MainActivity;

import java.io.ByteArrayOutputStream;

public class BioActivity extends AppCompatActivity {
    ImageView avatar;
    EditText nome, info;
    TextView phone;
    Button bottone;
    Uri urina;

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data!=null) {
                            Bundle extras = data.getExtras();
                            urina = data.getData();
                            if (extras != null) {
                                Bitmap imageBitmap = (Bitmap) extras.get("data");

                                if (imageBitmap != null) {
                                    avatar.setImageBitmap(imageBitmap);
                                }
                            }
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
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BioActivity.this);
                builder.setTitle("Scegli la tua immagine di profilo :)");
                final CharSequence[] options = {"Fai una foto", "sceglila dalla galleria", "indietro :'("};

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (options[item].equals("Fai una foto")) {
                            Intent faifoto = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            someActivityResultLauncher.launch(faifoto);

                        } else if (options[item].equals("sceglila dalla galleria")) {
                            Intent sceglifoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                           someActivityResultLauncher.launch(sceglifoto);

                        } else if (options[item].equals("indietro :'(")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
            });

        bottone.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nome.toString().isEmpty()){
                    byte[] data = (nome.getText().toString()+"\n"+info.getText().toString()+"\n").getBytes();
                    Bitmap imgp= ((BitmapDrawable) avatar.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imgp.compress(Bitmap.CompressFormat.valueOf(MyHelper.getFormat(urina)), 100, stream);
                    byte[] imgprofilo = stream.toByteArray();

                     FirebaseHandler.upload(FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/info.chatmate"),
                             data,
                             taskSnapshot -> {
                                MyLogger.log("New user data uploaded");
                             },
                             failureExceptions -> {
                                MyLogger.log("Kant upload new user info to database: "+ failureExceptions.getMessage());
                             }
                             );

                    FirebaseHandler.upload(FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/img_profilo."+ MyHelper.getFormat(urina)),
                            imgprofilo,
                            taskSnapshot -> {
                                MyLogger.log("User image profil udated :P");
                                User.get().logIn(data);
                                Intent intent = new Intent(BioActivity.this, MainActivity.class);
                                startActivity(intent);
                            },
                            failureExceptions -> {
                                MyLogger.log(":( user Image profil not uptaded: "+ failureExceptions.getMessage());
                            }
                    );

                }
                else{
                    nome.setHint("COMPLETA QUESTO CAMPO!!!");
                    nome.setHintTextColor(Color.RED);
                }
            }
        }));
    }

}

