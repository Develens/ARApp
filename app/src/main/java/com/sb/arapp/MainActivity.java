package com.sb.arapp;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sb.arapp.ar.rotation.ARFragmentPoc;

public class MainActivity extends AppCompatActivity {
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.tv);


        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
              openARview();
            }
        }, 2000);


    }


    private void openARview(){

        ARFragmentPoc createRenyooletFragment = new ARFragmentPoc();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle1 = new Bundle();
        createRenyooletFragment.setArguments(bundle1);
        transaction.replace(R.id.frame_container, createRenyooletFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
