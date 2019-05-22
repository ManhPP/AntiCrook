package com.android.findmyandroid;

import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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

public class MainActivity extends AppCompatActivity {

    private TextView tvSetting;
    private PolicyManager policyManager;
    Switch activate = null;
    private LinearLayout settingGroup;
    private LinearLayout uninstallApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        policyManager = new PolicyManager(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Chống trộm");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarColor)));
//        TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[] {android.R.attr.actionBarSize});
//        int actionBarSize = (int) styledAttributes.getDimension(0,0);
//        styledAttributes.recycle();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activate = findViewById(R.id.activate_button);
        activate.setOnCheckedChangeListener(onActivate);
        uninstallApp = findViewById(R.id.uninstall_app);
        uninstallApp.setOnClickListener(onUninstall);
        init();


//        startActivity(new Intent(this, EmailSendActivity.class));

//        startActivity(new Intent(this, AddReceivedEmail.class));
//        startActivity(new Intent(this, MainSetting.class));
//        startActivity(new Intent(this, SMSSettingActivity.class));

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
}
