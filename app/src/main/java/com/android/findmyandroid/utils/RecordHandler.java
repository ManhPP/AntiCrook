package com.android.findmyandroid.utils;

import android.app.Activity;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.AsyncTask;
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
    DoRecordingInBackground doRecordingInBackground = null;

    public RecordHandler(Context context){
        this.context = context;
    }
    public void record(int time){
        doRecordingInBackground = new DoRecordingInBackground();
        doRecordingInBackground.execute(time);
//        startRecording();
//        new CountDownTimer(time*1000, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//            }
//
//            public void onFinish() {
//                stopRecording();
//            }
//        }.start();
    }
    public void startRecording(){
        Log.i("xxxxxx", "startRecording: recodingg");
        if(isRecording == 0){
            fileName = this.context.getApplicationContext().getExternalFilesDir("Record")+ "/record"+System.currentTimeMillis()+".3gp";
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(fileName);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setMaxDuration(3000);
//            recordTime = 0;
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                isRecording = 1;

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void setOnReceiveRecordListener(OnReceiveRecordListener onReceiveRecordListener){
        this.onReceiveRecordListener = onReceiveRecordListener;
        this.record(recordTime);
    }

    public class DoRecordingInBackground extends AsyncTask<Integer, Void, Record>{
        @Override
        protected Record doInBackground(Integer... params) {
            startRecording();
            mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
                        Log.i("onRecord", "ghi am du tg");
                        mediaRecorder.stop();
                        mediaRecorder.release();
                        mediaRecorder = null;
                        isRecording = 0;
                        record = new Record(fileName, (new Date()).toString());
                        onReceiveRecordListener.onReceiveRecord(record);
                        Log.i("xxxxx", "onInfo: done recode, call back called");
                    }
                }
            });
            return record;
        }
    }
}
