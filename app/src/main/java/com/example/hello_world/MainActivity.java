package com.example.hello_world;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button callButton;
    private Button mapButton;
    private Button routeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callButton = (Button)findViewById(R.id.callButtonId);
        mapButton = (Button)findViewById(R.id.mapButtonId);
        callButton.setOnClickListener(new ButtonListener1());
        mapButton.setOnClickListener(new ButtonListener2());
    }

    class ButtonListener1 implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            Intent intent = new Intent();
            intent.setClass(com.example.hello_world.MainActivity.this, com.example.hello_world.CallCar.class);
            startActivity(intent);
        }
    }

    class ButtonListener2 implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            Intent intent = new Intent();
            intent.setClass(com.example.hello_world.MainActivity.this, com.example.hello_world.mapActivity.class);
            startActivity(intent);
        }
    }
}
