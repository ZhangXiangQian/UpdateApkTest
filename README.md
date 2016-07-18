# UpdateApkTest
通过系统工具下载APK

除了我们自己建立下载器更新APK外，我们也可以使用手机自带的下载器下载APK，这里我放入了一个Demo,当然也可以直接拿过来使用

1、在Service中调用
  使用Service可以看到Demo里放入了我们所有的操作，这里也不需要我们建立线程，也无需初始化通知栏，用的全部是系统的
2、DownLoadManager 先看下面的代码

```java
    
        DownloadManager.Request mRequest = new DownloadManager.Request(Uri.parse(loadApkPath));
        //传入  文件存储地址，也可以自定义存储位置
        mRequest.setDestinationInExternalPublicDir("/download/", fileName);
        //允许使用什么样的网络下载线程，这里限制WIFI下下载
        mRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        //下载器为没个下载任务分配一个id taskId 这个后面还要用
        long taskId = mDownloadManager.enqueue(mRequest);
  
```
  下载器建议在serVice中调用，但下载部分，瞧，就几行代码，是不是很简单
3、获取下载状态

```java
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
 
```
  除了下载完成的状态对我们有用，其它基本可以忽略，当然要是有特殊需要，请忽略
  下载完成之后我这里选择自动安装，不会有其它癖好吧.....
4、安装APK
```java
    private void installApk() {
        String downPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + fileName;
        File file = new File(downPath);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        //结束掉当前Service，请视情况而定
        stopSelf();
    }
```

  最后，使用系统的下载器虽然简单，但有不少确定比方说检查版本状况还需要我们自己来，而且处理不好，会出现重复下载，浪费流量的情况，so,我在Demo里
放入了另外一个moudle UpdateApkFromThread，本来要写一个Demo展示如何使用，但是发现手里目前没有合适的接口来展示它的使用，只好先放在这里了，后面
有机会再补充
