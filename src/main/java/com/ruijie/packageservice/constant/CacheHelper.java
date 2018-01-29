package com.ruijie.packageservice.constant;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author yhr
 * @version latest
 * @date 2018/1/24
 */
public class CacheHelper {

    public static final String INSTALL_RESULT_KEY = "install_result_key";

    public static final String UPGRADE_RESULT_KEY = "upgrade_result_key";

    public static Cache cacheHelp = CacheBuilder.newBuilder().initialCapacity(10).expireAfterWrite(1, TimeUnit.HOURS).build();

}
