package com.zz.demo.amap.util;

import android.app.Activity;
import android.app.Service;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import java.io.IOException;

/**
 * 手机震动工具类
 * Created by Administrator on 2017/7/25 .
 */

public class VibratorUtil {
    private static MediaPlayer mMediaPlayer;

    /**
     * final Activity activity ：调用该方法的Activity实例
     * long pattern ：震动的时长，单位是毫秒
     */
    public static void Vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    /**
     * <strong>@param </strong>activity 调用该方法的Activity实例
     * <strong>@param </strong>pattern long[] pattern ：自定义震动模式 。数组中数字的含义依次是[静止时长，震动时长，静止时长，震动时长。。。]时长的单位是毫秒
     * <strong>@param </strong>isRepeat 是否反复震动，如果是true，反复震动，如果是false，只震动一次
     */
    public static void Vibrate(final Activity activity, long[] pattern, boolean isRepeat) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }

    /**
     * 响铃代码
     *
     * @param activity
     */
    public static void MediaPlayer(final Activity activity) {
//        try {
        // 使用来电铃声的铃声路径
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(activity, uri);
        r.play();
//            // 如果为空，才构造，不为空，说明之前有构造过
//            if (mMediaPlayer == null)
//                mMediaPlayer = new MediaPlayer();
//            mMediaPlayer.setDataSource(activity, uri);

//            mMediaPlayer.setLooping(true); //循环播放
//            mMediaPlayer.prepare();
//            mMediaPlayer.start();

//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
