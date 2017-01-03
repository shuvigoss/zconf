package com.github.shuvigoss.zconf.util;

import com.google.common.base.Throwables;

import java.lang.reflect.Constructor;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public abstract class ReflectionUtil {

  public static <T> T create(String c) {
    try {
      Class<?> aClass = Class.forName(c);
      Constructor<?> constructor = aClass.getConstructor();
      return (T) constructor.newInstance();
    } catch (Exception e) {
      Throwables.propagate(e);
    }
    return null;
  }

}
