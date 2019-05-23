package com.android.findmyandroid.utils;

import android.media.MediaRecorder;
import android.os.Handler;

/**
 * Created by manhpp on 5/23/2019.
 */

public class RecordHandler {
    MediaRecorder mediaRecorder = null;
    String fileName = null;
    Handler handler;
    int recordTime = 0;
    int isRecording = 0;
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
        }
    }
    Runnable stopRecord = new Runnable() {
        @Override
        public void run() {
            if(isRecording == 1){
                recordTime += 1;
                handler.postDelayed(this, 1000);
            }
        }
    };
}
