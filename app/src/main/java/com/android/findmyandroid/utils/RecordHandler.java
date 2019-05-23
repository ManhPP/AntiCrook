package com.android.findmyandroid.utils;

import android.media.MediaRecorder;
import android.os.Handler;

/**
 * Created by manhpp on 5/23/2019.
 */

public class RecordHandler {
    MediaRecorder mediaRecorder = null;
    String fileName = null;
    int isRecording = 0;
    Handler handle;
    int timeRecord;
}
