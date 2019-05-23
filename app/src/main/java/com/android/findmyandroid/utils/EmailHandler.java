package com.android.findmyandroid.utils;

import android.os.AsyncTask;

import java.util.ArrayList;
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
    public Session session;

    public void send(){
        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("email gui","password");
            }
        });

        RetreiveFeedTask retreiveFeedTask = new RetreiveFeedTask();
        retreiveFeedTask.execute();
    }

    public class RetreiveFeedTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void...voids){
            ArrayList<String> paths = new ArrayList<>();
            paths.add("/storage/emulated/0/Android/data/com.example.tranv.myapplication/cache:IMG_1555230529540.jpeg");
            paths.add("/storage/emulated/0/Android/data/com.example.tranv.myapplication/cache:IMG_1555230536459.jpeg");

            try{
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("email gui"));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse("email nhan"));
                message.setSubject("12342345234");
                message.setText("sđfe fwe ews");

                MimeBodyPart messageBodyPart = new MimeBodyPart();

                Multipart multipart = new MimeMultipart();

                messageBodyPart = new MimeBodyPart();

                for(int i=0;i<paths.size(); i++) {
                    String file = paths.get(i);
                    String fileName = "attachmentName" + i;
                    DataSource source = new FileDataSource(file);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(fileName);
                    multipart.addBodyPart(messageBodyPart);

                    message.setContent(multipart);

                    Transport.send(message);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
}
