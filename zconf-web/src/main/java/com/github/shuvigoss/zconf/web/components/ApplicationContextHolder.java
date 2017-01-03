package com.github.shuvigoss.zconf.web.components;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
@Component
public class ApplicationContextHolder implements ApplicationContextAware {
  private static ApplicationContext ctx = null;

  @Override
  public void setApplicationContext(
      ApplicationContext applicationContext) throws BeansException {
    ctx = applicationContext;
  }

  public static ApplicationContext get() {
    return ctx;
  }

  public static String getProperties(String key) {
    return env().getProperty(key);
  }

  public static Environment env() {
    return ctx.getEnvironment();
  }
}
