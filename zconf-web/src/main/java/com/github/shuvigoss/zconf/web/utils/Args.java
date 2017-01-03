package com.github.shuvigoss.zconf.web.utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public abstract class Args {

  public static void isTrue(boolean expression, String message) {
    if (!expression)
      t(message);
  }

  public static void isFalse(boolean expression, String message) {
    if (expression)
      t(message);
  }

  public static void notNull(Object object, String message) {
    if (object == null)
      t(message);
  }

  public static void notNullNotEmpty(Object object, String message) {
    notNull(object, message);
    if (object instanceof CharSequence && StringUtils.isEmpty((String) object))
      t(message);
    if (object instanceof Collection && ((Collection) object).isEmpty())
      t(message);
    if (object instanceof Map && ((Map) object).isEmpty())
      t(message);
    if (object.getClass().isArray() && Array.getLength(object) == 0)
      t(message);
  }

  private static void t(String message) {
    throw new IllegalArgumentException(message);
  }

}
