package in.errorlabs.spotface.Activities.Register;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import in.errorlabs.spotface.MainMenu;
import in.errorlabs.spotface.R;
import in.errorlabs.spotface.Utils.SharedPrefs;

public class Capture extends AppCompatActivity implements SurfaceHolder.Callback {

    Button take_pic,flip;
    Camera camera1;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    public static boolean previewing = false;
    int currentCameraId;
    Context context;
    SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        take_pic = (Button) findViewById(R.id.takepic_btn);
        flip = (Button) findViewById(R.id.flip_cam_btn);
        sharedPrefs= new SharedPrefs(this);
        currentCameraId=sharedPrefs.getCam_prefs();
        startcam(currentCameraId);

        take_pic.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                if(camera1 != null) {
                    Camera.Parameters param;
                    param = camera1.getParameters();
                    param.setPreviewSize(640, 480);
                    param.setPictureSize(640, 480);
                    camera1.setParameters(param);
                    camera1.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);
                }
            }
        });
        flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera1.stopPreview();
                camera1.release();

                if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                }
                else {
                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                }
                    camera1 = Camera.open(currentCameraId);
                    if (camera1 != null){
                        try {
                            camera1.setDisplayOrientation(90);
                            camera1.setPreviewDisplay(surfaceHolder);
                            camera1.startPreview();
                            previewing = true;
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
            }
        });
    }


    android.hardware.Camera.ShutterCallback myShutterCallback = new android.hardware.Camera.ShutterCallback(){

        public void onShutter() {
            // TODO Auto-generated method stub
        }};

    android.hardware.Camera.PictureCallback myPictureCallback_RAW = new android.hardware.Camera.PictureCallback(){

        public void onPictureTaken(byte[] arg0, Camera arg1) {
            // TODO Auto-generated method stub
        }};

    android.hardware.Camera.PictureCallback myPictureCallback_JPG = new android.hardware.Camera.PictureCallback(){

        public void onPictureTaken(byte[] arg0, Camera arg1) {
            // TODO Auto-generated method stub
            Intent i = new Intent(getApplicationContext(),ConfirmPicture.class);
            i.putExtra("img",arg0);
            i.putExtra("cam",currentCameraId);
            startActivity(i);
        }};


    public void startcam(int id){

        if(!previewing){
            camera1 = Camera.open(currentCameraId);
            if (camera1 != null){
                try {
                    camera1.setDisplayOrientation(90);
                    camera1.setPreviewDisplay(surfaceHolder);
                    camera1.startPreview();
                    previewing = true;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        if(previewing){
            camera1.stopPreview();
            previewing = false;
        }
        if (camera1 != null){
            try {
                camera1.setPreviewDisplay(surfaceHolder);
                camera1.startPreview();
                previewing = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        startcam(currentCameraId);

    }
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        stoppreview();

    }
    public void stoppreview(){
        if (previewing && (camera1 != null)) {
            camera1.stopPreview();
            previewing = false;
            camera1.setPreviewCallback(null);
            camera1.release();
            camera1=null;
        }
    }

    @Override
    protected void onResume(){
        startcam(currentCameraId);
        super.onResume();
    }
    @Override
    public void onPause() {
        stoppreview();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stoppreview();
        Log.d("CAMERA","Destroy");
    }

    @Override
    public void onBackPressed(){
        stoppreview();
        startActivity(new Intent(getApplicationContext(),MainMenu.class));
        finish();
        super.onBackPressed();
    }
}
