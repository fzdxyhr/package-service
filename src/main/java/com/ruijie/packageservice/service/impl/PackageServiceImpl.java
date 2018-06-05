package com.ruijie.packageservice.service.impl;

import com.ruijie.packageservice.constant.CacheHelper;
import com.ruijie.packageservice.constant.CommonContant;
import com.ruijie.packageservice.constant.PackageType;
import com.ruijie.packageservice.service.PackageService;
import com.ruijie.packageservice.shell.ShellCall;
import com.ruijie.packageservice.thread.UploadThread;
import com.ruijie.packageservice.vo.FileVo;
import com.ruijie.packageservice.vo.PagerInfo;
import com.ruijie.packageservice.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author yhr
 * @version latest
 * @date 2018/1/18
 */

@Service
@Slf4j
public class PackageServiceImpl implements PackageService {

    public static final Integer LOG_SIZE = 300;

    @Value("${ftp.username}")
    private String ftpUserName;
    @Value("${ftp.password}")
    private String ftpPassword;
    @Value("${ftp.host}")
    private String ftpHost;
    @Value("${ftp.file.path}")
    private String ftpFilePath;
    @Value("${local.file.path}")
    private String localFilePath;

    public SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String packageStart(final String svnUrl, final String build, final String version, List<Integer> packageTypes) {
        if (!CollectionUtils.isEmpty(packageTypes)) {
            for (Integer packageType : packageTypes) {
                if (PackageType.INSTALL.getValue() == packageType) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<String> params = new ArrayList<String>();
                            //虚拟ip放在最前面作为sh脚本的参数，具体传值可以查看对应的脚本注释
                            params.add(svnUrl);
                            params.add(build);
                            params.add(version);
                            log.info("make_install.sh execute result is staring");
                            log.info("make_install.sh param is" + params);
                            int returnResult = ShellCall.callScript(ShellCall.COMMON_SHELL_PATH, "make_install.sh", params);
                            log.info("make_install.sh execute result is " + returnResult);
                            if (CacheHelper.cacheHelp.asMap().get(CacheHelper.INSTALL_RESULT_KEY) == null) {
                                CacheHelper.cacheHelp.asMap().put(CacheHelper.INSTALL_RESULT_KEY, returnResult);
                            }
                        }
                    }).start();
                }
                if (PackageType.UPGRADE.getValue() == packageType) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<String> params = new ArrayList<String>();
                            //虚拟ip放在最前面作为sh脚本的参数，具体传值可以查看对应的脚本注释
                            params.add(svnUrl);
                            params.add(build);
                            params.add(version);
                            int returnResult = ShellCall.callScript(ShellCall.COMMON_SHELL_PATH, "make_update.sh", params);
                            log.info("make_install.sh execute result is " + returnResult);
                            if (CacheHelper.cacheHelp.asMap().get(CacheHelper.UPGRADE_RESULT_KEY) == null) {
                                CacheHelper.cacheHelp.asMap().put(CacheHelper.UPGRADE_RESULT_KEY, returnResult);
                            }
                        }
                    }).start();
                }
            }
        }
        return "success";
    }

    @Override
    public PagerInfo<FileVo> listFiles(Integer packageType, Integer pageNo, Integer pageSize) {
        List<FileVo> result = new ArrayList<FileVo>();
        PagerInfo<FileVo> pagerInfo = new PagerInfo<FileVo>();
        try {
            File rootFile = null;
            if (PackageType.INSTALL.getValue() == packageType) {
                rootFile = new File(CommonContant.FILE_INSTALL_PATH);
            }
            if (PackageType.UPGRADE.getValue() == packageType) {
                rootFile = new File(CommonContant.FILE_UPGRADE_PATH);
            }
            if (PackageType.ROM.getValue() == packageType) {
                rootFile = new File(CommonContant.ROM_FILE_UPGRADE_PATH);
            }
            if (!rootFile.isDirectory()) {
                log.error("指定的路径不是个文件夹");
            } else if (rootFile.isDirectory()) {
                File[] fileList = rootFile.listFiles();
                Arrays.sort(fileList, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        return new Long(o2.lastModified()).intValue() - new Long(o1.lastModified()).intValue();
                    }
                });
                for (File file : fileList) {
                    FileVo fileVo = new FileVo();
                    fileVo.setFileName(file.getName());
                    fileVo.setFileDate(simpleFormatter.format(new Date(file.lastModified())));
                    fileVo.setFilePath(file.getPath());
                    fileVo.setFileSize(file.length() / 1024 / 1024);
                    result.add(fileVo);
                }
            }
        } catch (Exception e) {
            log.error("readfile()   Exception:" + e.getMessage(), e);
        }
        pagerInfo.setTotal(result.size());
        if (CollectionUtils.isEmpty(result)) {
            pagerInfo.setItems(result);
            return pagerInfo;
        }
        int startIndex = (pageNo - 1) * pageSize;
        int pages = 0;
        if (result.size() % pageSize == 0) {
            pages = result.size() / pageSize;
        } else {
            pages = result.size() / pageSize + 1;
        }
        if (pageNo == pages) {
            result = result.subList(startIndex, result.size());
        } else {
            result = result.subList(startIndex, startIndex + pageSize);
        }
        pagerInfo.setItems(result);
        return pagerInfo;
    }

    @Override
    public String readLogs() {
        try {//D:\installsoftware\apache-tomcat-7.0.65-windows-x64\apache-tomcat-7.0.65\logs
            File logFile = new File(CommonContant.LOG_PATH);
//            File logFile = new File("D:\\installsoftware\\apache-tomcat-7.0.65-windows-x64\\apache-tomcat-7.0.65\\logs\\catalina.out");
            StringBuffer result = new StringBuffer();
            if (logFile.exists()) {
                //读取文件信息
                InputStream is = new FileInputStream(logFile);
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String readLine;
                int count = 1;
                while ((readLine = br.readLine()) != null) {
                    result.append(readLine + "\r\n");
                    if (count > LOG_SIZE) {
                        break;
                    }
                    count++;
                }
                return result.toString();
            }
        } catch (Exception ex) {
            log.error("", ex);
        }
        return null;
    }

    @Override
    public ResultVo getResult() {
        ResultVo resultVo = new ResultVo();
        resultVo.setInstallResult(CacheHelper.cacheHelp.asMap().get(CacheHelper.INSTALL_RESULT_KEY) == null ? 0 : Integer.parseInt(CacheHelper.cacheHelp.asMap().get(CacheHelper.INSTALL_RESULT_KEY).toString()));
        resultVo.setUpgradeResult(CacheHelper.cacheHelp.asMap().get(CacheHelper.UPGRADE_RESULT_KEY) == null ? 0 : Integer.parseInt(CacheHelper.cacheHelp.asMap().get(CacheHelper.UPGRADE_RESULT_KEY).toString()));
        return resultVo;
    }

    @Override
    public boolean clearAll(Integer packageType) {
        try {
            File rootFile = null;
            if (PackageType.INSTALL.getValue() == packageType) {
                rootFile = new File(CommonContant.FILE_INSTALL_PATH);
            }
            if (PackageType.UPGRADE.getValue() == packageType) {
                rootFile = new File(CommonContant.FILE_UPGRADE_PATH);
            }
            if (!rootFile.isDirectory()) {
                log.error("指定的路径不是个文件夹");
                return false;
            } else if (rootFile.isDirectory()) {
                File[] fileList = rootFile.listFiles();
                for (File file : fileList) {
                    if (file.exists()) {
                        file.delete();
                    }
                }
                return true;
            }
        } catch (Exception e) {
            log.error("readfile()   Exception:" + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public int upload(String fileName, Integer packageType) {
        FTPClient ftpClient = new FTPClient();
        File file = null;
        FileInputStream fis = null;
        OutputStream os = null;
        try {
            ftpClient.connect(ftpHost);
            boolean loginResult = ftpClient.login("onc-ads", "ads@RIM7");
            int returnCode = ftpClient.getReplyCode();
            if (loginResult && FTPReply.isPositiveCompletion(returnCode)) {// 如果登录成功
                if (PackageType.INSTALL.getValue() == packageType) {
                    file = new File(CommonContant.FILE_INSTALL_PATH + "/" + fileName);
                }
                if (PackageType.UPGRADE.getValue() == packageType) {
                    file = new File(CommonContant.FILE_UPGRADE_PATH + "/" + fileName);
                }
                ftpClient.makeDirectory(ftpFilePath);
                // 设置上传目录
                ftpClient.changeWorkingDirectory(ftpFilePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.enterLocalPassiveMode();
                fis = new FileInputStream(file);
                os = ftpClient.storeFileStream(ftpFilePath + fileName);
                long originFileLength = file.length();
                UploadThread uploadThread = new UploadThread(ftpClient, os, fis, originFileLength, fileName);
                uploadThread.start();
            }
        } catch (Exception ex) {

        } finally {

        }
        return 0;
    }

    @Override
    public int getUploadProgress(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return 0;
        }
        String key = fileName.split("\\.")[0];
        int progress = (int) CacheHelper.cacheHelp.asMap().get(key);
        return progress;
    }
}
