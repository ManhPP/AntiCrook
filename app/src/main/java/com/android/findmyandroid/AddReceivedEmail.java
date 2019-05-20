package com.android.findmyandroid;

import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddReceivedEmail extends AppCompatActivity {
    private List<String> lstEmails = null;
    private ListView lstEmailView = null;
    private FloatingActionButton addEmailFAB = null;
    private CoordinatorLayout addReceivedEmailView = null;
    private PopupWindow popupWindow = null;
    private Button addFilledEmailButton = null;
    private Button dismissPopupButton = null;
    private EditText filledEmail = null;
    private ArrayAdapter adapter = null;
    private TextView confirmDeletingEmail = null;
    private Button deleteEmail = null;
    private Button cancelDeletingEmail = null;
    private TextView note = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_received_email);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Thêm Email nhận thông báo");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarColor)));
        TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[] {android.R.attr.actionBarSize});
        int actionBarSize = (int) styledAttributes.getDimension(0,0);
        styledAttributes.recycle();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lstEmails = new ArrayList<>();
        lstEmailView = findViewById(R.id.lstReceivedEmail);
        addEmailFAB = findViewById(R.id.addEmailFAB);
        addReceivedEmailView = findViewById(R.id.addReceivedEmail);
        note = findViewById(R.id.note);
        if (lstEmails.size() == 0){
            note.setText("Chưa có email nào trong danh sách!");
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstEmails);
        lstEmailView.setAdapter(adapter);

        lstEmailView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View deleteEmailView = inflater.inflate(R.layout.delete_email, null);
                popupWindow = new PopupWindow(deleteEmailView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                String msg = "Xóa email \"" + lstEmails.get(position) + "\" khỏi danh sách?";
                confirmDeletingEmail = deleteEmailView.findViewById(R.id.confirmDeletingEmail);
                deleteEmail = deleteEmailView.findViewById(R.id.deleteEmail);
                cancelDeletingEmail = deleteEmailView.findViewById(R.id.cancelDeletingEmail);

                confirmDeletingEmail.setText(msg);

                deleteEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String msg = "Đã xóa email \"" + lstEmails.get(position) + "\"!";
                        adapter.remove(adapter.getItem(position));
                        popupWindow.dismiss();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        if (lstEmails.size() == 0){
                            note.setText("Chưa có email nào trong danh sách!");
                        }
                    }
                });
                cancelDeletingEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                addEmailFAB.setVisibility(View.GONE);
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        addEmailFAB.setVisibility(View.VISIBLE);
                    }
                });
                popupWindow.setFocusable(true);
                popupWindow.setTouchable(true);
                popupWindow.update();
                popupWindow.showAtLocation(addReceivedEmailView, Gravity.CENTER, 0, 0);
            }
        });
        addEmailFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View fillEmailView = inflater.inflate(R.layout.activity_fill_email, null);
                popupWindow = new PopupWindow(fillEmailView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                addFilledEmailButton =  fillEmailView.findViewById(R.id.addFilledEmail);
                dismissPopupButton = fillEmailView.findViewById(R.id.dissmisPopup);
                filledEmail = fillEmailView.findViewById(R.id.filledEmail);

                addFilledEmailButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = filledEmail.getText().toString().trim();
                        if (email.length() == 0){
                            //TODO:
                            Toast.makeText(getApplicationContext(), "Chưa nhập email!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            adapter.add(email);
                            popupWindow.dismiss();
                            Toast.makeText(getApplicationContext(), "Đã thêm email!", Toast.LENGTH_SHORT).show();
                            if (lstEmails.size() > 0){
                                note.setText("Danh sách email được nhận thông báo: ");
                            }
                        }

                    }
                });
                dismissPopupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

//                if(Build.VERSION.SDK_INT>=21){
//                    popupWindow.setElevation(30.0f);
//                }
                addEmailFAB.setVisibility(View.GONE);
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        addEmailFAB.setVisibility(View.VISIBLE);
                    }
                });
                popupWindow.setFocusable(true);
                popupWindow.setTouchable(true);
                popupWindow.update();
                popupWindow.showAtLocation(addReceivedEmailView, Gravity.CENTER, 0, 0);
            }
        });
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
}
