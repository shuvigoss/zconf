package com.github.shuvigoss.zconf.web.controller.base;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class ZConfDataRequest {
  @NotEmpty
  private String rootPath;
  @NotEmpty
  @Pattern(regexp = "[\\d\\w_-]+")
  private String key;
  private String convertId;
  @NotEmpty
  private String data;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getRootPath() {
    return rootPath;
  }

  public void setRootPath(String rootPath) {
    this.rootPath = rootPath;
  }

  public String getConvertId() {
    return convertId;
  }

  public void setConvertId(String convertId) {
    this.convertId = convertId;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

}
