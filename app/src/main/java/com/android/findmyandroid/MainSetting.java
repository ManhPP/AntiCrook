package com.android.findmyandroid;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.HeterogeneousExpandableList;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainSetting extends AppCompatActivity {
    private static final String TAG = "MainSetting";
    private ExpandableListView features = null;
    private CustomExpandableListAdapter adapter = null;
    private Switch switchButton = null;
    private List<String> listHeader = null;
    private HashMap<String, List<String>> mData = null;
    SharedPreferences sharedPreferences = null;

    //===

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_setting);
        sharedPreferences = MainSetting.this.getSharedPreferences("appSetting", Context.MODE_PRIVATE);
        listHeader = new ArrayList<>();
        listHeader.add("Thiết Lập SMS");
        listHeader.add("Thiết Lập Email");
        listHeader.add("Thiết Lập Hành Động Khi Đổi Sim");

        mData = new HashMap<>();
        List<String> smsMenu = new ArrayList<>();
        List<String> emailMenu = new ArrayList<>();
        List<String> simMenu = new ArrayList<>();

        mData.put(listHeader.get(0), smsMenu);
        mData.put(listHeader.get(1), emailMenu);
        mData.put(listHeader.get(2), simMenu);

        smsMenu.add("Thiết lập lệnh SMS");
        emailMenu.add("Thay đổi email gửi thông báo");
        emailMenu.add("Thêm email nhận thông báo");
        simMenu.add("Chụp ảnh camera trước");
        simMenu.add("Chụp ảnh camera sau");
        simMenu.add("Ghi âm");
        simMenu.add("Lấy tọa độ");
        simMenu.add("Đọc danh bạ");
        simMenu.add("Đọc tin nhắn");

        features = (ExpandableListView) findViewById(R.id.features);
        adapter = new CustomExpandableListAdapter(this, listHeader, mData);
        features.setAdapter(adapter);
//        features.setGroupIndicator(null);
        features.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.e(TAG, "onChildClick: " + listHeader.get(groupPosition) + ", " + mData.get(listHeader.get(groupPosition)).get(childPosition));
                return false;
            }
        });
        features.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Log.e(TAG, "onGroupClick: " + groupPosition);
                return false;
            }
        });
        features.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Log.e(TAG, "onGroupCollapse: " + groupPosition);
            }
        });
        features.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Log.e(TAG, "onGroupExpand: " + groupPosition);
            }
        });
        features.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.e(TAG, "onChildClick: " + childPosition + " in group: " + groupPosition);
                if(groupPosition == 0 ){
                    startActivity(new Intent(MainSetting.this, SMSSettingActivity.class));
                }else if(groupPosition == 1){
                    if(childPosition == 0){
                        startActivity(new Intent(MainSetting.this, EmailSendActivity.class));
                    }else{
                        startActivity(new Intent(MainSetting.this, AddReceivedEmail.class));
                    }
                }
                return true;
            }
        });
        features.expandGroup(0);
        features.expandGroup(1);
        features.expandGroup(2);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Cài đặt chính");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarColor)));
        TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[] {android.R.attr.actionBarSize});
        int actionBarSize = (int) styledAttributes.getDimension(0,0);
        styledAttributes.recycle();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        Drawable drawable = getResources().getDrawable(R.drawable.back);
