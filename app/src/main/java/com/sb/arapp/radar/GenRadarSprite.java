package com.sb.arapp.radar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class GenRadarSprite extends View {
	private Paint mPaint;
    private List<GenRadarPoint> mRadarPoints;
    private int mMapWidth=200;
    
    // Lint requires a Default Construct for Views, So, make it private because its useless
    public GenRadarSprite(Context context, List<GenRadarPoint> genRadarPoints, int mapWidth){
    	super(context);
        this.mRadarPoints = new ArrayList<GenRadarPoint>(genRadarPoints);
        this.mMapWidth = mapWidth-20;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }
    
    public GenRadarSprite(Context context, List<GenRadarPoint> genRadarPoints) {
        super(context);
        this.mRadarPoints = new ArrayList<GenRadarPoint>(genRadarPoints);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("VF","ON DRAW: "+mRadarPoints.size()+ " , "+mMapWidth);
        float x = 0, y = 0;

        for(int i = 0; i < mRadarPoints.size(); i++){

            Log.e("VF","ON DRAW 2: "+mRadarPoints.get(i).getLocationName() +" , "+mRadarPoints.get(i).getX());
        	mPaint.setColor(mRadarPoints.get(i).getColor());

            // Log.e("VF","X VALUE 1: "+mRadarPoints.get(i).getX());

            if(mRadarPoints.get(i).getX() > mMapWidth ){
                Log.e("VF","check 1");
                x = mRadarPoints.get(i).getX()-100;

                if(mRadarPoints.get(i).getY() > mMapWidth){
                    y = mRadarPoints.get(i).getY()-100;
                }
                else {
                    y = mRadarPoints.get(i).getY();
                }

            }
            else if(mRadarPoints.get(i).getY() > mMapWidth){
                Log.e("VF","check 2");
                x = mRadarPoints.get(i).getX();
                y = mRadarPoints.get(i).getY()-100;
            }
            else if(mRadarPoints.get(i).getX()<0){
                Log.e("VF","check 3");
                x = mRadarPoints.get(i).getY()-90;
                y = mRadarPoints.get(i).getY();
            }
            else {
                Log.e("VF","check 4");
                x = mRadarPoints.get(i).getX();
                y = mRadarPoints.get(i).getY();
            }



        	/*if(mRadarPoints.get(i).getX() > 0 && mRadarPoints.get(i).getX()<100) {
        	    x = mRadarPoints.get(i).getX();
            }
            else if(mRadarPoints.get(i).getX()<0) {
                x = 80;
            }
            else if(mRadarPoints.get(i).getX()>100) {
                x = 80;
            }*/

            Log.e("VF","X VALUE 2: "+x +" , "+y);
               // canvas.drawCircle(mRadarPoints.get(i).getX(),
                 //       mRadarPoints.get(i).getY(), mRadarPoints.get(i).getRaduis(), mPaint);

            canvas.drawCircle(x, y, mRadarPoints.get(i).getRaduis(), mPaint);

        }        
    }
    
    public void updateUIWithNewRadarPoints(List<GenRadarPoint> genRadarPoints){
    	this.mRadarPoints = new ArrayList<GenRadarPoint>(genRadarPoints);
    	this.invalidate();
    }
}