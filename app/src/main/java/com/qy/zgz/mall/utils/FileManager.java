package com.qy.zgz.mall.utils;

import android.os.Environment;

import java.io.File;

/**
 * 文件管理
 */
public class FileManager {
    private static FileManager fileManager;
    private String destFileDir= Environment.getExternalStorageDirectory().getPath() +"/together/";
    private String downFileDir=Environment.getExternalStorageDirectory().getPath() + "/Download/together/";
    public static FileManager getInstance()
    {
        if(fileManager==null)
        {
            synchronized (FileManager.class)
            {
                if(fileManager==null)
                {
                    fileManager=new FileManager();
                }
            }
        }
        return fileManager;
    }

    /**
     * 获取文件夹路径
     * @return
     */
    public String getDestFileDir()
    {
        return destFileDir;
    }

    /**
     * 获取文件名
     * @param url
     * @return
     */
    public String getFileName(String url)
    {
        if(url==null)
        {
            return "";
        }
        int lastIndex=url.lastIndexOf("/");
        String fileName=url.substring(lastIndex+1,url.length()).toLowerCase();
        if(fileName.indexOf(".png")==-1 && fileName.indexOf(".jpg")==-1 && fileName.indexOf("jpeg")==-1
                && fileName.indexOf(".mp4")==-1 && fileName.indexOf("gif")==-1)
        {
            int timeLastIndex=fileName.lastIndexOf("type=");
            if(timeLastIndex!=-1)
            {
                fileName="qcode"+fileName.substring(timeLastIndex+5,fileName.length()) +".png";
                fileName=fileName.replace("&","_");
                fileName=fileName.replace("=","");
            }
            else
            {
                fileName="";
            }
        }
        return fileName;
    }

    /**
     * 判断文件是否存在
     * @param fileName
     * @return
     */
    public boolean isFileExists(String fileName)
    {
        File file = new File(destFileDir, fileName);
        if(file.exists())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * 递归删除文件夹
     */
    public void deleteSvaeFile(){
        File file=new File(destFileDir);
        if(file.isDirectory()){
            File[] childFiles = file.listFiles();
            for(File childFile:childFiles)
            {
                childFile.delete();
            }
        }
    }

    /**
     * 递归删除APK文件夹
     */
    public void deleteAPKSvaeFile(){
        File file=new File(downFileDir);
        if(file.isDirectory()){
            File[] childFiles = file.listFiles();
            for(File childFile:childFiles)
            {
                childFile.delete();
            }
        }
    }

    public String getDownFileDir() {
        return downFileDir;
    }

    public void setDownFileDir(String downFileDir) {
        this.downFileDir = downFileDir;
    }
}

