package com.android.findmyandroid;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvSetting;
    private PolicyManager policyManager;
    Switch activate = null;
    private LinearLayout settingGroup;
    private LinearLayout uninstallApp;
    final int REQUEST_MULTIPLE_PERMISSIONS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    public boolean checkAndRequestPermission(){
        List<String> listPermissionDenied = new ArrayList<>();

        String[] permissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        for (String per: permissions) {
            if (ContextCompat.checkSelfPermission(this, per) != PackageManager.PERMISSION_GRANTED){
                listPermissionDenied.add(per);
            }
        }

        if(!listPermissionDenied.isEmpty()){
            ActivityCompat.requestPermissions(this, listPermissionDenied.toArray(new String[listPermissionDenied.size()]), REQUEST_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}
