package com.ruijie.packageservice.controller;

import com.ruijie.packageservice.service.PackageService;
import com.ruijie.packageservice.vo.FileVo;
import com.ruijie.packageservice.vo.PackageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author yhr
 * @version latest
 * @date 2018/1/17
 */
@RestController
@RequestMapping("/v1")
@Slf4j
public class PackageController {

    @Autowired
    private PackageService packageService;

    @RequestMapping(value = "/package_start", method = RequestMethod.GET)
    public String packageStart(@RequestBody PackageVo packageVo) {
        return packageService.packageStart(packageVo.getSvnUrl(), packageVo.getBuild(), packageVo.getVersion(), packageVo.getPackageType());
    }

    @RequestMapping(value = "/files/{package_type}", method = RequestMethod.GET)
    public List<FileVo> listFiles(@PathVariable("package_type") Integer packageType) {
        return packageService.listFiles(packageType);
    }

    @RequestMapping(value = "/logs", method = RequestMethod.GET)
    public String readLogs() {
        return packageService.readLogs();
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
}
