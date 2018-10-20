package com.mobilegradingsystem.mobilegradingsystem;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobilegradingsystem.mobilegradingsystem.appModules.GlideApp;
import com.mobilegradingsystem.mobilegradingsystem.student.StudentRegistration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateAccount extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42;
    boolean access_storage;
    private static final int storagepermision_access_code = 548;
    boolean imageSet = false;
    Uri bannerUri;
    TextView updateProfile;
    CircleImageView circleImageView;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);
        findViewById(R.id.circleImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });
        context = this;
        updateProfile = (TextView) findViewById(R.id.updateProfile);
        circleImageView =(CircleImageView) findViewById(R.id.circleImageView);
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, StudentRegistration.class);
                i.putExtra("isUpdate",true);
                startActivity(i);
            }
        });
        GlideApp.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(circleImageView);
    }

    public void performFileSearch() {
        getStoragePermission();
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Filter to show only images, using the image MIME data imageType.
        // If one wanted to search for ogg vorbis files, the imageType would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Choose File"), READ_REQUEST_CODE);

    }
    private void getStoragePermission(){
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            access_storage = true;

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    storagepermision_access_code);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("TAG", "Uri: " + uri.getLastPathSegment());
//                ImageBannerOldURL = bannerUri;


                uploadItemBanner(uri);

            }else {
                System.out.println("null?");
            }
        }
    }
    private void setImage(Uri uri, ImageView imageView){
        // floatClearImage.setVisibility(View.VISIBLE);
        // Picasso.with(CreatePostActivity.this).load(uri).resize(300,600).into(imageToUpload);
        bannerUri = uri;
        imageSet = true;
//        GlideApp.with(AddRoom.this).load(uri).centerCrop().into(imageView);
        imageView.setPadding(0,0,0,0);
    }



    public void uploadItemBanner(final Uri ImageStorageURI){
//        setProgress(true);
//        loadingContainer.setVisibility(View.VISIBLE);
        findViewById(R.id.loadingIndicator).setVisibility(View.VISIBLE);
        if (ImageStorageURI!=null) {
            InputStream storeBannerFile = null;
            try {
                storeBannerFile = getContentResolver().openInputStream(ImageStorageURI);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            final InputStream file = storeBannerFile;

            final StorageReference ImagestoreRef = FirebaseStorage.getInstance().getReference().child("images"+ File.separator+ File.separator + getFileName(ImageStorageURI)
                    +storeBannerFile.toString()+File.separator+getFileName(ImageStorageURI));
            ImagestoreRef.putStream(storeBannerFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ImagestoreRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                           FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid())
                           .update("userImage",uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   GlideApp.with(UpdateAccount.this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(circleImageView);
                                   finish();
                               }
                           });

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(uri)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                GlideApp.with(UpdateAccount.this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(circleImageView);
                                                finish();
                                            }
                                        }
                                    });
                            findViewById(R.id.loadingIndicator).setVisibility(View.GONE);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    setProgress(false);
                    System.out.print(e);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    try {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()/file.available());
//                        uploadProgress.setText("Uploading " +taskSnapshot.getBytesTransferred()+" of "+file.available()+" "+progress+" %");
                    }catch (IOException e){

                    }

                }
            });
        }

    }
    private String getFileName(Uri uri) {

        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
