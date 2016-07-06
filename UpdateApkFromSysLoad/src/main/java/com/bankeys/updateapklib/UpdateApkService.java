package com.bankeys.updateapklib;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

//http://gdown.baidu.com/data/wisegame/2eeee3831c9dbc42/QQ_374.apk
public class UpdateApkService extends Service {
    private Context mContext;
    private String loadApkPath = "http://gdown.baidu.com/data/wisegame/2eeee3831c9dbc42/QQ_374.apk";
    private String fileName = "QQ_374.apk";
    private long taskId;
    private static final String TAG = "UpdateApkService";
    private DownloadManager mDownloadManager;
    private UpdateApkReceiver mUpdateApkReceiver;
//    public UpdateApkService(Context mContext,String loadApkPath,String storePath) {
//        this.mContext = mContext;
//        this.loadApkPath = loadApkPath;
//        this.fileName = storePath;
//    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        updateApk();
        mUpdateApkReceiver = new UpdateApkReceiver();
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(mUpdateApkReceiver, filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUpdateApkReceiver != null) {
            unregisterReceiver(mUpdateApkReceiver);
        }
    }

    private void updateApk() {
        Log.i(TAG, "path>>" + loadApkPath + "\n" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + fileName);
        //传入  下载地址
        DownloadManager.Request mRequest = new DownloadManager.Request(Uri.parse(loadApkPath));
        //传入  文件存储地址，也可以自定义存储位置
        mRequest.setDestinationInExternalPublicDir("/download/", fileName);
        mRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        taskId = mDownloadManager.enqueue(mRequest);
    }

    private void checkDownLoadStatus() {
        DownloadManager.Query mQuery = new DownloadManager.Query();
        mQuery.setFilterById(taskId);
        Cursor c = mDownloadManager.query(mQuery);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            Log.i(TAG, "status>>>>" + status);
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    break;
                case DownloadManager.STATUS_FAILED:
                    break;
                case DownloadManager.STATUS_PENDING:
                    break;
                case DownloadManager.STATUS_RUNNING:
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    installApk();
                    break;
            }
        }
    }

    private void installApk() {
        String downPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + fileName;
        File file = new File(downPath);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        stopSelf();
    }

    private class UpdateApkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkDownLoadStatus();
        }
    }

}
