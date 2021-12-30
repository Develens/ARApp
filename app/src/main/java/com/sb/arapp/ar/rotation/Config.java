package com.sb.arapp.ar.rotation;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ihsan_bz on 06/06/2016.
 */
public class Config {

    public static String toTheUpperCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

    public static String toTheUpperCaseSingle(String givenString) {
        String example = givenString;

        example = example.substring(0, 1).toUpperCase()
                + example.substring(1, example.length());

        System.out.println(example);
        return example;
    }

    public static double getDensityDpi(Context mContext) {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();

        double density = dm.density * 160;
        double x = Math.pow(dm.widthPixels / density, 2);
        double y = Math.pow(dm.heightPixels / density, 2);

        return Math.sqrt(x + y);
    }

    public static int dpToPx(Context mContext, int dp) {
        Resources r = mContext.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static float distanceFrom(double lat1, double lng1, double lat2, double lng2, double alt1, double alt2) {
        Location markerLoc = new Location("Marker");
        markerLoc.setLatitude(lat2);
        markerLoc.setLongitude(lng2);
        markerLoc.setAltitude(alt2);
        Location currentLoc = new Location("Current");
        currentLoc.setLatitude(lat1);
        currentLoc.setLongitude(lng1);
        currentLoc.setAltitude(alt1);
        return currentLoc.distanceTo(markerLoc);
    }


    public static void makeLinkClickable(final Context mContext, SpannableStringBuilder strBuilder, final URLSpan span)
    {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                // Do something with span.getURL() to handle the link click...
                Intent baseIntent = new Intent(Intent.ACTION_VIEW);
                Intent chooserIntent = Intent.createChooser(baseIntent, "Select Application");
                baseIntent.setData(Uri.parse(span.getURL()));
                mContext.startActivity(chooserIntent);
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    public static void setTextViewHTML(Context mContext, TextView text, String html)
    {
        CharSequence sequence;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sequence = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            sequence = Html.fromHtml(html);
        }
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for(URLSpan span : urls) {
            makeLinkClickable(mContext, strBuilder, span);
        }
        text.setText(strBuilder);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static String cDist(float dist){
        String distance;
        if(dist >= 500f){
            distance = String.valueOf(String.format("%.2f", dist / 1000f)) + " km";
        }else{
            distance = String.valueOf(String.format("%.2f", dist)) + " m";
        }
        return distance;
    }




    public static boolean checkConnection(Context cek) {
        ConnectivityManager cm = (ConnectivityManager) cek.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static void showToast(View v, String text) {
        Snackbar snackbar = Snackbar
                .make(v, text, Snackbar.LENGTH_LONG);
        snackbar.show();
    }


/*
    public static Date getDefault(){
        SimpleDateFormat f = new SimpleDateFormat(Constants.DATE_FORMAT);
        SimpleDateFormat dateFormatter = new SimpleDateFormat(Constants.DATE_FORMAT);
        f.setTimeZone(TimeZone.getDefault());
        Date nowDate = new Date();
        try {
            nowDate = dateFormatter.parse(f.format(new Date()));
        }catch (ParseException e){
            e.printStackTrace();
        }
        return nowDate;
    }*/

    public static Drawable setTint(Drawable d, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(d);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }


    public static String getStyledFont(String html, String backColor) {
        boolean addBodyStart = !html.toLowerCase().contains("<body>");
        boolean addBodyEnd = !html.toLowerCase().contains("</body");
        return "<style type=\"text/css\">@font-face {font-family: CustomFont;" +
                "src: url(\"file:///android_asset/fonts/QuattrocentoSans-Regular.ttf\")}" +
                "body {font-family: CustomFont;font-size:normal;background-color: "+ backColor +";}</style>" +
                (addBodyStart ? "<body>" : "") + html + (addBodyEnd ? "</body>" : "");
    }


    public static void clearApplicationData(Context mContext) {
        File cacheDirectory = mContext.getCacheDir();
        File applicationDirectory = new File(cacheDirectory.getParent());
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!fileName.equals("lib")) {
                    deleteFile(new File(applicationDirectory, fileName));
                }
            }
        }
    }
    
    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    deletedAll = deleteFile(new File(file, children[i])) && deletedAll;
                }
            } else {
                deletedAll = file.delete();
            }
        }
        
        return deletedAll;
    }
    
    public static boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

    public static void showSettingsAlert(final Context mContext) {
        /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle(R.string.gps_settings);
        alertDialog.setMessage(R.string.gps_failed);
        alertDialog.setPositiveButton(R.string.setting_test, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();*/
    }
}
