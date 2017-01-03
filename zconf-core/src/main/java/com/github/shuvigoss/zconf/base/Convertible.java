package com.github.shuvigoss.zconf.base;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public interface Convertible {
  /**
   * get converted value by registConvertor(String key, ValueConvertor<V> convertor)
   *
   * @param key config key
   * @param <V> return type result
   *
   * @return converted value
   */
  <V> V getC(String key);


  interface ValueConvertor<T> {
    T convert(String value);
  }
}
