package com.minorproject.scanandbuy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.minorproject.scanandbuy.Models.Product;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class Add_Product_Activity extends AppCompatActivity {

    private String message = "";
    private TextView add_product_image;
    private boolean mStoragePermissions;
    private Dialog dialog;

    public static final int CAMERA_REQUEST_CODE = 5467;//random number
    public static final int PICKFILE_REQUEST_CODE = 8352;//random number
    private static final int REQUEST_CODE = 1234;
    private static final double MB_THRESHHOLD = 1.0;
    private static final double MB = 1000000.0;

    private Uri mSelectedImageUri;
    private Bitmap mSelectedImageBitmap;
    private byte[] mBytes;
    private double progress;
    private ImageView profile_image;
    private TextView upload_product;
    private TextInputEditText input_product_name,input_barcodeid,input_rfid,input_product_price,input_product_details;
    private Product product;
    private ProgressBar progressbar;
    private TextView scan_product;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product_layout);

        product = new Product();

        add_product_image = (TextView) findViewById(R.id.change_photo);
        upload_product = (TextView) findViewById(R.id.upload_product);
        scan_product = (TextView) findViewById(R.id.scan_product);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        progressbar = (ProgressBar) findViewById(R.id.addproduct_progressbar);

        input_product_name = (TextInputEditText) findViewById(R.id.input_product_name);
        input_barcodeid = (TextInputEditText) findViewById(R.id.input_barcode_id);
        input_rfid = (TextInputEditText) findViewById(R.id.input_rfid);
        input_product_price = (TextInputEditText) findViewById(R.id.input_product_price);
        input_product_details = (TextInputEditText) findViewById(R.id.input_product_details);

        //    message = getIntent().getExtras().getString("message");
        add_product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStoragePermissions) {
                    dialog = new Dialog(Add_Product_Activity.this);
                    dialog.setContentView(R.layout.dialog_changephoto);
                    TextView selectPhoto = (TextView) dialog.findViewById(R.id.dialogChoosePhoto);
                    selectPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent, PICKFILE_REQUEST_CODE);
                        }
                    });

                    //Initialize the textview for choosing an image from memory
                    TextView takePhoto = (TextView) dialog.findViewById(R.id.dialogOpenCamera);
                    takePhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                        }
                    });
                    dialog.show();

                } else {
                    verifyStoragePermissions();
                }
            }
        });

        upload_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_product_name.getText().length() != 0 && input_barcodeid.getText().length() != 0 && input_rfid.getText().length() != 0 && input_product_price.getText().length() != 0 && input_product_details.getText().length() != 0 && (mSelectedImageUri!=null || mSelectedImageBitmap!=null)) {


                    product.setProduct_name(input_product_name.getText().toString());
                    product.setProduct_barcode_id(input_barcodeid.getText().toString());
                    product.setProduct_rfid(input_rfid.getText().toString());
                    product.setProduct_price(input_product_price.getText().toString());
                    product.setProduct_details(input_product_details.getText().toString());
                    if (mSelectedImageUri!=null){
                        uploadNewPhoto(mSelectedImageUri);
                    }
                    if (mSelectedImageBitmap!=null){
                        uploadNewPhoto(mSelectedImageBitmap);
                    }
                }else {
                    Toast.makeText(Add_Product_Activity.this,"Fill All the Fields" , Toast.LENGTH_SHORT).show();
                }
            }
        });
        scan_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Add_Product_Activity.this,AddProductScan.class);
                startActivityForResult(intent,1);
            }
        });
    }


    public void verifyStoragePermissions(){
        String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0] ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1] ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[2] ) == PackageManager.PERMISSION_GRANTED) {
            mStoragePermissions = true;
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    REQUEST_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode){
            case REQUEST_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
        Results when selecting new image from phone memory
         */

        if (requestCode==1 && resultCode == Activity.RESULT_OK){
            String barcodeid = data.getStringExtra("barcodeid");
            input_barcodeid.setText(barcodeid);
        }
        if (requestCode == PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();

            //send the bitmap and fragment to the interface
            getImagePath(selectedImageUri);
            dialog.dismiss();

        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data");

            getImageBitmap(bitmap);
            dialog.dismiss();
        }
    }

    public void getImagePath(Uri imagePath) {
        if( !imagePath.toString().equals("")){
            mSelectedImageBitmap = null;
            mSelectedImageUri = imagePath;

            profile_image.setImageURI(imagePath);
            /*GlideApp.with(this)
                    .load(imagePath.toString())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // log exception
                            Toast.makeText(this,"Error"+e.toString(),Toast.LENGTH_LONG).show();
                            return false; // important to return false so the error placeholder can be placed
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .transform(new RoundedCornersTransformation(radius, margin))
                    .into(profile_image);*/
        }
    }
    public void getImageBitmap(Bitmap bitmap) {
        if(bitmap != null){
            mSelectedImageUri = null;
            mSelectedImageBitmap = bitmap;

            profile_image.setImageBitmap(bitmap);
            /*GlideApp.with(getActivity())
                    .load(bitmap)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // log exception
                            Toast.makeText(getActivity(),"Error"+e.toString(),Toast.LENGTH_LONG).show();
                            return false; // important to return false so the error placeholder can be placed
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .transform(new RoundedCornersTransformation(radius, margin))
                    .into(profile_image);*/
        }
    }



    public void uploadNewPhoto(Uri imageUri){
        /*
            upload a new profile photo to firebase storage
         */
        //Only accept image sizes that are compressed to under 5MB. If thats not possible
        //then do not allow image to be uploaded
        BackgroundImageResize resize = new BackgroundImageResize(null);
        resize.execute(imageUri);
    }
    public void uploadNewPhoto(Bitmap imageBitmap){
        /*
            upload a new profile photo to firebase storage
         */
        //Only accept image sizes that are compressed to under 5MB. If thats not possible
        //then do not allow image to be uploaded
        BackgroundImageResize resize = new BackgroundImageResize(imageBitmap);
        Uri uri = null;
        resize.execute(uri);
    }

    @SuppressLint("StaticFieldLeak")
    public class BackgroundImageResize extends AsyncTask<Uri, Integer, byte[]> {

        Bitmap mBitmap;
        BackgroundImageResize(Bitmap bm) {
            if(bm != null){
                mBitmap = bm;

            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(Add_Product_Activity.this, "compressing image", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected byte[] doInBackground(Uri... params ) {

            if(mBitmap == null){

                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(Add_Product_Activity.this.getContentResolver(), params[0]);

                } catch (IOException e) {
                }
            }

            byte[] bytes = null;
            for (int i = 1; i < 11; i++){
                if(i == 10){
                    Toast.makeText(Add_Product_Activity.this, "That image is too large.", Toast.LENGTH_SHORT).show();
                    break;
                }
                bytes = getBytesFromBitmap(mBitmap,100/i);
                if(bytes.length/MB  < MB_THRESHHOLD){
                    return bytes;
                }
            }
            return bytes;
        }


        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            mBytes = bytes;
            //execute the upload
            executeUploadTask();
        }
    }

    // convert from bitmap to byte array
    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }


    private void executeUploadTask(){
        //showDialog();
        FilePaths filePaths = new FilePaths();
//specify where the photo will be stored
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + input_barcodeid.getText().toString()); //just replace the old image with the new one


        if(mBytes.length/MB < MB_THRESHHOLD) {

            // Create file metadata including the content type
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .setContentLanguage("en") //see nodes below
                    /*
                    Make sure to use proper language code ("English" will cause a crash)
                    I actually submitted this as a bug to the Firebase github page so it might be
                    fixed by the time you watch this video. You can check it out at https://github.com/firebase/quickstart-unity/issues/116
                     */
                    .build();
            //if the image size is valid then we can submit to database
            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(mBytes); //without metadata


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Now insert the download url into the firebase database

                    StorageReference storage = taskSnapshot.getStorage();
                    storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String product_uniqueid = FirebaseDatabase.getInstance().getReference().push().getKey();

                            product.setProduct_image_url(uri.toString());
                            product.setProduct_id(product_uniqueid);
                            progressbar.setVisibility(View.INVISIBLE);
                            assert product_uniqueid != null;
                            FirebaseDatabase.getInstance().getReference()
                                    .child("Products")
                                    .child(product_uniqueid)
                                    .setValue(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(Add_Product_Activity.this,"Product Added Successfully",Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(Add_Product_Activity.this,Add_Product_Activity.class);
                                    startActivity(i);
                                    finish();
                                }
                            });

                        }
                    });
                    //hideDialog();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(Add_Product_Activity.this, "could not upload photo", Toast.LENGTH_SHORT).show();

                    //hideDialog();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double currentProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if(currentProgress > (progress + 15)){
                        progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        Toast.makeText(Add_Product_Activity.this, progress + "%", Toast.LENGTH_SHORT).show();
                    }

                }
            })
            ;
        }else{
            Toast.makeText(Add_Product_Activity.this, "Image is too Large", Toast.LENGTH_SHORT).show();
        }
    }
    public void setUIArguments(final String barcodeid) {
        Add_Product_Activity.this.runOnUiThread(new Runnable() {
            public void run() {
                /* do your UI stuffs */
                input_barcodeid.setText(barcodeid);
            }
        });
    }

}
