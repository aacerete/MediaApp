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

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private Button btnFoto;
    private Button btnVideo;
    private GridView gvImages;

    //instanciamos db firebase
    FirebaseDatabase database;

    //referencia
    private StorageReference mStorageRef;
    private FirebaseListAdapter mAdapter;
    File f;

    //ruta de la imagen
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Cargamos la imagen
        mStorageRef = FirebaseStorage.getInstance().getReference();


        View view = inflater.inflate(R.layout.fragment_main, container, false);

        btnFoto = (Button) view.findViewById(R.id.btnFoto);
        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePictureIntent();

            }
        });

        gvImages = (GridView) view.findViewById(R.id.gvImages);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
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
                photoFile = createImageFile();
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
        }


    }
}