package com.android.findmyandroid;

import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SMSSettingActivity extends AppCompatActivity {

    private TextView tvWifi, tvReadSMS, tvContact, tvRecord, tvFrontCam, tvBackCam, tvLocate, tvAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smssetting);
        initView();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Cài đặt tin nhắn");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarColor)));
        TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[] {android.R.attr.actionBarSize});
        int actionBarSize = (int) styledAttributes.getDimension(0,0);
        styledAttributes.recycle();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Toast.makeText(this, "bam quay lai", Toast.LENGTH_SHORT ).show();
                onBackPressed();

                return true;

            default:break;
        }

        return super.onOptionsItemSelected(item);
    }
    public void initView(){
        tvWifi = findViewById(R.id.tvWifi);
        tvReadSMS = findViewById(R.id.tvReadSMS);
        tvContact = findViewById(R.id.tvContact);
        tvRecord = findViewById(R.id.tvRecord);
        tvFrontCam = findViewById(R.id.tvFrontCam);
        tvBackCam = findViewById(R.id.tvBackCam);
        tvLocate = findViewById(R.id.tvLocate);
        tvAlarm = findViewById(R.id.tvAlarm);

        tvWifi.setOnClickListener(onClickItem);
        tvReadSMS.setOnClickListener(onClickItem);
        tvContact.setOnClickListener(onClickItem);
        tvRecord.setOnClickListener(onClickItem);
        tvFrontCam.setOnClickListener(onClickItem);
        tvBackCam.setOnClickListener(onClickItem);
        tvLocate.setOnClickListener(onClickItem);
        tvAlarm.setOnClickListener(onClickItem);
    }

    public void initDialog(final TextView tv, String title){
        final View view = getLayoutInflater().inflate(R.layout.alert_dialog_custom_layout, null);
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);


        final EditText inputDialog =  view.findViewById(R.id.inputDialog);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = inputDialog.getText().toString();
                tv.setText(s);
            }
        });


        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                alertDialog.dismiss()
            }
        });


        alertDialog.setView(view);
        alertDialog.show();
    }


    private View.OnClickListener onClickItem = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()){
                case R.id.tvWifi:
                    initDialog(tvWifi, "Lệnh SMS bật wifi");
                    break;
                case R.id.tvReadSMS:
                    initDialog(tvReadSMS, "Lệnh SMS đọc tin nhắn");
                    break;
                case R.id.tvContact:
                    initDialog(tvContact, "Lệnh SMS đọc danh bạ");
                    break;
                case R.id.tvRecord:
                    initDialog(tvRecord, "Lệnh SMS ghi âm");
                    break;
                case R.id.tvFrontCam:
                    initDialog(tvFrontCam, "Lệnh SMS chụp cam trước");
                    break;
                case R.id.tvBackCam:
                    initDialog(tvBackCam, "Lệnh SMS chụp cam sau");
                    break;
                case R.id.tvLocate:
                    initDialog(tvLocate, "Lệnh SMS lấy tọa độ");
                    break;
                case R.id.tvAlarm:
                    initDialog(tvAlarm, "Lệnh SMS báo động");
                    break;
            }
        }
    };


}
