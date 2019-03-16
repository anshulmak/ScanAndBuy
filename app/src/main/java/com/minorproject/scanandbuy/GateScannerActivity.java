package com.minorproject.scanandbuy;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.scandit.barcodepicker.BarcodePicker;
import com.scandit.barcodepicker.OnScanListener;
import com.scandit.barcodepicker.ScanSession;
import com.scandit.barcodepicker.ScanSettings;
import com.scandit.barcodepicker.ScanditLicense;
import com.scandit.recognition.Barcode;
import com.scandit.recognition.SymbologySettings;

public class GateScannerActivity extends AppCompatActivity implements OnScanListener {

    private Button scan_barcode;
    private final int Barcode_req_code = 200;

    public static final String sScanditSdkAppKey= "ASUcEBG8DbHoDPieWjD24qkryRvAL5DU6m2UmCxwx9cndtweO1WWM6RSZ133WiyM22PAB1lDKknfW0rGpxmPQ/satua6b+0oNhFn7Xl8RWI2Sx66Y3EcvyxePk+ESfHtX3CTpW8nj82ELTSEdUT0+Ur8/FEYIVOa111PssYFq396CUWgb6G96SNsOVw6kcoSZNFdK6kGsUElSpsYviQJVECFLn1wwBOtq7baoMqOVJN+PSxefYZVZyV4ywWADg076arTu2NZXIQKAXNhdFNNrArmvUHi5WcvuAkos+VbMoDKaOa6UBAX63q3azWymbIhkfHNr7FANpMMNPKk32iDy9IUCoJVVRR9jMxFeu8uNO4/tEiaZzlSd/cqrGyTeXii5wpnJ2zF9J8kISEVg1j1VIOrtCn9sSOnrspY+lF8ETHeyQugp7tNdOMNFTO4k34ExjIq/p6jOO0paA2zzu31SgDn/0INnj6iZmOL5RV/kIe86qXWgTmOHGLnRVK6pVsT77iqqZNhp623QkaveOiHsFgbQoinvorp8mtVI8KkNW6bRY2rGKqNTCthSGztzooCfqGlJMSuk7K1Ucsog5GDuKjvQ57S2ju/I3X4AI8f5DHoyGOxu3WW+O8mUOEkZP7XRkkmQk2IrNNiEjaInfdHjgTGILZ+5Bs9tyEaHrq3c4tcr8MNTK7dkhc5iPkPzQ43eMK12HUECFTru4AjeKohqwM0GUjCFW1GfWSs8IoBBhcJg/FjDSxUxAItV6fvVhkDXNhh94Q1soi+aeAHNh4rjMN9NSF0vU7CRDnUVygp8R1sWnwgjNRhyjpv50ui";
    private final int CAMERA_PERMISSION_REQUEST = 0;

    // The main object for recognizing and displaying barcodes.
    private BarcodePicker mBarcodePicker;
    private boolean mDeniedCameraAccess = false;
    private boolean mPaused = true;
    private Toast mToast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set your license key
        ScanditLicense.setAppKey(sScanditSdkAppKey);

