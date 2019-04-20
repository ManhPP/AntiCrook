package com.android.findmyandroid;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.HeterogeneousExpandableList;
import android.widget.Switch;
import android.widget.TextView;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_setting);

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
                return true;
            }
        });

//        switchButton = findViewById(R.id.switchbutton);
//        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Log.e(TAG, "onChangeSwitch: ");
//            }
//        });
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
