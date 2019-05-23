package com.android.findmyandroid.utils;

import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;

/**
 * Created by manhpp on 5/23/2019.
 */

public class RecordHandler {
    MediaRecorder mediaRecorder = null;
    String fileName = Environment.getExternalStorageDirectory() + "/myRecord"+System.currentTimeMillis()+".3gp";
    int isRecording = 0;
    Handler handler;
    int recordTime = 15;

    public void startRecording(){
        if(isRecording == 0){
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
                handler.post(stopRecording);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    Runnable stopRecording = new Runnable() {
        @Override
        public void run() {
            if(isRecording == 1){
                handler.postDelayed(this, recordTime * 1000);
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                isRecording = 0;
            }
        }
    };
}
