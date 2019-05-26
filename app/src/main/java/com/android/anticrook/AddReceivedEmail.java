package com.android.anticrook;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.anticrook.model.EmailReceive;

public class AddReceivedEmail extends AppCompatActivity {
    private Cursor lstEmails = null;
    private ListView lstEmailView = null;
    private FloatingActionButton addEmailFAB = null;
    private CoordinatorLayout addReceivedEmailView = null;
    private PopupWindow popupWindow = null;
    private Button addFilledEmailButton = null;
    private Button dismissPopupButton = null;
    private EditText filledEmail = null;
    private EmailAdapter adapter = null;
    private TextView confirmDeletingEmail = null;
    private Button deleteEmail = null;
    private Button cancelDeletingEmail = null;
    private TextView note = null;
    MyDatabaseHelper helper = null;
    EmailReceive emailReceive = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_received_email);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add email is notified");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBarColor)));
        TypedArray styledAttributes = getTheme().obtainStyledAttributes(new int[] {android.R.attr.actionBarSize});
        int actionBarSize = (int) styledAttributes.getDimension(0,0);
        styledAttributes.recycle();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        helper = new MyDatabaseHelper(this);
        lstEmails = helper.getEmailReceive();
//        startManagingCursor(lstEmails);
        lstEmailView = findViewById(R.id.lstReceivedEmail);
        addEmailFAB = findViewById(R.id.addEmailFAB);
        addReceivedEmailView = findViewById(R.id.addReceivedEmail);
        note = findViewById(R.id.note);
        if (lstEmails.getCount() == 0){
            note.setText("No email in the list yet!");
        }
        adapter = new EmailAdapter(lstEmails);
        lstEmailView.setAdapter(adapter);

        lstEmailView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                Toast.makeText(AddReceivedEmail.this, "hehehe", Toast.LENGTH_SHORT).show();
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View deleteEmailView = inflater.inflate(R.layout.delete_email, null);
                popupWindow = new PopupWindow(deleteEmailView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lstEmails.moveToPosition(position);
                String msg = "Delete \"" + lstEmails.getString(1) + "\"?";
                confirmDeletingEmail = deleteEmailView.findViewById(R.id.confirmDeletingEmail);
                deleteEmail = deleteEmailView.findViewById(R.id.deleteEmail);
                cancelDeletingEmail = deleteEmailView.findViewById(R.id.cancelDeletingEmail);

                confirmDeletingEmail.setText(msg);

                deleteEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String msg = "Deleted email \"" + lstEmails.getString(1) + "\"!";
                        int[] id = {lstEmails.getInt(0)};
                        helper.deleteEmailReceive(id);
                        lstEmails = helper.getEmailReceive();
                        adapter.changeCursor(lstEmails);
                        popupWindow.dismiss();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        if (lstEmails.getCount() == 0){
                            note.setText("No email in the list yet!");
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
                            Toast.makeText(getApplicationContext(), "Please enter email!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            emailReceive = new EmailReceive();
                            emailReceive.setEmail(email);
                            boolean ret = helper.addEmailReceive(emailReceive);
                            if(ret) {
                                lstEmails = helper.getEmailReceive();
                                adapter.changeCursor(lstEmails);

                                popupWindow.dismiss();
                                Toast.makeText(getApplicationContext(), "Added email!", Toast.LENGTH_SHORT).show();
                                if (lstEmails.getCount() > 0) {
                                    note.setText("Email list is notified");
                                }
                            }else{
                                Toast.makeText(getApplicationContext(), "Email already exists!", Toast.LENGTH_SHORT).show();
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
//    @Override
//    protected void onStart(){
//        super.onStart();
//        helper = new MyDatabaseHelper(this);
//        lstEmails = helper.getEmailReceive();
//        adapter.changeCursor(lstEmails);
//    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        helper.close();
    }

//    protected void onResume() {
//        // TODO Auto-generated method stub
//        super.onResume();
//        Log.i("log","onresume");
//        helper = new MyDatabaseHelper(this);
//        lstEmails = helper.getEmailReceive();
//        adapter.changeCursor(lstEmails);
//
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
//                Toast.makeText(this, "bam quay lai", Toast.LENGTH_SHORT ).show();
                onBackPressed();

                return true;

            default:break;
        }

        return super.onOptionsItemSelected(item);
    }
    public class EmailAdapter extends CursorAdapter{
        EmailAdapter(Cursor c){
            super(AddReceivedEmail.this, c, true);
        }
        @Override
        public void bindView(View row, Context ctxt,
                             Cursor c) {
            EmailHolder holder = (EmailHolder)row.getTag();
            holder.populateFrom(c, c.getPosition());
        }
        @Override
        public View newView(Context ctxt, Cursor c,
                            ViewGroup parent) {
            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.email_row, parent, false);
            EmailHolder holder=new EmailHolder(row);
            row.setTag(holder);
            return(row);
        }
    }
    static class EmailHolder {
        private TextView email = null;

        EmailHolder(View row) {
            email = (TextView) row.findViewById(R.id.emailrow);
        }

        void populateFrom(Cursor c, int no) {
            email.setText(no+1 + ". " +c.getString(1));
        }
    }
}
