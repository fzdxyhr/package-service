package com.ruijie.packageservice.controller;

import com.ruijie.packageservice.service.PackageService;
import com.ruijie.packageservice.vo.FileVo;
import com.ruijie.packageservice.vo.PackageVo;
import com.ruijie.packageservice.vo.PagerInfo;
import com.ruijie.packageservice.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @author yhr
 * @version latest
 * @date 2018/1/17
 */
@RestController
@RequestMapping("/v1")
@Slf4j
public class PackageController {

    Logger logger = LogManager.getLogger(PackageController.class);

    @Autowired
    private PackageService packageService;
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/package_start", method = RequestMethod.POST)
    public String packageStart(@RequestBody PackageVo packageVo) {
        return packageService.packageStart(packageVo.getSvnUrl(), packageVo.getBuild(), packageVo.getVersion(), packageVo.getPackageType());
    }

    @RequestMapping(value = "/files/{package_type}", method = RequestMethod.GET)
    public PagerInfo<FileVo> listFiles(@PathVariable("package_type") Integer packageType
            , @RequestParam(value = "page_no", required = true, defaultValue = "1") Integer pageNo
            , @RequestParam(value = "page_size", required = true, defaultValue = "10") Integer pageSize) {
        return packageService.listFiles(packageType, pageNo, pageSize);
    }

    @RequestMapping(value = "/logs", method = RequestMethod.GET)
    public String readLogs() {
        return packageService.readLogs();
    }

    @RequestMapping(value = "/result", method = RequestMethod.GET)
    public ResultVo getResult() {
        return packageService.getResult();
    }

    @RequestMapping(value = "/files/download", method = RequestMethod.GET)
    public void download(@RequestParam("path") String path, HttpServletResponse response) throws Exception {
        File configFile = new File(path);
        if (!configFile.exists()) {
            log.error(configFile.getName() + " is not exist.");
            throw new Exception(configFile.getName() + "文件不存在");
        }
        // 读到流中
        InputStream inStream = new FileInputStream(path);// 文件的存放路径
        // 设置输出的格式
        response.reset();
        String headStr = "attachment; filename=\"" + URLEncoder.encode(configFile.getName(), "utf-8") + "\"";//编码用于解决中文乱码
        response.setContentType("APPLICATION/OCTET-STREAM");
        response.setHeader("Content-Disposition", headStr);
        // 循环取出流中的数据
        byte[] b = new byte[100];
        int len;
        try {
            while ((len = inStream.read(b)) > 0)
                response.getOutputStream().write(b, 0, len);
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/files/delete", method = RequestMethod.DELETE)
    public boolean delete(@RequestParam("path") String path) throws Exception {
        File configFile = new File(path);
        if (!configFile.exists()) {
            log.error(configFile.getName() + " is not exist.");
            return false;
        }
        return configFile.delete();
    }

    @RequestMapping(value = "/files/{package_type}/clear_all", method = RequestMethod.DELETE)
    public boolean clear(@PathVariable("package_type") Integer packageType) {
        return packageService.clearAll(packageType);
    }

    @RequestMapping(value = "/types/{package_type}/files/{file_name}/upload", method = RequestMethod.GET)
    public int clear(@PathVariable("package_type") Integer packageType, @PathVariable("file_name") String fileName) {
        return packageService.upload(fileName, packageType);
    }

    @RequestMapping(value = "/files/{file_name}/progress", method = RequestMethod.GET)
    public int getProgress(@PathVariable("file_name") String fileName) {
        return packageService.getUploadProgress(fileName);
    }


    @RequestMapping(value = "/test3", method = RequestMethod.GET)
    public String test3() throws Exception {
        FTPClient ftpClient = new FTPClient();
        String host = "192.168.5.184";
        String remotePath = "/data/onc-ads/";
        String localPath = "D:\\opt\\ftp\\";
        ftpClient.connect(host);
        FileInputStream fis = null;
        boolean loginResult = ftpClient.login("onc-ads", "ads@RIM7");
        int returnCode = ftpClient.getReplyCode();
        if (loginResult && FTPReply.isPositiveCompletion(returnCode)) {// 如果登录成功
            System.out.println("登录FTP服务器成功");
            ftpClient.makeDirectory(remotePath);
            // 设置上传目录
            ftpClient.changeWorkingDirectory(remotePath);
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.enterLocalPassiveMode();
            File file = new File(localPath + "my_test.txt");
            fis = new FileInputStream(file);
//            ftpClient.storeFile("my_test.txt", fis);
            long originFileLenth = file.length();
            System.out.println("size = " + originFileLenth);
            long progress = 0;
            byte[] bytes = new byte[1024];
            int length = 0;
            long sum = 0;
            OutputStream os = ftpClient.storeFileStream(remotePath + "my_test4.txt");
            while ((length = fis.read(bytes)) != -1) {
                sum += length;
                progress = sum / originFileLenth;
                os.write(bytes);
                System.out.println("progress=" + progress);
            }
            os.close();
            fis.close();
            ftpClient.disconnect();

        }
        return "success";
    }
}
