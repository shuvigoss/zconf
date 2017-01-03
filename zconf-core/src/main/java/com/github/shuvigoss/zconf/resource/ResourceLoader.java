package com.github.shuvigoss.zconf.resource;

import com.google.common.base.Throwables;
import com.google.common.io.Closeables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public abstract class ResourceLoader {
  private final static Logger log = LoggerFactory.getLogger(ResourceLoader.class);

  private static final Properties p = new Properties();

  static {
    init();
  }

  private static void init() {
    InputStream input = null;
    try {
      input = ResourceLoader.class
          .getClassLoader().getResourceAsStream("zconf.properties");
      p.load(input);
    } catch (IOException e) {
      log.error("load zconf.properties error!", e);
      Throwables.propagate(e);
    } finally {
      if (input != null)
        Closeables.closeQuietly(input);
    }
  }

  public static Properties get() {return p;}
}
