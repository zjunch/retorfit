package com.creativearts.ymt.utils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.creativearts.common.interface_common.FunctionManager;
import com.creativearts.ymt.R;
import com.mobanker.eagleeye.utils.PreferencesUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

public class UpdateService extends IntentService {

    private Context mContext;
    private String apkUrl;
    private NotificationUtils notificationUtils;
    private File downapkfile;
    private  boolean isSlience;    //0为更新，1为下载还款出现的app
    Callback.Cancelable cancelable;

    public UpdateService() {
        super("UpdateService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            isSlience = intent.getBooleanExtra("isSlience",false);
            notificationUtils =  NotificationUtils.getInstance(getApplicationContext());
            apkUrl = intent.getStringExtra("apkUrl");
            createNotification();
            downApk(apkUrl);
        }
    }

    private void createNotification() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_notifaction);
        if(isSlience){
            remoteViews.setViewVisibility(R.id.notify_download_iv, View.GONE);
        }else {
            remoteViews.setViewVisibility(R.id.notify_download_iv, View.VISIBLE);
        }
        remoteViews.setTextViewText(R.id.notify_tv, "正在下载...");
        remoteViews.setProgressBar(R.id.notify_progress_pb, 100, 0, false);
        remoteViews.setTextViewText(R.id.notify_progress_tv, "0%");
        if(isSlience){
            notificationUtils.sendNotification( 1,"", "", remoteViews, null);
        }else{
            notificationUtils.sendNotification( 0,"", "", remoteViews, null);
        }
    }


    private void downApk(String apkUrl) {
        setPresValue(true);
        RequestParams params = new RequestParams(apkUrl);
        //断点下载
        params.setAutoRename(true);
        File appCacheDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ymtApp");
        if (!appCacheDir.exists()) {
            appCacheDir.mkdirs();
        }
        params.setSaveFilePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ymtApp/app_" + System.currentTimeMillis() + ".apk");
        cancelable = x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File result) {
                // 通知下载完成
                downapkfile = result;
                sendMessage(1, 100);
                setPresValue(false);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("zjun", "onError");
                setPresValue(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e("zjun", "onCancelled");
                setPresValue(false);
            }

            @Override
            public void onFinished() {
                Log.e("zjun", "onFinished");
                setPresValue(false);
            }

            @Override
            public void onWaiting() {
            }

            @Override
            public void onStarted() {
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                int progress = (int) (current * 100 / total);
                setPresValue(true);
                sendMessage(0, progress);
            }
        });

    }


    private  void setPresValue(boolean bl){
        if(isSlience){
            PreferencesUtils.putBoolean(mContext,"isSilenceDowning",bl);
        }else{
            PreferencesUtils.putBoolean(mContext, "isDownLoading", bl);
        }
    }


    public void sendMessage(int what, int mprogress) {
        Message msg0 = mHandler.obtainMessage();
        msg0.what = what;
        msg0.arg1 = mprogress;
        mHandler.sendMessage(msg0);
    }






    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //正在下载中

                        RemoteViews contentView = notificationUtils.getNotification().contentView;
                        contentView.setTextViewText(R.id.notify_tv, "更新包下载中...");
                        contentView.setProgressBar(R.id.notify_progress_pb, 100, msg.arg1, false);
                        contentView.setTextViewText(R.id.notify_progress_tv, msg.arg1 + "%");
                        // 更新UI
                        if(isSlience){
                            notificationUtils.notifyNotifaction(1);
                        }else{
                            notificationUtils.notifyNotifaction(0);
                        }
                    break;
                case 1:
                    if(isSlience){
                        notificationUtils.cancelNotification(1);
                        FunctionManager.getInstance().invokeOnlyPramFunc("silence_downloadFinish", downapkfile);
                    }else{
                        notificationUtils.cancelNotification(0);
                        FunctionManager.getInstance().invokeOnlyPramFunc("downloadFinish", downapkfile);
                    }

                    break;
            }
            return true;
        }
    });

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
