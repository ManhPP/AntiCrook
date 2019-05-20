package com.android.findmyandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //startActivity(new Intent(this, EmailSendActivity.class));
//        startActivity(new Intent(this, AddReceivedEmail.class));
        startActivity(new Intent(this, MainSetting.class));
//        startActivity(new Intent(this, SMSSettingActivity.class));
    }
}
