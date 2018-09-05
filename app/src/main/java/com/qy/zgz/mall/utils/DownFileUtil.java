package com.qy.zgz.mall.utils;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

import com.qy.zgz.mall.network.NetworkClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 文件下载
 */
public class DownFileUtil {
    private OkHttpClient mOkHttpClient;
    private List<String> fileList;
    private int fileIndex = 0;
    private Handler handler=new Handler();

    public DownFileUtil(List<String> fileList) {
        mOkHttpClient = NetworkClient.getInstance().okHttpClient;
        this.fileList = fileList;
        File path = new File(FileManager.getInstance().getDestFileDir());
        if (!path.exists()) {
            path.mkdirs();
        }
    }

    public void startDown() {
        if (fileIndex >= fileList.size()) {
            System.out.println("下载完成");
            return;
        }
        String fileName = FileManager.getInstance().getFileName(fileList.get(fileIndex));
        if (fileName.equals("")) {
            fileIndex++;
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startDown();
                }
            }.start();

            return;
        }
        File file = new File(FileManager.getInstance().getDestFileDir(), fileName);
        if (file.exists()) {
            boolean isDownSuccess = (boolean) SharePerferenceUtil.getInstance().getValue(fileName, false);
            if (isDownSuccess) {
                fileIndex++;
                startDown();
                return;
            } else {
                System.out.println("没有成功，删除重下" + fileName);
                file.delete();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("下载中" + fileIndex);
        final Request request = new Request.Builder().url(fileList.get(fileIndex)).build();
        Call downCall = mOkHttpClient.newCall(request);
        downCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (file.exists()) {
                    System.out.println("下载失败" + fileName);
                    file.delete();
                }
                fileIndex++;
                startDown();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    double current = 0;
                    double total = response.body().contentLength();

                    is = response.body().byteStream();

                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    fileIndex++;
                    startDown();
                    SharePerferenceUtil.getInstance().setValue(fileName, true);
                } catch (IOException e) {

                    System.out.println("下载失败" + fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    e.printStackTrace();

                    fileIndex++;
                    startDown();
                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }


    public void downApk(String url, int version, Activity activity) {
        File path = new File(FileManager.getInstance().getDownFileDir());
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(FileManager.getInstance().getDownFileDir(), "newapp" + version + ".apk");
        if (file.exists()) {
            file.delete();
        }

        final Request request = new Request.Builder().url(url).build();
        Call downCall = mOkHttpClient.newCall(request);
        downCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(activity,"更新失败", Toast.LENGTH_SHORT).show();
                if (file.exists()) {
                    file.delete();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    double current = 0;
                    double total = response.body().contentLength();

                    is = response.body().byteStream();

                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    //如果下载文件成功，第一个参数为文件的绝对路径
                    Runnable runnable=new Runnable() {
                        @Override
                        public void run() {
                            AntoUtil.setUrl(FileManager.getInstance().getDownFileDir() + "newapp" + version + ".apk");
                            AntoUtil.install(activity);
                        }
                    };
                    SharePerferenceUtil.getInstance().setValue("newapp" + version, true);
                    handler.postDelayed(runnable,0);
                } catch (IOException e) {
                    Toast.makeText(activity,"更新失败", Toast.LENGTH_SHORT).show();
                    if (file.exists()) {
                        file.delete();
                    }
                    e.printStackTrace();

                } finally {
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fos != null) fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }



}
