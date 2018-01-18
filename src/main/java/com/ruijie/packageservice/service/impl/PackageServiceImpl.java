package com.ruijie.packageservice.service.impl;

import com.ruijie.packageservice.constant.CommonContant;
import com.ruijie.packageservice.constant.PackageType;
import com.ruijie.packageservice.service.PackageService;
import com.ruijie.packageservice.vo.FileVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yhr
 * @version latest
 * @date 2018/1/18
 */

@Service
@Slf4j
public class PackageServiceImpl implements PackageService {


    @Override
    public String packageStart(String svnUrl, String build, String version) {

        return null;
    }

    @Override
    public List<FileVo> listFiles(Integer packageType) {
        List<FileVo> result = new ArrayList<FileVo>();
        try {
            File rootFile = null;
            if(PackageType.INSTALL.getValue() == packageType) {
                rootFile = new File(CommonContant.FILE_INSTALL_PATH);
            }
            if(PackageType.UPGRADE.getValue() == packageType) {
                rootFile = new File(CommonContant.FILE_UPGRADE_PATH);
            }
            if (!rootFile.isDirectory()) {
                log.error("指定的路径不是个文件夹");
            } else if (rootFile.isDirectory()) {
                File[] fileList = rootFile.listFiles();
                for (File file : fileList) {
                    FileVo fileVo = new FileVo();
                    fileVo.setFileName(file.getName());
                    fileVo.setFilePath(file.getPath());
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
        return null;
    }
}