//        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//        Drawable newDrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, actionBarSize, actionBarSize, true));
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setLogo(R.drawable.back);
//        actionBar.setDisplayUseLogoEnabled(true);
//        switchButton = findViewById(R.id.switchbutton);
//        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Log.e(TAG, "onChangeSwitch: ");
//            }
//        });
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
    public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
        private static final String TAG = "ExpandableListAdapter";
        private Context mContext;
        private List<String> mHeaderGroup;
        private HashMap<String, List<String>> mDataChild;

        public CustomExpandableListAdapter(Context context, List<String> headerGroup, HashMap<String, List<String>> datas){
            this.mContext = context;
            this.mHeaderGroup = headerGroup;
            this.mDataChild = datas;
        }

        public Context getmContext() {
            return mContext;
        }

        public void setmContext(Context mContext) {
            this.mContext = mContext;
        }

        public List<String> getmHeaderGroup() {
            return mHeaderGroup;
        }

        public void setmHeaderGroup(List<String> mHeaderGroup) {
            this.mHeaderGroup = mHeaderGroup;
        }

        public HashMap<String, List<String>> getmDataChild() {
            return mDataChild;
        }

        public void setmDataChild(HashMap<String, List<String>> mDataChild) {
            this.mDataChild = mDataChild;
        }

        public int getGroupCount(){
            return this.mHeaderGroup.size();
        }
        public int getChildrenCount(int groupPosition){
            return this.mDataChild.get(mHeaderGroup.get(groupPosition)).size();
        }
        public Object getGroup(int groupPosition){
            return this.mHeaderGroup.get(groupPosition);
        }
        public Object getChild(int groupPosition, int childPosition){
            return this.mDataChild.get(mHeaderGroup.get(groupPosition)).get(childPosition);
        }
        public long getGroupId(int groupPosition){
            return groupPosition;
        }
        public long getChildId(int groupPosition, int childPosition){
            return childPosition * groupPosition;
        }
        public boolean hasStableIds(){
            return true;
        }
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent){
            if (convertView == null){
                LayoutInflater inflater = LayoutInflater.from(this.mContext);
                convertView = inflater.inflate(R.layout.list_group, parent, false);
            }
            TextView listTitle = (TextView) convertView.findViewById(R.id.listTitle);
            listTitle.setText(this.mHeaderGroup.get(groupPosition));

            return convertView;
        }
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
            int itemType = getChildType(groupPosition,childPosition);
            if (convertView == null){
                LayoutInflater inflater = LayoutInflater.from(mContext);
                switch (itemType){
                    case 0:
                        convertView = inflater.inflate(R.layout.list_item_select, parent, false);
                        break;
                    case 1:
                        convertView = inflater.inflate(R.layout.list_item_switch, parent, false);
                        switchButton = convertView.findViewById(R.id.switchbutton);
                        switch(childPosition){
                            case 0:
                                if(sharedPreferences.getBoolean("frontCam",false)){
                                    switchButton.setChecked(true);
                                }
                                break;
                            case 1:
                                if(sharedPreferences.getBoolean("behindCam",false)){
                                    switchButton.setChecked(true);
                                }
                                break;
                            case 2:
                                if(sharedPreferences.getBoolean("record",false)){
                                    switchButton.setChecked(true);
                                }
                                break;
                            case 3:
                                if(sharedPreferences.getBoolean("locate",false)){
                                    switchButton.setChecked(true);
                                }
                                break;
                            case 4:
                                if(sharedPreferences.getBoolean("readContact",false)){
                                    switchButton.setChecked(true);
                                }
                                break;
                            case 5:
                                if(sharedPreferences.getBoolean("readSMS",false)){
                                    switchButton.setChecked(true);
                                }
                                break;
                        }
                        break;
                }

            }
            TextView listItem = null;
            switch (itemType){
                case 0:
                    listItem = (TextView) convertView.findViewById(R.id.expandedListItemSelect);
                    break;
                case 1:
                    listItem = (TextView) convertView.findViewById(R.id.expandedListItemSwitch);
                    switchButton = convertView.findViewById(R.id.switchbutton);
                    switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Log.e(TAG, "onChangeSwitch: " + childPosition + " in group: " + groupPosition);
                            if(isChecked == true){
                                switch (childPosition){
                                    case 0:
                                        sharedPreferences.edit().putBoolean("frontCam", true).apply();
                                        Log.i("switch","bat cam truoc");
                                        break;
                                    case 1:
                                        sharedPreferences.edit().putBoolean("behindCam", true).apply();
                                        break;
                                    case 2:
                                        sharedPreferences.edit().putBoolean("record", true).apply();
                                        break;
                                    case 3:
                                        sharedPreferences.edit().putBoolean("locate", true).apply();
                                        break;
                                    case 4:
                                        sharedPreferences.edit().putBoolean("readContact", true).apply();
                                        break;
                                    case 5:
                                        sharedPreferences.edit().putBoolean("readSMS", true).apply();
                                        break;
                                }
                            }
                            else {
                                switch (childPosition){
                                    case 0:
                                        sharedPreferences.edit().putBoolean("fontCam", false).apply();
                                        Log.i("switch","tat cam truoc");
                                        break;
                                    case 1:
                                        sharedPreferences.edit().putBoolean("behindCam", false).apply();
                                        break;
                                    case 2:
                                        sharedPreferences.edit().putBoolean("record", false).apply();
                                        break;
                                    case 3:
                                        sharedPreferences.edit().putBoolean("locate", false).apply();
                                        break;
                                    case 4:
                                        sharedPreferences.edit().putBoolean("readContact", false).apply();
                                        break;
                                    case 5:
                                        sharedPreferences.edit().putBoolean("readSMS", false).apply();
                                        break;
                                }
                            }

                        }
                    });
                    break;
            }
            listItem.setText((String) this.getChild(groupPosition, childPosition));

            return convertView;
        }
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public int getChildType(int groupPosition, int childPosition) {
            if (groupPosition == 2){
                return 1;
            }
            else{
                return 0;
            }
        }

        @Override
        public int getChildTypeCount() {
            return 2;
        }

        @Override
        public int getGroupType(int groupPosition) {
            return 0;
        }

        @Override
        public int getGroupTypeCount() {
            return 1;
        }
    }
}
