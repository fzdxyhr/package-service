package com.ruijie.packageservice.thread;

import com.ruijie.packageservice.constant.CacheHelper;
import org.apache.commons.net.ftp.FTPClient;

import java.io.FileInputStream;
import java.io.OutputStream;

/**
 * Created by YHR on 2018/6/5.
 */
public class UploadThread extends Thread {

    private FTPClient ftpClient;
    private OutputStream os;
    private FileInputStream fis;
    private long originFileLength;
    private String fileName;

    public UploadThread(FTPClient ftpClient, OutputStream os, FileInputStream fis
            , long originFileLength, String fileName) {
        this.ftpClient = ftpClient;
        this.os = os;
        this.fis = fis;
        this.originFileLength = originFileLength;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        String key = fileName.split("\\.")[0];
        try {
            byte[] bytes = new byte[1024];
            int length = 0;
            long sum = 0;
            while ((length = fis.read(bytes)) != -1) {
                sum += length;
                os.write(bytes);
                CacheHelper.cacheHelp.asMap().put(key, sum / originFileLength);
            }
        } catch (Exception ex) {

        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception ex) {
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception ex) {
                }
            }
            if (ftpClient != null) {
                try {
                    ftpClient.disconnect();
                } catch (Exception ex) {
                }
            }
        }
    }
}
