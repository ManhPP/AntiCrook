package com.android.findmyandroid;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
/**
 * Created by manhpp on 5/21/2019.
 */

public class SampleDeviceAdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onDisabled(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Toast.makeText(context, "Disabled", Toast.LENGTH_SHORT).show();
        super.onDisabled(context, intent);
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Toast.makeText(context, "Activated", Toast.LENGTH_SHORT).show();
        super.onEnabled(context, intent);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Toast.makeText(context, "Disabled", Toast.LENGTH_SHORT).show();
        return super.onDisableRequested(context, intent);
    }
}
