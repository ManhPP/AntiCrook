package com.android.findmyandroid;

import android.content.DialogInterface;
import android.content.SharedPreferences;
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

        SharedPreferences sharedPreferences = getSharedPreferences("appSetting", MODE_PRIVATE);
        tvWifi.setText(sharedPreferences.getString("WIFI", "WIFI_ON"));
        tvReadSMS.setText(sharedPreferences.getString("SMS", "READ_SMS"));
        tvContact.setText(sharedPreferences.getString("CONTACT", "READ_CONTACT"));
        tvRecord.setText(sharedPreferences.getString("RECORD", "RECORD"));
        tvFrontCam.setText(sharedPreferences.getString("FRONT", "FRONT_CAM"));
        tvBackCam.setText(sharedPreferences.getString("BACK", "BACK_CAM"));
        tvLocate.setText(sharedPreferences.getString("LOCATE", "LOCATE"));
        tvAlarm.setText(sharedPreferences.getString("ALARM", "ALARM"));

        tvWifi.setOnClickListener(onClickItem);
        tvReadSMS.setOnClickListener(onClickItem);
        tvContact.setOnClickListener(onClickItem);
        tvRecord.setOnClickListener(onClickItem);
        tvFrontCam.setOnClickListener(onClickItem);
        tvBackCam.setOnClickListener(onClickItem);
        tvLocate.setOnClickListener(onClickItem);
        tvAlarm.setOnClickListener(onClickItem);
    }

    public void initDialog(final TextView tv, String title, final String key){
        final View view = getLayoutInflater().inflate(R.layout.alert_dialog_custom_layout, null);
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setCancelable(false);


        final EditText inputDialog =  view.findViewById(R.id.inputDialog);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = inputDialog.getText().toString();
                if(!s.trim().equals("")) {
                    tv.setText(s);
                    SharedPreferences sharedPreferences = getSharedPreferences("appSetting", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(key, s);
                    editor.apply();
                }else{
                    Toast.makeText(getApplicationContext(), "Hãy điền lệnh sms!!!", Toast.LENGTH_SHORT).show();
                }
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
                    initDialog(tvWifi, "Lệnh SMS bật wifi", "WIFI");
                    break;
                case R.id.tvReadSMS:
                    initDialog(tvReadSMS, "Lệnh SMS đọc tin nhắn","SMS");
                    break;
                case R.id.tvContact:
                    initDialog(tvContact, "Lệnh SMS đọc danh bạ", "CONTACT");
                    break;
                case R.id.tvRecord:
                    initDialog(tvRecord, "Lệnh SMS ghi âm", "RECORD");
                    break;
                case R.id.tvFrontCam:
                    initDialog(tvFrontCam, "Lệnh SMS chụp cam trước", "FRONT");
                    break;
                case R.id.tvBackCam:
                    initDialog(tvBackCam, "Lệnh SMS chụp cam sau", "BACK");
                    break;
                case R.id.tvLocate:
                    initDialog(tvLocate, "Lệnh SMS lấy tọa độ", "LOCATE");
                    break;
                case R.id.tvAlarm:
                    initDialog(tvAlarm, "Lệnh SMS báo động", "ALARM");
                    break;
            }
        }
    };


}
