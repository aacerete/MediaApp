package com.example.aacerete.mediaapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;



import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

/**
 * A placeholder fragment containing a simple view.
 */
public class MediaFragment extends Fragment {

    private Button btnFoto;
    private Button btnVideo;
    private GridView gvImages;

    //instanciamos db firebase
    FirebaseDatabase database;

    private FirebaseListAdapter mAdapter;
    File f;

    //ruta de la imagen
    String mCurrentPhotoPath;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 2;

    public MediaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_main, container, false);

        btnFoto = (Button) view.findViewById(R.id.btnFoto);
        btnVideo = (Button) view.findViewById(R.id.btnVideo);


        //al darle al boton de foto, abrimos camara
        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePictureIntent();

            }
        });

        btnVideo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                TakeVideoIntent();

            }
        });

        gvImages = (GridView) view.findViewById(R.id.gvImages);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        //Adaptador del gridview
        mAdapter = new FirebaseListAdapter<Gallery>(getActivity(), Gallery.class, R.layout.gv_item, ref) {

            @Override
            protected void populateView(View view, Gallery item, int position) {
                ImageView img = (ImageView) view.findViewById(R.id.imageView);
                Glide.with(getContext()).load(Uri.fromFile(new File(item.absolute)))
                        .centerCrop()
                        .crossFade()
                        .into(img);
            }
        };
        gvImages.setAdapter(mAdapter);


        return view;
    }

    private File createMediaFile(String format, File directori) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "Media" + timeStamp + "_";


        File image = File.createTempFile(
                fileName,  /* prefix */
                format,         /* suffix */
                directori      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        System.out.println(image.getAbsolutePath());


        return image;
    }




    private void TakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;

            try {
                File storageDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
                photoFile = createMediaFile(".jpg" , storageDir);

            } catch (IOException ex) {


            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                f = photoFile;
            }
        }
    }

    private void TakeVideoIntent(){

        //create new Intent
        Intent takeVidIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (takeVidIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File videoFile = null;

            try {
                File storageDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MOVIES);
                videoFile = createMediaFile(".mp4" , storageDir);

            } catch (IOException ex) {


            }
            // Continue only if the File was successfully created
            if (videoFile != null) {

                takeVidIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoFile);
                // set the image file name
                takeVidIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                // set the video image quality to high
                takeVidIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(videoFile));
                startActivityForResult(takeVidIntent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);

                f = videoFile;
            }
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // La imagen ha sido guardada en el móvil..

                    // Envíamos la referencia a la db realtime, para rellenar el gridview
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    Gallery msg = new Gallery(f.getName(), f.getAbsolutePath());
                    ref.push().setValue(msg);


                }

            case CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    // Video capturado y guardado en el movil

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    Gallery msg = new Gallery(f.getName(), f.getAbsolutePath());
                    ref.push().setValue(msg);
                }


        }
    }
}