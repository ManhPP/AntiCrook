package com.android.findmyandroid;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView tvSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Chống trộm");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarColor)));
//        TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[] {android.R.attr.actionBarSize});
//        int actionBarSize = (int) styledAttributes.getDimension(0,0);
//        styledAttributes.recycle();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        tvSetting.setOnClickListener(settingClick);
//        startActivity(new Intent(this, EmailSendActivity.class));

//        startActivity(new Intent(this, AddReceivedEmail.class));
//        startActivity(new Intent(this, MainSetting.class));
//        startActivity(new Intent(this, SMSSettingActivity.class));
    }

    public void init(){
        tvSetting = findViewById(R.id.tvSetting);
    }


    private View.OnClickListener settingClick = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            startActivity(new Intent(MainActivity.this, MainSetting.class));
        }
    };
}
