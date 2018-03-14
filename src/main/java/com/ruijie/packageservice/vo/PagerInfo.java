package com.ruijie.packageservice.vo;

import lombok.Data;

import java.util.Collection;

/**
 * @author yhr
 * @version latest
 * @date 2018/3/14
 */

@Data
public class PagerInfo<T> {

    private Integer total;

    private Collection<T> items;
}
