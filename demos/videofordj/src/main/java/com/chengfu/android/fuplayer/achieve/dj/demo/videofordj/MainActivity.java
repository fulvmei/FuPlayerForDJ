package com.chengfu.android.fuplayer.achieve.dj.demo.videofordj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.play).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, VodPlayActivity.class);
            startActivity(intent);
        });
    }
}
