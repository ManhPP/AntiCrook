package com.android.anticrook.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.android.anticrook.MyDatabaseHelper;
import com.android.anticrook.OnSendEmailListener;

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

    //hàm thực hiện gửi email
    public void send(List<String> contentAndPath, boolean isDelteDB, String tableDelete) {
        //biến kiểm tra xem sau khi gủi xong có xóa db không, và xóa ở bảng nào
        this.isDeleteDB = isDelteDB;
        this.tableDelete = tableDelete;
        //lấy email và mật khẩu email sẽ gửi thông báo đi
        SharedPreferences sharedPreferences = context.getSharedPreferences("appSetting", Context.MODE_PRIVATE);
        final String email = sharedPreferences.getString("email", null);
        final String password = sharedPreferences.getString("password", null);

        String[] arrContentAndPath  = new String[contentAndPath.size()];
        for(int i=0; i<contentAndPath.size(); i++){
            arrContentAndPath[i] = contentAndPath.get(i);
        }

        this.email = email;
        //kiểm tra xem người dùng đã cài đặt email và mật khẩu gửi đi chưa
        if (email != null && password != null) {
            //cấu hình gửi mail qua giao thức smtp
            Properties props = new Properties();

            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            //tạo session để SMTP server xác thực tài khoản và gửi email
            session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(email, password);
                }
            });

            //khởi tạo một asynctask để bắt đàu gửi mail
            RetreiveFeedTask retreiveFeedTask = new RetreiveFeedTask();
            retreiveFeedTask.execute(arrContentAndPath);
        }
    }

    public class RetreiveFeedTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String...paths){
            try{

                SharedPreferences sharedPreferences = context.getSharedPreferences("appSetting", Context.MODE_PRIVATE);

                //khởi tạo một đối tượng message để gửi
                Message message = new MimeMessage(session);
                //khởi tạo địa chỉ email sẽ gửi
                message.setFrom(new InternetAddress(email));
                //khởi tạo danh sách email sẽ nhận thông báo
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(EmailHandler.this.getAllEmailReceive()));
                //khởi tạo subject cho thư thông báo
                message.setSubject("Ứng dụng chống trộm điện thoại: "+(new Date()).toString()
                        +" from user "+sharedPreferences.getString("username", "USER"));

                //tạo đối tượng chứa các MimeBodyPart chứa các file cần attach
                Multipart multipart = new MimeMultipart();
                //gán các đối tuonjg MimeBodyPart vào đối tượng multipart
                for(int i=1;i<paths.length; i++) {
                    //đối tượng MỉeBodyPart chứa một file được đính kèm
                    MimeBodyPart messageBodyPart = new MimeBodyPart();

                    //đính kèm file vào đối tuọng
                    String file = paths[i];
                    String fileName = paths[i];
                    DataSource source = new FileDataSource(file);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(fileName);
                    messageBodyPart.setContentID("<ARTHOS>");
                    messageBodyPart.setDisposition(MimeBodyPart.INLINE);
                    //add đối tượng MimeBodyPart vào đối tượng multipart
                    multipart.addBodyPart(messageBodyPart);

                }
                //Tạo đối tượng MimeBodyPart chứ nội dung email
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(paths[0]);
                //gán vào đối tượng MimeMessage
                multipart.addBodyPart(messageBodyPart);

                message.setContent(multipart);
                //bắt đầu gửi mail
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
                    //gọi hàm call back khi việc gửi mail đã xong
                    onSendEmailListener.onSendEmail();
                    Log.i("donesend", "onPostExecute: done send");
                }
            }
        }
    }

    //Đọc tất cả các email nhận thông báo
    public String getAllEmailReceive(){
        String mails="";
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
        Cursor cursor = myDatabaseHelper.getEmailReceive();

        cursor.moveToFirst();
        Log.i("email",cursor.getCount() +"");
        //duyệt cursor để lấy tất cả email nhận thông báo
        if(cursor .getCount() > 0){
            while(!cursor.isAfterLast()){
                mails += cursor.getString(1) + ",";
                Log.i("email",mails);
                cursor.moveToNext();
            }
        }

        return mails;
    }

    //set đôi tượng sẽ được gọi call back khi gửi thư xong
    public void setOnSendEmailListener(OnSendEmailListener onSendEmailListener){
        this.onSendEmailListener = onSendEmailListener;
    }
}
