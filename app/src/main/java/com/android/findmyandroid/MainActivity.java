package com.android.findmyandroid;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.findmyandroid.utils.CheckPermission;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvSetting;
    private PolicyManager policyManager;
    Switch activate = null;
    private LinearLayout settingGroup;
    private LinearLayout uninstallApp;
    public CheckPermission checkPermission = new CheckPermission(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission.checkAndRequestPermission(null);
        policyManager = new PolicyManager(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Chống trộm");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarColor)));

        activate = findViewById(R.id.activate_button);
        activate.setOnCheckedChangeListener(onActivate);
        uninstallApp = findViewById(R.id.uninstall_app);
        uninstallApp.setOnClickListener(onUninstall);
        init();


        settingGroup.setOnClickListener(settingClick);

        //khoi tao share preferences cho lenh sms neu chua ton tai
        SharedPreferences sharedPreferences = getSharedPreferences("appSetting", MODE_PRIVATE);
        if(sharedPreferences==null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getResources().getString(R.string.WIFI), "WIFI_ON");
            editor.putString(getResources().getString(R.string.SMS), "READ_SMS");
            editor.putString(getResources().getString(R.string.CONTACT), "READ_CONTACT");
            editor.putString(getResources().getString(R.string.RECORD), "RECORD");
            editor.putString(getResources().getString(R.string.FRONT), "FRONT_CAM");
            editor.putString(getResources().getString(R.string.BACK), "BACK_CAM");
            editor.putString(getResources().getString(R.string.LOCATE), "LOCATE");
            editor.putString(getResources().getString(R.string.ALARM), "ALARM");
            editor.apply();
        }

    }

    public void init(){
//        tvSetting = findViewById(R.id.tvSetting);
        settingGroup = findViewById(R.id.settingGroup);
    }

    public CompoundButton.OnCheckedChangeListener onActivate = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                if (!policyManager.isAdminActive()) {
                    Intent activateDeviceAdmin = new Intent(
                            DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    activateDeviceAdmin.putExtra(
                            DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                            policyManager.getAdminComponent());
                    activateDeviceAdmin
                            .putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                    "Sau khi kích hoạt quyền admin cho ứng dụng, bạn sẽ có thể chặn việc gỡ ứng dụng.");
                    startActivityForResult(activateDeviceAdmin,
                            PolicyManager.DPM_ACTIVATION_REQUEST_CODE);
                }
            }
            else{
                if (policyManager.isAdminActive())
                    policyManager.disableAdmin();
            }
        }
    };

    public void initDialogUninstall(){
        final View view = getLayoutInflater().inflate(R.layout.alert_dialog_uninstall, null);
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Cảnh báo");
        alertDialog.setCancelable(false);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (policyManager.isAdminActive())
                    policyManager.disableAdmin();
                Uri packageURI = Uri.parse("package:"+MainActivity.class.getPackage().getName());
                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                startActivity(uninstallIntent);
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                alertDialog.dismiss()
            }
        });


        alertDialog.setView(view);
        alertDialog.show();
    }
    private View.OnClickListener onUninstall = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            initDialogUninstall();
        }
    };
    private View.OnClickListener settingClick = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            startActivity(new Intent(MainActivity.this, MainSetting.class));
        }
    };
}
