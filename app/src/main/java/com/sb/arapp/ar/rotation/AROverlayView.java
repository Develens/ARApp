package com.sb.arapp.ar.rotation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.opengl.Matrix;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sb.arapp.R;
import com.sb.arapp.ar.model.LocationHelper;
import com.sb.arapp.networkRetrofit.poi.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AROverlayView extends View {

    Context mContext;
    int showDist;
    float[] rotatedProjectionMatrix = new float[16];
    Location currentLocation;
    List<Result> mPlacesModelList;

    RelativeLayout insertPoint;
    RelativeLayout rel_leftright;
    View view;
    boolean useOrientation;

    List<Location> poiLoc;
    private int rotation;
    float[] cameraCoordinateVector = new float[4];
    ArrayList<View> views;
    ImageView img_left, img_right;
    Double Angle;
    double angle;
    double ANG = 0;
    double DIS = 0;
    Boolean ISGET = false;
    ArrayList<Double> angles = new ArrayList<>();
    ArrayList<Integer> Xaxis_array = new ArrayList<>();
    ArrayList<Integer> Yaxis_array = new ArrayList<>();

    public AROverlayView(Context mContext, RelativeLayout insertPoint, int showDist) {
        super(mContext);
        this.insertPoint = insertPoint;
        this.mContext = mContext;
        this.showDist = showDist;
    }

    public void updateRotatedProjectionMatrix(float[] rotatedProjectionMatrix) {
        this.rotatedProjectionMatrix = rotatedProjectionMatrix;
        this.invalidate();
    }

    public void updateCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
        this.invalidate();
    }

    public void getAngle(Double Angle) {
        this.Angle = Angle;
        this.invalidate();
    }

    public void  getViews(ArrayList<View> views, ImageView img_left, ImageView img_right){
        this.views = views;
        this.img_left = img_left;
        this.img_right = img_right;
        this.invalidate();
    }

    public void updateFilter(RelativeLayout insertPoint, RelativeLayout rel_leftright,
                            List<Result> mPlacesModelList, int showDist,
                            boolean useOrientation){
        this.insertPoint = insertPoint;
        this.mPlacesModelList = mPlacesModelList;
        this.showDist = showDist;
        this.useOrientation = useOrientation;
        this.rel_leftright = rel_leftright;
        this.view = view;
        poiLoc = new ArrayList<>();
        for (int i=0; i<mPlacesModelList.size();i++){
            Location loc = new Location(mPlacesModelList.get(i).getName());

            loc.setAltitude(mPlacesModelList.get(i).getElevation());

            loc.setLatitude(mPlacesModelList.get(i).getGeometry().getLocation().getLat());

            loc.setLongitude(mPlacesModelList.get(i).getGeometry().getLocation().getLng());

            poiLoc.add(loc);
        }

        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentLocation == null) {
            // return back to previous activity
            return;
        }


        final int radius = 40;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(25);



        for (int i = 0; i < mPlacesModelList.size(); i++) {

            float[] currentLocationInECEF = LocationHelper.WSG84toECEF(currentLocation);

            float[] pointInECEF = LocationHelper.WSG84toECEF(poiLoc.get(i));
            float[] pointInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointInECEF);

            Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);

            if (cameraCoordinateVector[2] < 0) {

                float x = (0.5f + cameraCoordinateVector[0] / cameraCoordinateVector[3]) * canvas.getWidth();
                float y = (0.5f - cameraCoordinateVector[1] / cameraCoordinateVector[3]) * canvas.getHeight();

                if(insertPoint!=null) {
                    if(insertPoint.getChildAt(i)!=null) {
                        float X_axis;
                        float Y_axis;
                        if (Xaxis_array.size()!=0) {
                            insertPoint.getChildAt(i).setX((x - (insertPoint.getChildAt(i).getWidth())));
                            insertPoint.getChildAt(i).setY((y - 80) + Yaxis_array.get(i));
                            insertPoint.getChildAt(i).setVisibility(VISIBLE);

//                             X_axis = insertPoint.getChildAt(i).getX() - Xaxis_array.get(i);
                             Y_axis = insertPoint.getChildAt(i).getY() - Yaxis_array.get(i);
                        } else {
                            insertPoint.getChildAt(i).setX(x - (insertPoint.getChildAt(i).getWidth()));
                            insertPoint.getChildAt(i).setY(y - 80);
                            insertPoint.getChildAt(i).setVisibility(VISIBLE);

                             X_axis = insertPoint.getChildAt(i).getX();
                             Y_axis = insertPoint.getChildAt(i).getY();
                        }

//                        insertPoint.getChildAt(i).setX(x - (insertPoint.getChildAt(i).getWidth() ));
//                        insertPoint.getChildAt(i).setY(y - 80);
//                        insertPoint.getChildAt(i).setVisibility(VISIBLE);

                        //TODO IMPORTANT------------------
//                        float X_axis = insertPoint.getChildAt(i).getX();
//                        float Y_axis = insertPoint.getChildAt(i).getY();

                        int p = 0;

                        for (int j = 0; j < mPlacesModelList.size(); j++){

                            float[] currentLocationInECEF1 = LocationHelper.WSG84toECEF(currentLocation);

                            float[] pointInECEF1 = LocationHelper.WSG84toECEF(poiLoc.get(j));
                            float[] pointInENU1 = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF1, pointInECEF1);

                            Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU1, 0);

                            if (cameraCoordinateVector[2] < 0) {

                                float x1 = (0.5f + cameraCoordinateVector[0] / cameraCoordinateVector[3]) * canvas.getWidth();
                                float y1 = (0.5f - cameraCoordinateVector[1] / cameraCoordinateVector[3]) * canvas.getHeight();

                                if (insertPoint != null) {
                                    if (insertPoint.getChildAt(j) != null) {

                                        float Xaxis = x1 - (insertPoint.getChildAt(j).getWidth());
                                        float Yaxis = y1 - 80;

//                                        float Xaxis = insertPoint.getChildAt(j).getX();
//                                        float Yaxis = insertPoint.getChildAt(j).getY();
                                        try {
                                            if (Y_axis==Yaxis) {
                                                if (Yaxis - Y_axis < 210) {
//                                            insertPoint.getChildAt(i).setY(insertPoint.getChildAt(i).getY() + 350);
                                                    Yaxis_array.add(j, p);
                                                    p=p+220;
//                                            Xaxis_array.add(j,0);
//                                        } else if (Xaxis - X_axis < 600) {
//                                            insertPoint.getChildAt(i).setX(insertPoint.getChildAt(i).getX() + 650);
//                                            Yaxis_array.add(j, 0);
////                                            Xaxis_array.add(j,650);
                                                } else {
                                                    Yaxis_array.add(j, 0);
//                                            Xaxis_array.add(j,0);
                                                }
                                            } else {
                                                Yaxis_array.add(j, 0);
                                            }
                                        } catch (IndexOutOfBoundsException e){
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        //TODO IMPORTANT------------------

                        Log.d("NAME", " = " + mPlacesModelList.get(i).getName());
                        Log.d("AXIS", "X-axis = " + insertPoint.getChildAt(i).getX() + " Y-axis = " + insertPoint.getChildAt(i).getY());
                        Log.d("AREA", "Width " + insertPoint.getChildAt(i).getWidth() + " Height" + insertPoint.getChildAt(i).getHeight());
//                        Log.d("AREA", "Width " + insertPoint.getChildAt(i).getDisplay().hr + " Height" + insertPoint.getChildAt(i).getHeight());

                        rel_leftright.setVisibility(VISIBLE);
                        img_right.setVisibility(GONE);
                        img_left.setVisibility(GONE);
                    }
                }
            }

//            insertPoint.getChildAt(i).setId(i);
//            RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            layoutParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, (i-1));
//
//            insertPoint.getChildAt(i).setLayoutParams(layoutParam);

//            float qq = (views.get(i).getX() + views.get(i).getY())/2;
//            float ww = (insertPoint.getPivotX() + insertPoint.getPivotY())/2;
//            float ee = (insertPoint.getChildAt(i).getPivotX() + insertPoint.getChildAt(i).getPivotY())/2;
//
//            if (currentLocationInECEF[2] < pointInECEF[2]){
//                img_left.setVisibility(VISIBLE);
//            }else if (currentLocationInECEF[2] > pointInECEF[2]){
//                img_right.setVisibility(VISIBLE);
//            }

//            if (cameraCoordinateVector[2] < 0) {
//                if (qq < ee) {
//                    img_left.setVisibility(VISIBLE);
//                } else if (qq > ee) {
//                    img_right.setVisibility(VISIBLE);
//                }
//            }
        }

        Location closestLocation = null;
        float smallestDistance = -1;

        angles.clear();
        for(int i = 0; i < poiLoc.size(); i++){
//            Location location1 = location;
            Location location2 = currentLocation;

//            float angle = (float) Math.toDegrees(Math.atan2(location.getLatitude() - currentLocation.getLatitude(), location.getLongitude() - currentLocation.getLongitude()));

            angle = Math.atan2(poiLoc.get(i).getLatitude() - currentLocation.getLatitude(), poiLoc.get(i).getLongitude() - currentLocation.getLongitude())/(Math.PI/180);

            if (angle < 0) {
                angle += 360;
            }

            if (Angle < 90){
                Angle = Angle + 90;
            }else if (Angle > 90){
                Angle = Angle - 90;
            }

            Angle = Angle - 180;
            Angle = -Angle;

            Angle = Angle + DIS;

            if (Angle > 180){
                double m = Angle - 180;
                Angle = 0 + m;
            }

            if (Angle < angle/2){
                double ang = 180 - angle/2;
                ANG = (Angle + ang + 1) - (angle/2 + ang + 1);
            }else {
                ANG = Angle - angle/2;
            }

            if (ANG < 0) {
                ANG = -ANG;
            }

//            Log.d("current angle", String.valueOf(ANG));

            angles.add(ANG);

//            if (Angle > angle/2){
//                img_left.setVisibility(VISIBLE);
//            }else {
//                img_right.setVisibility(VISIBLE);
//            }

            if (!ISGET) {
                for (int j = 0; j < views.size(); j++) {
                    Rect scrollBounds = new Rect();
                    insertPoint.getHitRect(scrollBounds);
                    if (views.get(j).getLocalVisibleRect(scrollBounds)) {
                        float y = views.get(j).getX();
                        if (y > 300 && y < 400) {
                            double angle1 = Math.atan2(poiLoc.get(j).getLatitude() - currentLocation.getLatitude(), poiLoc.get(j).getLongitude() - currentLocation.getLongitude())/(Math.PI/180);
                            if (angle1 < Angle){
                                double ang = 180 - Angle;
                                DIS = (angle1/2 + ang + 1) - (Angle + ang + 1);
                            }else {
                                DIS = angle1 / 2 - Angle;
                            }
                            ISGET = true;
                            break;
                        }
                    }
                }
            }
        }

        if (Angle < 90){
            Angle = Angle + 90;
        }else if (Angle > 90){
            Angle = Angle - 90;
        }

        Angle = Angle - 180;
        Angle = -Angle;

        Angle = Angle + DIS;

        if (Angle > 180){
            double m = Angle - 180;
            Angle = 0 + m;
        }

        int indexOfMinimum = angles.indexOf(Collections.min(angles));
        double ange = Math.atan2(poiLoc.get(indexOfMinimum).getLatitude() - currentLocation.getLatitude(), poiLoc.get(indexOfMinimum).getLongitude() - currentLocation.getLongitude())/(Math.PI/180);

        if (ange < 0) {
            ange += 360;
        }



        double d = ange/2;

        if (Angle > d){
            img_left.setVisibility(VISIBLE);
        }else {
            img_right.setVisibility(VISIBLE);
        }

        for (int j = 0; j < views.size(); j++){
            Rect scrollBounds = new Rect();
            insertPoint.getHitRect(scrollBounds);
            if (views.get(j).getLocalVisibleRect(scrollBounds)) {
                rel_leftright.setVisibility(GONE);
//                float y = views.get(j).getX();
//                Log.d("Location", String.valueOf(y));
            }
        }
        canvas.save();
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        // haversine great circle distance approximation, returns meters
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60; // 60 nautical miles per degree of seperation
        dist = dist * 1852; // 1852 meters per nautical mile
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