        initializeAndStartBarcodeScanning();
        }
       // protected void onActivityResult(int request_code, int result_code, Intent data){
        //if (request_code == Barcode_req_code){
          //  Bitmap photo = (Bitmap) data.getExtras().get("data");
            //barcoderecognition(photo);
        //}
        //}

    @Override
    public void didScan(ScanSession scanSession) {
        String message = "";
        for (Barcode code : scanSession.getNewlyRecognizedCodes()) {
            String data = code.getData();
            // Truncate code to certain length.
            String cleanData = data;
            
            if (message.length() > 0) {
                message += "\n\n\n";
            }
            message += cleanData;
            //message += "\n\n(" + code.getSymbologyName().toUpperCase(Locale.US) + ")";
        }
        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        //mToast.show();
        //mBarcodePicker.stopScanning();
        if (message.length()!=0) {
            Intent intent = new Intent(GateScannerActivity.this, GatePassActivity.class);
            intent.putExtra("barcodeid", message);
            startActivity(intent);
        }
        //
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    @Override
    protected void onPause() {
        super.onPause();

        // When the activity is in the background immediately stop the
        // scanning to save resources and free the camera.
        mBarcodePicker.stopScanning();
        mPaused = true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void grantCameraPermissionsThenStartScanning() {
        if (this.checkSelfPermission(android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (mDeniedCameraAccess == false) {
                // It's pretty clear for why the camera is required. We don't need to give a
                // detailed reason.
                this.requestPermissions(new String[]{ android.Manifest.permission.CAMERA },
                        CAMERA_PERMISSION_REQUEST);
            }
        } else {
            // We already have the permission.
            mBarcodePicker.startScanning();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mDeniedCameraAccess = false;
                if (!mPaused) {
                    mBarcodePicker.startScanning();
                }
            } else {
                mDeniedCameraAccess = true;
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPaused = false;
        // Handle permissions for Marshmallow and onwards...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            grantCameraPermissionsThenStartScanning();
        } else {
            // Once the activity is in the foreground again, restart scanning.
            mBarcodePicker.startScanning();
        }
    }

    /**
     * Initializes and starts the bar code scanning.
     */
    public void initializeAndStartBarcodeScanning() {
        // Switch to full screen.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // The scanning behavior of the barcode picker is configured through scan
        // settings. We start with empty scan settings and enable a very generous
        // set of symbologies. In your own apps, only enable the symbologies you
        // actually need.
        ScanSettings settings = ScanSettings.create();
        int[] symbologiesToEnable = new int[] {
                Barcode.SYMBOLOGY_EAN13,
                Barcode.SYMBOLOGY_EAN8,
                Barcode.SYMBOLOGY_UPCA,
                Barcode.SYMBOLOGY_DATA_MATRIX,
                Barcode.SYMBOLOGY_QR,
                Barcode.SYMBOLOGY_CODE39,
                Barcode.SYMBOLOGY_CODE128,
                Barcode.SYMBOLOGY_INTERLEAVED_2_OF_5,
                Barcode.SYMBOLOGY_UPCE
        };
        for (int sym : symbologiesToEnable) {
            settings.setSymbologyEnabled(sym, true);
        }

        // Some 1d barcode symbologies allow you to encode variable-length data. By default, the
        // Scandit BarcodeScanner SDK only scans barcodes in a certain length range. If your
        // application requires scanning of one of these symbologies, and the length is falling
        // outside the default range, you may need to adjust the "active symbol counts" for this
        // symbology. This is shown in the following few lines of code.

        SymbologySettings symSettings = settings.getSymbologySettings(Barcode.SYMBOLOGY_CODE39);
        short[] activeSymbolCounts = new short[] {
                7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20
        };
        symSettings.setActiveSymbolCounts(activeSymbolCounts);
        // For details on defaults and how to calculate the symbol counts for each symbology, take
        // a look at http://docs.scandit.com/stable/c_api/symbologies.html.

        // Prefer the back-facing camera, is there is any.
        settings.setCameraFacingPreference(ScanSettings.CAMERA_FACING_BACK);

        // Some Android 2.3+ devices do not support rotated camera feeds. On these devices, the
        // barcode picker emulates portrait mode by rotating the scan UI.
        boolean emulatePortraitMode = !BarcodePicker.canRunPortraitPicker();
        if (emulatePortraitMode) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        BarcodePicker picker = new BarcodePicker(this, settings);

        setContentView(picker);
        mBarcodePicker = picker;

        // Register listener, in order to be notified about relevant events
        // (e.g. a successfully scanned bar code).
        mBarcodePicker.setOnScanListener(this);
    }
    public void onBackPressed(){
        Intent intent = new Intent(GateScannerActivity.this,Main2Activity.class);
        startActivity(intent);
        finish();
    }

}
