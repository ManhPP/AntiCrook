package com.android.findmyandroid.utils;

import android.app.Activity;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
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
    Context context;
    MediaRecorder mediaRecorder = null;
    String fileName = null;
    int isRecording = 0;
    int recordTime = 1;
    Record record = null;
    private OnReceiveRecordListener onReceiveRecordListener;
    public RecordHandler(Context context){
        this.context = context;
    }
    public void record(int time){
        startRecording();
        new CountDownTimer(time*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                stopRecording();
            }
        }.start();
    }
    public void startRecording(){
        if(isRecording == 0){
            fileName = this.context.getApplicationContext().getExternalFilesDir("Record")+ "/record"+System.currentTimeMillis()+".3gp";
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
                record = new Record(fileName, (new Date()).toString());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void stopRecording(){
        if(isRecording == 1){
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = 0;
            Log.i("onReceived","ghi am xong");
            onReceiveRecordListener.onReceiveRecord(record);
        }
    }

    public void setOnReceiveRecordListener(OnReceiveRecordListener onReceiveRecordListener){
        this.onReceiveRecordListener = onReceiveRecordListener;
        this.record(recordTime);
    }
}
