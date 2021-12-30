package com.sb.arapp.ar.rotation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.opengl.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


@SuppressLint("ViewConstructor")
public class ARCamera extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG = ARCamera.class.getSimpleName();;

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;

    Activity activity;

    float[] projectionMatrix = new float[16];

    int cameraWidth;
    int cameraHeight;

    private final static float Z_NEAR = 0.5f;
    private final static float Z_FAR = 2000;

    int screenWidth, screenHeight;
    int rotation;

    public ARCamera(Context context, SurfaceView surfaceView) {
        super(context);

        this.surfaceView = surfaceView;
        this.activity = (Activity) context;
        surfaceHolder = this.surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        rotation = windowManager.getDefaultDisplay()
                .getRotation();

        this.screenWidth = displayMetrics.widthPixels;
        this.screenHeight = displayMetrics.heightPixels;

//        Log.e("ARCamera","ARCamera:"+screenWidth+" x "+screenHeight);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
       /*
        if (this.camera != null) {
            Camera.Parameters params = this.camera.getParameters();

            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                this.camera.setParameters(params);
            }

        }
        */
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (camera != null) {

                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);

                int degrees = 0;
                switch (rotation) {
                    case Surface.ROTATION_0:
                        degrees = 0;
                        break;
                    case Surface.ROTATION_90:
                        degrees = 90;
                        break;
                    case Surface.ROTATION_180:
                        degrees = 180;
                        break;
                    case Surface.ROTATION_270:
                        degrees = 270;
                        break;
                }

                camera.setDisplayOrientation((info.orientation - degrees + 360) % 360);

                camera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(camera != null) {
            this.cameraWidth = width;
            this.cameraHeight = height;

//            Log.e("ARCamera","surfaceChanged:"+width+" x "+height);

            Camera.Parameters parameters = camera.getParameters();
            try {
                List<Camera.Size> supportedSizes = camera.getParameters().getSupportedPreviewSizes();
                for (Camera.Size element : supportedSizes) {
                    element.width -= cameraWidth;
                    element.height -= cameraHeight;
                }
                Collections.sort(supportedSizes, new ResolutionOrders());
                parameters.setPreviewSize(width + supportedSizes.get(supportedSizes.size()-1).width, height + supportedSizes.get(supportedSizes.size()-1).height);
            } catch (Exception ex) {
                parameters.setPreviewSize(screenWidth, screenHeight);
            }

            this.camera.setParameters(parameters);
            this.camera.startPreview();

            generateProjectionMatrix();
        }
    }

    class ResolutionOrders implements java.util.Comparator<Camera.Size>{
        public int compare(Camera.Size left, Camera.Size right) {
            return Float.compare(left.width + left.height, right.width + right.height);
        }
    }

    private void generateProjectionMatrix() {
        float ratio = (float) this.screenWidth / this.screenHeight;
        final int OFFSET = 0;
        final float LEFT =  -ratio;
        final float RIGHT = ratio;
        final float BOTTOM = -1;
        final float TOP = 1;
        Matrix.frustumM(projectionMatrix, OFFSET, LEFT, RIGHT, BOTTOM, TOP, Z_NEAR, Z_FAR);
    }

    public float[] getProjectionMatrix() {
        return projectionMatrix;
    }

}
