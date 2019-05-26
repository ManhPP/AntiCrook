package com.android.findmyandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.android.findmyandroid.MyDatabaseHelper;
import com.android.findmyandroid.OnSendEmailListener;
import com.android.findmyandroid.model.EmailReceive;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by manhpp on 5/23/2019.
 */

public class EmailHandler {

    private Session session;
    private String email;
    private Context context;
    private boolean isDeleteDB = false;
    private String tableDelete;
    private MyDatabaseHelper myDatabaseHelper;
    private OnSendEmailListener onSendEmailListener;

    public EmailHandler(Context context) {
        myDatabaseHelper = new MyDatabaseHelper(context);
        this.context = context;
    }

    public void send(List<String> contentAndPath, boolean isDelteDB, String tableDelete) {
        this.isDeleteDB = isDelteDB;
        this.tableDelete = tableDelete;
        SharedPreferences sharedPreferences = context.getSharedPreferences("appSetting", Context.MODE_PRIVATE);
        final String email = sharedPreferences.getString("email", null);
        final String password = sharedPreferences.getString("password", null);
        String[] arrContentAndPath  = new String[contentAndPath.size()];
        for(int i=0; i<contentAndPath.size(); i++){
            arrContentAndPath[i] = contentAndPath.get(i);
        }

        this.email = email;

        Log.i("emailsend", "send: "+email+"-"+password);
        if (email != null && password != null) {
            Properties props = new Properties();

            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(email, password);
                }
            });

            RetreiveFeedTask retreiveFeedTask = new RetreiveFeedTask();
            retreiveFeedTask.execute(arrContentAndPath);
        }
    }

    public class RetreiveFeedTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String...paths){
            try{

                SharedPreferences sharedPreferences = context.getSharedPreferences("appSetting", Context.MODE_PRIVATE);

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(email));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(EmailHandler.this.getAllEmailReceive()));
                message.setSubject("Ứng dụng chống trộm điện thoại: "+(new Date()).toString()
                        +" from user "+sharedPreferences.getString("username", "USER"));


                Multipart multipart = new MimeMultipart();
                for(int i=1;i<paths.length; i++) {
                    MimeBodyPart messageBodyPart = new MimeBodyPart();

                    String file = paths[i];
                    String fileName = paths[i];
                    DataSource source = new FileDataSource(file);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(fileName);
                    messageBodyPart.setContentID("<ARTHOS>");
                    messageBodyPart.setDisposition(MimeBodyPart.INLINE);
                    multipart.addBodyPart(messageBodyPart);

                }
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(paths[0]);
                multipart.addBodyPart(messageBodyPart);

                message.setContent(multipart);
                Transport.send(message);
                Log.i("sendemail", "doInBackground: send done");
                return true;
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isError) {
            //xoa db neu day la gui cac du lieu dc luu lai do la truoc k gui dc
            if(isDeleteDB){
                if(!isError){
                    switch (tableDelete){
                        case "contact":
                            myDatabaseHelper.deleteContact();
                            break;
                        case "location":
                            myDatabaseHelper.deleteLocate();
                            break;
                        case "image":
                            myDatabaseHelper.deleteImage();
                            break;
                        case "record":
                            myDatabaseHelper.deleteRecord();
                            break;
                        case "sms":
                            myDatabaseHelper.deleteSMS();
                            break;
                    }
                }
            }else{
                if (onSendEmailListener!=null) {
                    onSendEmailListener.onSendEmail();
                    Log.i("donesend", "onPostExecute: done send");
                }
            }
        }
    }

    //doc tat ca cac email nhan thong bao
    public String getAllEmailReceive(){
        String mails="";
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
        Cursor cursor = myDatabaseHelper.getEmailReceive();

        cursor.moveToFirst();
        Log.i("email",cursor.getCount() +"");
        if(cursor .getCount() > 0){
            while(!cursor.isAfterLast()){
//                if(email.equals("")){
//                    mails += cursor.getString(1);
//                }else{
//                    mails+= ", "+cursor.getString(1);
//                }

                mails += cursor.getString(1) + ",";
                Log.i("email",mails);
                cursor.moveToNext();
            }
        }

//        for(EmailReceive email : listMail){
//            if(email.getEmail().equals("")){
//                mails += email.getEmail();
//            }else{
//                mails+= ", "+email.getEmail();
//            }
//        }

        return mails;
    }

    //set doi tuong se duoc goi call back khi gui email xong
    public void setOnSendEmailListener(OnSendEmailListener onSendEmailListener){
        this.onSendEmailListener = onSendEmailListener;
    }
}
