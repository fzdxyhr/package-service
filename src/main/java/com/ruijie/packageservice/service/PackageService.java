package com.ruijie.packageservice.service;

import com.ruijie.packageservice.vo.FileVo;
import com.ruijie.packageservice.vo.PagerInfo;
import com.ruijie.packageservice.vo.ResultVo;

import java.util.List;

/**
 * @author yhr
 * @version latest
 * @date 2018/1/18
 */
public interface PackageService {

    String packageStart(String svnUrl, String build, String version, List<Integer> packageTypes);

    PagerInfo<FileVo> listFiles(Integer packageType, Integer pageNo, Integer pageSize);

    String readLogs();

    ResultVo getResult();

    boolean clearAll(Integer packageType);

}
