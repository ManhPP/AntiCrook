package com.android.findmyandroid;

import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView tvSetting;
    private PolicyManager policyManager;
    Switch activate = null;
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
    private View.OnClickListener settingClick = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            startActivity(new Intent(MainActivity.this, MainSetting.class));
        }
    };
}
