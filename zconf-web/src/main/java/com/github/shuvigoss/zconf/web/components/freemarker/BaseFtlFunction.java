package com.github.shuvigoss.zconf.web.components.freemarker;

import com.alibaba.fastjson.JSON;

import static com.github.shuvigoss.zconf.web.components.ApplicationContextHolder.getProperties;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
@FtlFunction(prefix = "simple")
public class BaseFtlFunction {

  public static String get(String key) {
    return getProperties(key);
  }

  public static String json(Object obj) {
    return JSON.toJSONString(obj);
  }
}
