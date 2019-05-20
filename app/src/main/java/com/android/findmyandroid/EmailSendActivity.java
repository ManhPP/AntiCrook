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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EmailSendActivity extends AppCompatActivity {

    private TextView tvName;
    private EditText username, password;
    private ImageView ava, icoEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_send);
        initView();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Email gửi");
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
        tvName = findViewById(R.id.tvName);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        ava = findViewById(R.id.ava);
        icoEdit = findViewById(R.id.icoEdit);

        icoEdit.setOnClickListener(onClickItem);


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
                case R.id.icoEdit:
                    initDialog(tvName, "Thay đổi tên");
                    break;
            }
        }
    };
}