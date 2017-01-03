package com.github.shuvigoss.zconf.base;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public interface ZConfigure {

  /**
   * get key from rootPath
   *
   * @param key config key
   *
   * @return config value
   */
  String get(String key);

}
