package com.example.matrixscanreject;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.matrixscanreject.data.ScanResult;
import com.scandit.datacapture.barcode.data.Barcode;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTracking;
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTrackingListener;
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTrackingSession;
import com.scandit.datacapture.barcode.tracking.capture.BarcodeTrackingSettings;
import com.scandit.datacapture.barcode.tracking.data.TrackedBarcode;
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingAdvancedOverlay;
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingAdvancedOverlayListener;
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingBasicOverlay;
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingBasicOverlayListener;
import com.scandit.datacapture.barcode.tracking.ui.overlay.BarcodeTrackingBasicOverlayStyle;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.common.geometry.Anchor;
import com.scandit.datacapture.core.common.geometry.FloatWithUnit;
import com.scandit.datacapture.core.common.geometry.MeasureUnit;
import com.scandit.datacapture.core.common.geometry.PointWithUnit;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.CameraSettings;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.core.source.VideoResolution;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.scandit.datacapture.core.ui.style.Brush;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class MatrixScanActivity extends CameraPermissionActivity implements BarcodeTrackingListener {

    // Enter your Scandit License key here.
    // Your Scandit License key is available via your Scandit SDK web account.
    public static final String SCANDIT_LICENSE_KEY = "AVjSbjncG+pwDeDn1EG/ua8/Tzm5Q2nG5T37l0x89JjjUJAHeH6FhUBDpSyxU6fbJ2HIz9UDDBo9c/mUjyQBwvZNAPw1RDJCGWsw+GNg4glaf/GhhG/ncj1LtvX9dUaihUukbPg2+OcsE6xgtj93fY0abZNKhix6j6bMa2lB6D3Zq9EKSPN4bzgbzon/8GK5kWVihZJ5raLi8Gs2sO4PtKRpDx9SJyHcCSEH7hxOHdh7VNNmBzAOF30gHWBWx8bfArFs4h0RJCIPsgK0PdwwKUIThPjYG+oN7sZAAbRiGN1gwY3EORihuwg9YAnYdIVxTqFJQsTmYaP4AD3676ypDpKTLqyCd7p3KvWIyCGU6GRK7t9RsWSg0ou0sMlqcoxD7WK9Jsap+sfu236rcbuInRP4BI8MZ0OgA4XC7131xDAhPTXA1tBJQMt8w4SiaGK3StYXgnxBW7oNoWoqHmKHq0+z9GXBc6gmjntrEi0EQg+L69LYDE6fETg0jhWp1Egk/8PfyJ12NxJleGktJ+LBbIYCXVsXKkuuXhlTcGE5zh1rWlecvzxwIVhUKyzJ+f3s5KMysYKrQdMBEJYvfUZGCScgcpLXuah3HYV7y0++Uljiivjbx5LTHJzuDyNHdmk9p0q0l2qfY+MTN2dv/h1EWQpq1MjnZeIZJOHX7pzpEEJTlPdHBbBE0YtfAKdQwrj46U9g/bp4RRhIQn4733Wd5HTxS6eX62dGWFFKt98H3mS8NSmyNE+9bRVpcIWeZKjY6rMSQoKJWzcrsgMdWSKWliNuIfJ52899f1tRXMA88r4FYapd/Ht3vJ71gigsYjJ8IL1QYQ==";

    public static final int REQUEST_CODE_SCAN_RESULTS = 1;

    private Camera camera;
    private BarcodeTracking barcodeTracking;
    private DataCaptureContext dataCaptureContext;

    private final HashSet<ScanResult> scanResults = new HashSet<>();

    private RequestQueue queue;

    private final String URL= "http://35.211.170.102/getPreparacion.php";

    ArrayList<String> list = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Before starting Scan, we need the codes available
        queue = Volley.newRequestQueue(this);
         //getData();
        getData();

        setContentView(R.layout.activity_matrix_scan);
        setTitle(R.string.app_title);

        // Initialize and start the barcode recognition.
        initialize();

        Button doneButton = findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (scanResults) {
                    // Show new screen displaying a list of all barcodes that have been scanned.
                    Intent intent = ResultsActivity.getIntent(
                            MatrixScanActivity.this, scanResults);
                    startActivityForResult(intent, REQUEST_CODE_SCAN_RESULTS);
                }
            }
        });
    }


    public void getData() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0; i < response.length(); i++) {
                    try {
                        JSONObject object = new JSONObject(response.get(i).toString());
                        list.add(object.getString("lote"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.v("TAG_ARRAY", String.valueOf(list));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MatrixScanActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                Log.d("TAG", "volleyError" + volleyError.getMessage());
                return super.parseNetworkError(volleyError);
            }


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s","mmunoz","a8dab92ff8dfa9f45aa89f551c56188d93683c4aa3db6b84aba748872221b934");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Authorization ", auth);
                return params;
            }
        };
        queue.add(request);
    }

    private void initialize() {
        // Create data capture context using your license key.
        dataCaptureContext = DataCaptureContext.forLicenseKey(SCANDIT_LICENSE_KEY);

        // Use the recommended camera settings for the BarcodeTracking mode.
        CameraSettings cameraSettings = BarcodeTracking.createRecommendedCameraSettings();
        // Adjust camera settings - set Full HD resolution.
        cameraSettings.setPreferredResolution(VideoResolution.UHD4K);
        // Use the default camera and set it as the frame source of the context.
        // The camera is off by default and must be turned on to start streaming frames to the data
        // capture context for recognition.
        // See resumeFrameSource and pauseFrameSource below.
        if (cameraSettings.getZoomGestureZoomFactor() == 1.0F) {
            cameraSettings.setZoomGestureZoomFactor(3.0F);
        }

        if (cameraSettings.getZoomGestureZoomFactor() == 3.0F){
            cameraSettings.setZoomGestureZoomFactor(6.0F);
        }

        if (cameraSettings.getZoomGestureZoomFactor() == 6.0F){
            cameraSettings.setZoomGestureZoomFactor(8.0F);
        }

        //cameraSettings.setMaxFrameRate(60.0F);
        cameraSettings.setShouldPreferSmoothAutoFocus(true);
        camera = Camera.getDefaultCamera(cameraSettings);
        if (camera != null) {
            dataCaptureContext.setFrameSource(camera);
        } else {
            throw new IllegalStateException(
                    "App depends on a camera, which failed to initialize.");
        }

        // The barcode tracking process is configured through barcode tracking settings
        // which are then applied to the barcode tracking instance that manages barcode tracking.
        BarcodeTrackingSettings barcodeTrackingSettings = new BarcodeTrackingSettings();

        // The settings instance initially has all types of barcodes (symbologies) disabled.
        // For the purpose of this sample we enable a very generous set of symbologies.
        // In your own app ensure that you only enable the symbologies that your app requires
        // as every additional enabled symbology has an impact on processing times.
        HashSet<Symbology> symbologies = new HashSet<>();
        symbologies.add(Symbology.EAN13_UPCA);
        symbologies.add(Symbology.EAN8);
        symbologies.add(Symbology.UPCE);
        symbologies.add(Symbology.CODE39);
        symbologies.add(Symbology.CODE128);
        symbologies.add(Symbology.CODE11);
        symbologies.add(Symbology.CODE25);
        symbologies.add(Symbology.DOT_CODE);
        symbologies.add(Symbology.MAXI_CODE);
        symbologies.add(Symbology.QR);
        symbologies.add(Symbology.CODABAR);
        symbologies.add(Symbology.MICRO_QR);
        symbologies.add(Symbology.AZTEC);


        barcodeTrackingSettings.enableSymbologies(symbologies);

        // Create barcode tracking and attach to context.
        barcodeTracking = BarcodeTracking.forDataCaptureContext(dataCaptureContext, barcodeTrackingSettings);

        // Register self as a listener to get informed of tracked barcodes.
        barcodeTracking.addListener(this);

        // To visualize the on-going barcode tracking process on screen, setup a data capture view
        // that renders the camera preview. The view must be connected to the data capture context.
        DataCaptureView dataCaptureView = DataCaptureView.newInstance(this, dataCaptureContext);

        // Add a barcode tracking overlay to the data capture view to render the tracked barcodes on
        // top of the video preview. This is optional, but recommended for better visual feedback.
        BarcodeTrackingBasicOverlay overlay = BarcodeTrackingBasicOverlay.newInstance(
                barcodeTracking,
                dataCaptureView,
                BarcodeTrackingBasicOverlayStyle.FRAME
        );

//        BarcodeTrackingAdvancedOverlay overlay = BarcodeTrackingAdvancedOverlay.newInstance(
//                barcodeTracking,
//                dataCaptureView
//        );

        // Configure how barcodes are highlighted - apply default brush or create your own.
        int acceptedBorderColor = getResources().getColor(R.color.barcode_accepted_border);
        final Brush acceptedBrush = new Brush(Color.TRANSPARENT, acceptedBorderColor, 3f);

        // Modify brush dynamically.
        // Note that modifying a barcode's brush color requires the MatrixScan AR add-on.
        int rejectedBorderColor = getResources().getColor(R.color.barcode_rejected_border);
        final Brush rejectedBrush = new Brush(Color.TRANSPARENT, rejectedBorderColor, 3f);
        overlay.setListener(new BarcodeTrackingBasicOverlayListener() {
            @Override
            @NonNull
            public Brush brushForTrackedBarcode(
                    @NonNull BarcodeTrackingBasicOverlay overlay,
                    @NonNull TrackedBarcode trackedBarcode
            ) {
                if (isValidBarcode(trackedBarcode.getBarcode())) {
                    return acceptedBrush;
                } else {
                    return rejectedBrush;
                }
            }

            @Override
            public void onTrackedBarcodeTapped(
                    @NonNull BarcodeTrackingBasicOverlay overlay,
                    @NonNull TrackedBarcode trackedBarcode
            ) {
                // Handle barcode click if necessary.
            }
        });

//        overlay.setListener(new BarcodeTrackingAdvancedOverlayListener() {
//            @Nullable
//            @Override
//            public View viewForTrackedBarcode(@NonNull BarcodeTrackingAdvancedOverlay barcodeTrackingAdvancedOverlay, @NonNull TrackedBarcode trackedBarcode) {
//                // Create and return the view you want to show for this tracked barcode. You can also return null, to have no view for this barcode.
//                TextView textView = new TextView(MatrixScanActivity.this);
//                textView.setBackgroundColor(Color.WHITE);
//                textView.setLayoutParams(
//                        new ViewGroup.LayoutParams(
//                                ViewGroup.LayoutParams.WRAP_CONTENT,
//                                ViewGroup.LayoutParams.WRAP_CONTENT
//                        )
//                );
//                textView.setText(trackedBarcode.getBarcode().getData());
//                return textView;
//
//            }
//
//            @NonNull
//            @Override
//            public Anchor anchorForTrackedBarcode(@NonNull BarcodeTrackingAdvancedOverlay barcodeTrackingAdvancedOverlay, @NonNull TrackedBarcode trackedBarcode) {
//                // As we want the view to be above the barcode, we anchor the view's center to the top-center of the barcode quadrilateral.
//                // Use the function 'offsetForTrackedBarcode' below to adjust the position of the view by providing an offset.
//                return Anchor.TOP_CENTER;
//            }
//
//            @NonNull
//            @Override
//            public PointWithUnit offsetForTrackedBarcode(@NonNull BarcodeTrackingAdvancedOverlay barcodeTrackingAdvancedOverlay, @NonNull TrackedBarcode trackedBarcode, @NonNull View view) {
//                // This is the offset that will be applied to the view.
//                // You can use MeasureUnit.FRACTION to give a measure relative to the view itself, the sdk will take care of transforming this into pixel size.
//                // We now center horizontally and move up the view to make sure it's centered and above the barcode quadrilateral by half of the view's height.
//                return new PointWithUnit(new FloatWithUnit(0f, MeasureUnit.FRACTION),new FloatWithUnit(-1f, MeasureUnit.FRACTION));
//            }
//        });

        // Add the DataCaptureView to the container.
        FrameLayout container = findViewById(R.id.data_capture_view_container);
        container.addView(dataCaptureView);
    }


    @Override
    protected void onPause() {
        pauseFrameSource();
        super.onPause();
    }

    private void pauseFrameSource() {
        // Switch camera off to stop streaming frames.
        // The camera is stopped asynchronously and will take some time to completely turn off.
        // Until it is completely stopped, it is still possible to receive further results, hence
        // it's a good idea to first disable barcode tracking as well.
        barcodeTracking.setEnabled(false);
        camera.switchToDesiredState(FrameSourceState.OFF, null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check for camera permission and request it, if it hasn't yet been granted.
        // Once we have the permission the onCameraPermissionGranted() method will be called.
        requestCameraPermission();
    }

    @Override
    public void onCameraPermissionGranted() {
        resumeFrameSource();
    }

    private void resumeFrameSource() {
        // Switch camera on to start streaming frames.
        // The camera is started asynchronously and will take some time to completely turn on.
        barcodeTracking.setEnabled(true);
        camera.switchToDesiredState(FrameSourceState.ON, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SCAN_RESULTS
                && resultCode == ResultsActivity.RESULT_CODE_CLEAN) {
            synchronized (scanResults) {
                scanResults.clear();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onObservationStarted(@NonNull BarcodeTracking barcodeTracking) {
        // Nothing to do.
    }

    @Override
    public void onObservationStopped(@NonNull BarcodeTracking barcodeTracking) {
        // Nothing to do.
    }

    // This function is called whenever objects are updated and it's the right place to react to
    // the tracking results.
    @Override
    public void onSessionUpdated(
            @NonNull BarcodeTracking mode,
            @NonNull BarcodeTrackingSession session,
            @NonNull FrameData data
    ) {
        synchronized (scanResults) {
            for (TrackedBarcode trackedBarcode : session.getAddedTrackedBarcodes()) {
                if (isValidBarcode(trackedBarcode.getBarcode())) {
                    scanResults.add(new ScanResult(trackedBarcode.getBarcode()));
                }
            }
        }
    }

    // Method with custom logic for accepting/rejecting recognized barcodes.
    private boolean isValidBarcode(Barcode barcode) {
        // Reject invalid barcodes.
        if (barcode.getData() == null || barcode.getData().isEmpty()) return false;

        // Reject barcodes based on your logic.
        //if (barcode.getData().startsWith("7") || barcode.getData().equals("MU034008")  || barcode.getData().matches("MU034007") || barcode.getData().contains("MU034006")) return false;

        // Just allow the codes of lotes from db
        //Log.v("TAG_array_to_string", list.toString());
        for (int i = 0; i < list.size() ; i++) {
            //Log.v("TAG_index", list.get(i));
            if(barcode.getData().equals(list.get(i))) return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        dataCaptureContext.removeMode(barcodeTracking);
        super.onDestroy();
    }

    public static class ainActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        }
    }
}
