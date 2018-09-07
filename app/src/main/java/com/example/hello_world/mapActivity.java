package com.example.hello_world;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class mapActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        imageView = (ImageView)findViewById(R.id.imageViewId);
        imageView.setImageResource(R.drawable.cas);
        //wori
        //axiba
    }
}
