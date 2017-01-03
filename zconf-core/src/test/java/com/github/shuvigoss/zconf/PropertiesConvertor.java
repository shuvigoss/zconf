package com.github.shuvigoss.zconf;

import com.github.shuvigoss.zconf.base.Convertible;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class PropertiesConvertor implements Convertible.ValueConvertor<Properties> {
  @Override
  public Properties convert(String value) {
    Properties p = new Properties();
    try {
      p.load(new ByteArrayInputStream(value.getBytes(UTF_8)));
    } catch (IOException ignored) {

    }
    return p;
  }
}
