package com.ruijie.packageservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yhr
 * @version latest
 * @date 2018/1/17
 */
@RestController
@RequestMapping("/v1")
public class PackageController {

    @RequestMapping(value = "/package_start",method = RequestMethod.GET)
    public String packageStart(String svnUrl, String build, String version) {
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
        return "success";
    }
}
