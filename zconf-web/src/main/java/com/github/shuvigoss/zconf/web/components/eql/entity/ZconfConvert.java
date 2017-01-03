package com.github.shuvigoss.zconf.web.components.eql.entity;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author shuvigoss@gmail.com (Wei Shu)
 */
public class ZconfConvert {
  private Long   id;
  @NotEmpty
  private String rootPath;
  @NotEmpty
  private String value;
  private String description;

  public String getRootPath() {
    return rootPath;
  }

  public void setRootPath(String rootPath) {
    this.rootPath = rootPath;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
