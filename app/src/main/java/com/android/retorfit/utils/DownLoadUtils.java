package com.android.retorfit.utils;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownLoadUtils {
    public void downloadPdf(final  String pdfUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(pdfUrl);
                    HttpURLConnection urlConn = (HttpURLConnection) url
                            .openConnection();
                    BufferedInputStream bis = new BufferedInputStream(urlConn
                            .getInputStream());
                    File appCacheDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/pdf");
                    if (!appCacheDir.exists()) {
                        appCacheDir.mkdirs();
                    }
                    String path=Environment.getExternalStorageDirectory().getAbsolutePath() + "/pdf/ymt_" + System.currentTimeMillis() + ".pdf";
                    FileOutputStream fos = new FileOutputStream(path);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    byte[] buf = new byte[3 * 1024];
                    int result = bis.read(buf);
                    while (result != -1) {
                        bos.write(buf, 0, result);
                        result = bis.read(buf);
                    }
                    bos.flush();
                    bis.close();
                    fos.close();
                    bos.close();
                 //   showPdf(new File(path));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
