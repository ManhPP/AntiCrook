package com.android.findmyandroid.utils;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.android.findmyandroid.OnReceiveRecordListener;
import com.android.findmyandroid.model.Record;

import java.util.Date;

/**
 * Created by manhpp on 5/23/2019.
 */

public class RecordHandler extends Activity {
    MediaRecorder mediaRecorder = null;
    String fileName = null;
    int isRecording = 0;
    int recordTime = 15;
    private OnReceiveRecordListener onReceiveRecordListener;

    public void startRecording(){
        if(isRecording == 0){
            fileName = getApplicationContext().getExternalFilesDir("Record")+ "/record"+System.currentTimeMillis()+".3gp";
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(fileName);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            recordTime = 0;
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                isRecording = 1;
                new Thread(stopRecording).start();
                Record record = new Record(fileName, (new Date()).toString());

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    Runnable stopRecording = new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(recordTime);
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    isRecording = 0;
                }
            });
        }
    };
    public void setOnReceiveRecordListener(OnReceiveRecordListener onReceiveRecordListener){
        this.onReceiveRecordListener = onReceiveRecordListener;
        startRecording();
    }
}
