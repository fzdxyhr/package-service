package com.ruijie.packageservice.service.impl;

import com.google.common.collect.Ordering;
import com.ruijie.packageservice.constant.CacheHelper;
import com.ruijie.packageservice.constant.CommonContant;
import com.ruijie.packageservice.constant.PackageType;
import com.ruijie.packageservice.service.PackageService;
import com.ruijie.packageservice.shell.ShellCall;
import com.ruijie.packageservice.vo.FileVo;
import com.ruijie.packageservice.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.SimpleFormatter;

/**
 * @author yhr
 * @version latest
 * @date 2018/1/18
 */

@Service
@Slf4j
public class PackageServiceImpl implements PackageService {

    public static final Integer LOG_SIZE = 300;

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
    public List<FileVo> listFiles(Integer packageType) {
        List<FileVo> result = new ArrayList<FileVo>();
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
        return result;
    }

    @Override
    public String readLogs() {
        try {//D:\installsoftware\apache-tomcat-7.0.65-windows-x64\apache-tomcat-7.0.65\logs
            File logFile = new File(CommonContant.LOG_PATH + "/catalina.out");
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
}
