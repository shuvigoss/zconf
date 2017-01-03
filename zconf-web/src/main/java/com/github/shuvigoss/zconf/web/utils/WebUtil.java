package com.github.shuvigoss.zconf.web.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public abstract class WebUtil {

  public static HttpServletRequest request() {
    return ((ServletRequestAttributes) RequestContextHolder
        .getRequestAttributes()).getRequest();
  }

  public static HttpServletResponse response() {
    return ((ServletRequestAttributes) RequestContextHolder
        .getRequestAttributes()).getResponse();
  }
}
